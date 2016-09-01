package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import cn.edu.bnuz.bell.organization.Student
import cn.edu.bnuz.bell.workflow.AuditAction
import cn.edu.bnuz.bell.workflow.CommitCommand
import grails.util.Holders
import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_PERM_CARD_REISSUE_WRITE'])
class CardReissueFormController implements ServiceExceptionHandler {
    CardReissueFormService cardReissueFormService

    def index(String userId) {
        Student student = Student.findById(userId)
        if (student == null) {
            throw new NotFoundException()
        }

        List<CardReissueForm> forms = cardReissueFormService.getAll(userId)

        renderJson([
                student: [
                        id      : student.id,
                        atSchool: student.atSchool,
                        picture : new File(Holders.config.bell.student.picturePath, "${student.id}.jpg").exists()
                ],
                forms  : forms
        ])
    }

    def show(String userId, Long id) {
        renderJson cardReissueFormService.getFormForShow(userId, id)
    }

    def create(String userId) {
        renderJson cardReissueFormService.getFormForCreate(userId)
    }

    def save(String userId) {
        def form = cardReissueFormService.create(userId, request.JSON.reason as String)
        renderJson([id: form.id])
    }

    def edit(String userId, Long id) {
        renderJson cardReissueFormService.getFormForEdit(userId, id)
    }

    def update(String userId, Long id) {
        cardReissueFormService.update(userId, id, request.JSON.reason as String)
        renderOk()
    }

    def delete(String userId, Long id) {
        cardReissueFormService.delete(userId, id)
        renderOk()
    }

    def patch(String userId, Long id, String op) {
        def operation = AuditAction.valueOf(op)
        switch (operation) {
            case AuditAction.COMMIT:
                def cmd = new CommitCommand()
                bindData(cmd, request.JSON)
                cmd.id = id
                cardReissueFormService.commit(cmd, userId)
                renderOk()
                break
            default:
                throw new BadRequestException()
        }
    }

    def checkers(Long cardReissueFormId) {
        renderJson cardReissueFormService.getCheckers(cardReissueFormId)
    }
}
