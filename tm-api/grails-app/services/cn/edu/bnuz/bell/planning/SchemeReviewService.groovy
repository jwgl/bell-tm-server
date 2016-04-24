package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.workflow.*
import grails.transaction.Transactional

/**
 * 教学计划审核服务
 * @author Yang Lin
 */
@Transactional
class SchemeReviewService {
    WorkflowService workflowService
    SchemePublicService schemePublicService

    /**
     * 获取审核数据
     * @param id Scheme ID
     * @param userId 用户ID
     * @param workitemId 工作项ID
     * @return 审核数据
     */
    def getSchemeForReview(Long id, String userId, String workitemId) {
        def scheme = schemePublicService.getSchemeInfo(id)

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

        scheme.reviewType = reviewType

        return scheme
    }

    /**
     * 同意
     * @param cmd 同意数据
     * @param userId 用户ID
     * @param workItemId 工作项ID
     */
    void accept(AcceptCommand cmd, String userId, String workItemId) {
        Scheme scheme = Scheme.get(cmd.id)

        if (!scheme) {
            throw new NotFoundException()
        }

        if (!scheme.allowAction(AuditAction.ACCEPT)) {
            throw new BadRequestException()
        }

        String activity
        String toUserId
        switch (scheme.status) {
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
                toUserId = workflowService.getCommitUser(scheme.workflowInstance)
                break
            default:
                throw new BadRequestException()
        }

        AuditAction action = AuditAction.ACCEPT
        scheme.status = scheme.nextStatus(action)
        scheme.save()

        workflowService.setProcessed(workItemId)
        workflowService.createWorkItem(scheme.workflowInstance, activity, userId, action, cmd.comment, toUserId)
    }

    /**
     * 不同意
     * @param cmd 不同意数据
     * @param userId 用户ID
     * @param workItemId 工作项ID
     */
    void reject(RejectCommand cmd, String userId, String workItemId) {
        Scheme scheme = Scheme.get(cmd.id)

        if (!scheme) {
            throw new NotFoundException()
        }

        if (!scheme.allowAction(AuditAction.REJECT)) {
            throw new BadRequestException()
        }

        switch (scheme.status) {
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
        scheme.status = scheme.nextStatus(action)
        scheme.save()

        workflowService.setProcessed(workItemId)
        def toUserId = workflowService.getCommitUser(scheme.workflowInstance)
        workflowService.createWorkItem(scheme.workflowInstance, Activities.VIEW, userId, action, cmd.comment, toUserId)
    }


    /**
     * 获取审批人
     * @param id Scheme ID
     * @return 审批人列表
     */
    List getApprovers(Long id) {
        Scheme.getApprovers(id)
    }

    /**
     * 是否为审核人
     * @param id Scheme ID
     * @param userId 用户ID
     * @return 是否为审核人
     */
    private boolean isChecker(Long id, String userId) {
        Scheme.getCheckers(id).collect{it.id}.contains(userId)
    }

    /**
     * 是否为审批人
     * @param id Scheme ID
     * @param userId 用户ID
     * @return 是否为审批人
     */
    private boolean isApprover(Long id, String userId) {
        Scheme.getApprovers(id).collect{it.id}.contains(userId)
    }
}
