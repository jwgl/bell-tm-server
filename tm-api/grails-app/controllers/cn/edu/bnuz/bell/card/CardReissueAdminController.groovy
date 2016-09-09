package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.workflow.AcceptCommand
import cn.edu.bnuz.bell.workflow.AuditAction
import cn.edu.bnuz.bell.workflow.RejectCommand
import org.springframework.security.access.annotation.Secured

/**
 * 补办学生证申请（管理员）
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_CARD_REISSUE_CHECK'])
class CardReissueAdminController {
    CardReissueAdminService cardReissueAdminService
    SecurityService securityService

    def index() {
        def status = CardReissueStatus.valueOf(params.status)
        // 当参数中没有offset和max时，表示不分页
        def offset = params.int("offset") ?: 0
        def max = params.int("max") ?: (params.int("offset") ? 20 : Integer.MAX_VALUE)
        def forms = cardReissueAdminService.findAllByStatus(status, offset, max)
        def counts = cardReissueAdminService.getCountsByStatus()
        renderJson([counts: counts, forms: forms])
    }

    def show(Long id) {
        renderJson cardReissueAdminService.getFormInfo(securityService.userId, id);
    }

    def patch(Long cardReissueAdminId, String id, String op) {
        def userId = securityService.userId
        def operation = AuditAction.valueOf(op)
        switch (operation) {
            case AuditAction.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = cardReissueAdminId
                cardReissueAdminService.accept(cmd, userId, UUID.fromString(id))
                break
            case AuditAction.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = cardReissueAdminId
                cardReissueAdminService.reject(cmd, userId, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        renderOk()
    }
}
