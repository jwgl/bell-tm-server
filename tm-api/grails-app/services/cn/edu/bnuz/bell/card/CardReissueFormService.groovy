package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.organization.Student
import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.security.UserLogService
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.AuditAction
import cn.edu.bnuz.bell.workflow.CommitCommand
import cn.edu.bnuz.bell.workflow.WorkflowService
import grails.transaction.Transactional

@Transactional
class CardReissueFormService {
    UserLogService userLogService
    WorkflowService workflowService

    /**
     * 查找学生所有申请
     * @param studentId
     * @return 申请列表
     */
    def getAll(String studentId) {
        CardReissueForm.findAllByStudent(Student.load(studentId));
    }

    private getStudent(String studentId) {
        def results = Student.executeQuery '''
select new map(
  student.id as id,
  student.name as name,
  cast(student.birthday as string) as birthday,
  admission.fromProvince as province,
  department.name as department,
  subject.name as subject,
  '本科' as educationLevel)
from Student student
join student.admission admission
join student.department department
join student.major m
join m.subject subject
where student.id = :studentId
''', [studentId: studentId]
        if (!results) {
            throw new NotFoundException()
        }
        return results[0]
    }

    def getFormForShow(String studentId, Long id) {
        def results = CardReissueForm.executeQuery '''
select new map(
  form.id as id,
  form.reason as reason,
  form.status as status,
  form.workflowInstance.id as workflowInstanceId
 )
from CardReissueForm form
where form.id = :id and form.student.id = :studentId
''', [id: id, studentId: studentId]
        if (!results) {
            throw new NotFoundException()
        }

        def form = results[0]

        return [
                id: form.id,
                reason: form.reason,
                workflowInstanceId: form.workflowInstanceId,
                editable: form.status.allow(AuditAction.UPDATE),
                student: getStudent(studentId),
        ]
    }

    def getFormForCreate(String studentId) {
        return [student: getStudent(studentId)]
    }

    CardReissueForm create(String studentId, String reason) {
        def student = Student.load(studentId)
        def forms = CardReissueForm.findAllByStudent(student)

        if (forms.size() >= 2) {
            throw new BadRequestException('申请次数已经超过2次。')
        } else if (forms.size() > 0) {
            def unfinished = forms.find {it.status != CardReissueStatus.FINISHED}
            if (unfinished) {
                throw new BadRequestException('存在未完成的申请。')
            }
        }

        CardReissueForm form = new CardReissueForm(
                student: student,
                reason: reason,
                status: CardReissueStatus.CREATED,
                dateCreated: new Date(),
                dateModified: new Date(),
        )
        form.save()

        userLogService.log(AuditAction.CREATE, form)

        return form
    }

    def getFormForEdit(String studentId, Long id) {
        def dto = getFormForShow(studentId, id)
        if (!dto.editable) {
            throw new BadRequestException()
        } else {
            return dto
        }
    }

    CardReissueForm update(String studentId, Long id, String reason) {
        CardReissueForm form = CardReissueForm.get(id)

        if (!form) {
            throw new NotFoundException()
        }

        if (form.student.id != studentId) {
            throw new ForbiddenException()
        }

        if (!form.status.allow(AuditAction.UPDATE)) {
            throw new BadRequestException()
        }

        form.reason = reason
        form.dateModified = new Date()
        form.save()

        userLogService.log(AuditAction.UPDATE, form)

        return form
    }

    void delete(String studentId, Long id) {
        CardReissueForm form = CardReissueForm.get(id)

        if (!form) {
            throw new NotFoundException()
        }

        if (form.student.id != studentId) {
            throw new ForbiddenException()
        }

        if (!form.status.allow(AuditAction.DELETE)) {
            throw new BadRequestException()
        }

        userLogService.log(AuditAction.DELETE, form)

        if(form.workflowInstance) {
            form.workflowInstance.delete()
        }

        form.delete()
    }

    void commit(CommitCommand cmd, String userId) {
        CardReissueForm form = CardReissueForm.get(cmd.id)

        if (!form) {
            throw new NotFoundException()
        }

        if (form.student.id != userId) {

            throw new ForbiddenException()
        }

        if (!form.status.allow(AuditAction.COMMIT)) {
            throw new BadRequestException()
        }

        def action = AuditAction.COMMIT
        if (form.status == CardReissueStatus.REJECTED) {
            workflowService.setProcessed(form.workflowInstance, userId)
        } else {
            form.workflowInstance = workflowService.createInstance('card.reissue', cmd.title, cmd.id)
        }
        workflowService.createWorkItem(form.workflowInstance, Activities.CHECK, userId, action, cmd.comment, cmd.to)

        form.status = form.status.next(action)
        form.save()

        userLogService.log(action, form)
    }

    def getCheckers(Long id) {
        User.findAllWithPermission('PERM_CARD_REISSUE_CHECK')
    }
}
