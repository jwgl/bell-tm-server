package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.workflow.*
import grails.transaction.Transactional

@Transactional
class CardReissueAdminService {
    CardReissueFormService cardReissueFormService
    WorkflowService workflowService

    /**
     * 各状态申请数量
     * @return 各状态申请数量
     */
    def getCountsByStatus() {
        def results = CardReissueForm.executeQuery("""
select status, count(*)
from CardReissueForm
group by status
""")
        return results.collectEntries {[it[0], it[1]]}
    }

    /**
     * 查找所有指定状态的申请（DTO）
     * @param status
     * @param offset
     * @param max
     * @return
     */
    def findAllByStatus(CardReissueStatus status, int offset, int max) {
        CardReissueForm.executeQuery """
select new map(
  form.id as id,
  student.id as studentId,
  student.name as name,
  student.sex as sex,
  department.name as department,
  subject.name as subject,
  form.dateModified as applyDate,
  form.status as status,
  statis.rank as rank
)
from CardReissueFormStatis statis
join statis.form as form
join form.student student
join student.major major
join major.subject subject
join student.department department
where form.status = :status
order by form.dateModified desc
""", [status: status], [offset: offset, max: max]
    }

    def getFormInfo(String userId, Long id) {
        def form = cardReissueFormService.getFormInfo(id)
        def workitem = Workitem.findByInstanceAndActivity(
                WorkflowInstance.load(form.workflowInstanceId),
                WorkflowActivity.load('card.reissue.check')
        )
        if (workitem && workitem.status == 0 && workitem.to.id == userId) {
            form.workitemId = workitem.id
        }
        return form
    }

    /**
     * 同意
     * @param cmd 同意数据
     * @param userId 用户ID
     * @param workItemId 工作项ID
     */
    void accept(AcceptCommand cmd, String userId, UUID workItemId) {
        CardReissueForm form = CardReissueForm.get(cmd.id)

        if (!form) {
            throw new NotFoundException()
        }

        if (!form.status.allow(AuditAction.ACCEPT)) {
            throw new BadRequestException()
        }

        if (!isChecker(cmd.id, userId)) {
            throw new ForbiddenException()
        }

        AuditAction action = AuditAction.ACCEPT
        form.status = form.status.next(action)
        form.save()

        String toUserId = workflowService.getCommitUser(form.workflowInstance)
        workflowService.setProcessed(workItemId)
        workflowService.createWorkItem(form.workflowInstance, Activities.VIEW, userId, action, cmd.comment, toUserId)
    }

    /**
     * 不同意
     * @param cmd 不同意数据
     * @param userId 用户ID
     * @param workItemId 工作项ID
     */
    void reject(RejectCommand cmd, String userId, UUID workItemId) {
        CardReissueForm form = CardReissueForm.get(cmd.id)

        if (!form) {
            throw new NotFoundException()
        }

        if (!form.status.allow(AuditAction.REJECT)) {
            throw new BadRequestException()
        }


        if (!isChecker(cmd.id, userId)) {
            throw new ForbiddenException()
        }

        def action = AuditAction.REJECT
        form.status = form.status.next(action)
        form.save()

        def toUserId = workflowService.getCommitUser(form.workflowInstance)
        workflowService.setProcessed(workItemId)
        workflowService.createWorkItem(form.workflowInstance, Activities.VIEW, userId, action, cmd.comment, toUserId)
    }

    private boolean isChecker(Long id, String userId) {
        cardReissueFormService.getCheckers(id).collect{it.id}.contains(userId)
    }
}
