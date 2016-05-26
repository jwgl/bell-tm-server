package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.workflow.*
import grails.transaction.Transactional

/**
 * 培养方案审核服务
 * @author Yang Lin
 */
@Transactional
class VisionReviewService {
    WorkflowService workflowService
    VisionPublicService visionPublicService

    /**
     * 获取审核数据
     * @param id Vision ID
     * @param userId 用户ID
     * @param workitemId 工作项ID
     * @return 审核数据
     */
    def getVisionForReview(Long id, String userId, UUID workitemId) {
        def vision = visionPublicService.getVisionInfo(id)

        Workitem workItem = Workitem.get(workitemId)
        def reviewType = workItem.activitySuffix
        switch (reviewType) {
            case ReviewTypes.CHECK:
                if (!isChecker(id, userId)) {
                    throw new ForbiddenException()
                }
                break
            case ReviewTypes.APPROVE:
                if (!isApprover(id, userId)) {
                    throw new ForbiddenException()
                }
                break
            case ReviewTypes.REVIEW:
                // 已在WorkflowController中进行了身份验证
                break;
            default:
                throw new BadRequestException()
        }

        vision.reviewType = reviewType

        return vision
    }

    /**
     * 同意
     * @param cmd 同意数据
     * @param userId 用户ID
     * @param workItemId 工作项ID
     */
    void accept(AcceptCommand cmd, String userId, UUID workItemId) {
        Vision vision = Vision.get(cmd.id)

        if (!vision) {
            throw new NotFoundException()
        }

        if (!vision.allowAction(AuditAction.ACCEPT)) {
            throw new BadRequestException()
        }

        String activity
        String toUserId
        switch (vision.status) {
            case AuditStatus.COMMITTED: // check
                if (!isChecker(cmd.id, userId)) {
                    throw new ForbiddenException()
                }
                activity = Activities.APPROVE
                toUserId = cmd.to
                break
            case AuditStatus.CHECKED:   // approve
                if (!isApprover(cmd.id, userId)) {
                    throw new ForbiddenException()
                }
                activity = Activities.VIEW
                toUserId = workflowService.getCommitUser(vision.workflowInstance)
                break
            default:
                throw new BadRequestException()
        }

        AuditAction action = AuditAction.ACCEPT
        vision.status = vision.nextStatus(action)
        vision.save()

        workflowService.setProcessed(workItemId)
        workflowService.createWorkItem(vision.workflowInstance, activity, userId, action, cmd.comment, toUserId)
    }

    /**
     * 不同意
     * @param cmd 不同意数据
     * @param userId 用户ID
     * @param workItemId 工作项ID
     */
    void reject(RejectCommand cmd, String userId, UUID workItemId) {
        Vision vision = Vision.get(cmd.id)

        if (!vision) {
            throw new NotFoundException()
        }

        if (!vision.allowAction(AuditAction.REJECT)) {
            throw new BadRequestException()
        }

        switch (vision.status) {
            case AuditStatus.COMMITTED: // check
                if (!isChecker(cmd.id, userId)) {
                    throw new ForbiddenException()
                }
                break
            case AuditStatus.CHECKED:   // approve
                if (!isApprover(cmd.id, userId)) {
                    throw new ForbiddenException()
                }
                break
            default:
                throw new BadRequestException()
        }

        def action = AuditAction.REJECT
        vision.status = vision.nextStatus(action)
        vision.save()

        workflowService.setProcessed(workItemId)
        def toUserId = workflowService.getCommitUser(vision.workflowInstance)
        workflowService.createWorkItem(vision.workflowInstance, Activities.VIEW, userId, action, cmd.comment, toUserId)
    }

    /**
     * 获取审批人
     * @param id Vision ID
     * @return 审批人列表
     */
    List getApprovers(Long id) {
        Vision.getApprovers(id)
    }

    /**
     * 是否为审核人
     * @param id Vision ID
     * @param userId 用户ID
     * @return 是否为审核人
     */
    private boolean isChecker(Long id, String userId) {
        Vision.getCheckers(id).collect{it.id}.contains(userId)
    }

    /**
     * 是否为审批人
     * @param id Vision ID
     * @param userId 用户ID
     * @return 是否为审批人
     */
    private boolean isApprover(Long id, String userId) {
        Vision.getApprovers(id).collect{it.id}.contains(userId)
    }
}
