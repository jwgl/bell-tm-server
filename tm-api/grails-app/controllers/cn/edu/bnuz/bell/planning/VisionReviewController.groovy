package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.workflow.AcceptCommand
import cn.edu.bnuz.bell.workflow.AuditAction
import cn.edu.bnuz.bell.workflow.RejectCommand
import org.springframework.security.access.annotation.Secured

/**
 * 培养方案审核控制器。
 * @author Yang Lin
 */
@Secured([PlanningPerms.ROLE_VISION_CHECK, PlanningPerms.ROLE_VISION_APPROVE])
class VisionReviewController implements ServiceExceptionHandler {
    SecurityService securityService
    VisionReviewService visionReviewService

    /**
     * 审核显示
     * @param visionPublicId Vision ID
     * @param id Workitem id
     * @return
     */
    def show(Long visionPublicId, String id) {
        renderJson visionReviewService.getVisionForReview(visionPublicId, securityService.userId, UUID.fromString(id))
    }

    /**
     * 处理同意/不同意
     * @param visionPublicId Vision ID
     * @param id Workitem ID
     * @param op 操作
     * @return
     */
    def patch(Long visionPublicId, String id, String op) {
        def userId = securityService.userId
        def operation = AuditAction.valueOf(op)
        switch (operation) {
            case AuditAction.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = visionPublicId
                visionReviewService.accept(cmd, userId, UUID.fromString(id))
                break
            case AuditAction.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = visionPublicId
                visionReviewService.reject(cmd, userId, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        renderOk()
    }

    /**
     * 获取批准人
     * @param visionPublicId Vision ID
     * @return 批准人列表
     */
    def approvers(Long visionPublicId) {
        renderJson visionReviewService.getApprovers(visionPublicId)
    }
}
