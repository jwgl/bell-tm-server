package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.workflow.AcceptCommand
import cn.edu.bnuz.bell.workflow.AuditAction
import cn.edu.bnuz.bell.workflow.RejectCommand
import org.springframework.security.access.annotation.Secured

/**
 * 教学计划审核
 * @author Yang Lin
 */
@Secured(PlanningPerms.ROLE_SCHEME_CHECK)
class SchemeReviewController implements ServiceExceptionHandler {
    SecurityService securityService
    SchemeReviewService schemeReviewService

    /**
     * 审核显示
     * @param publicSchemeId Scheme ID
     * @param id Workitem ID
     */
    def show(Long schemePublicId, String id) {
        renderJson schemeReviewService.getSchemeForReview(schemePublicId, securityService.userId, id)
    }


    /**
     * 处理同意/不同意
     * @param schemePublicId Scheme ID
     * @param id Workitem ID
     * @param op 操作
     * @return
     */
    def patch(Long schemePublicId, String id, String op) {
        def userId = securityService.userId
        def operation = AuditAction.valueOf(op)
        switch (operation) {
            case AuditAction.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = schemePublicId
                schemeReviewService.accept(cmd, userId, id)
                break
            case AuditAction.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = schemePublicId
                schemeReviewService.reject(cmd, userId, id)
                break
            default:
                throw new BadRequestException()
        }

        renderOk()
    }

    /**
     * 获取批准人
     * @param schemePublicId Scheme ID
     * @return 批准人列表
     */
    def approvers(Long schemePublicId) {
        renderJson schemeReviewService.getApprovers(schemePublicId)
    }
}
