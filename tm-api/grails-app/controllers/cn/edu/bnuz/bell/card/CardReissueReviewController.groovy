package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.workflow.AcceptCommand
import cn.edu.bnuz.bell.workflow.AuditAction
import cn.edu.bnuz.bell.workflow.RejectCommand
import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_PERM_CARD_REISSUE_CHECK'])
class CardReissueReviewController {
    SecurityService securityService
    CardReissueReviewService cardReissueReviewService

    /**
     * 审核显示
     * @param cardReissueId Card reissue ID
     * @param id Workitem ID
     */
    def show(Long cardReissueId, String id) {
        log.debug(cardReissueId)
        renderJson cardReissueReviewService.getFormForReview(cardReissueId, securityService.userId, UUID.fromString(id))
    }

    /**
     * 处理同意/不同意
     * @param cardReissueId Vision ID
     * @param id Workitem ID
     * @param op 操作
     */
    def patch(Long cardReissueId, String id, String op) {
        def userId = securityService.userId
        def operation = AuditAction.valueOf(op)
        switch (operation) {
            case AuditAction.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = cardReissueId
                cardReissueReviewService.accept(cmd, userId, UUID.fromString(id))
                break
            case AuditAction.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = cardReissueId
                cardReissueReviewService.reject(cmd, userId, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        renderOk()
    }
}
