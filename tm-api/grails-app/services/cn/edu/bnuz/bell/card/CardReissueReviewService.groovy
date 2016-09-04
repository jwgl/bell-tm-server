package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.planning.Scheme
import cn.edu.bnuz.bell.workflow.*
import grails.transaction.Transactional

/**
 * 补办学生证审核服务
 * @author Yang Lin
 */
@Transactional
class CardReissueReviewService {
    CardReissueFormService cardReissueFormService
    WorkflowService workflowService
    /**
     * 获取审核数据
     * @param id Vision ID
     * @param userId 用户ID
     * @param workitemId 工作项ID
     * @return 审核数据
     */
    def getFormForReview(Long id, String userId, UUID workitemId) {
        def form = cardReissueFormService.getFormInfo(id)

        Workitem workitem = Workitem.get(workitemId)
        def reviewType = workitem.activitySuffix
        switch (reviewType) {
            case ReviewTypes.CHECK:
                if (!isChecker(id, userId)) {
                    throw new ForbiddenException()
                }
                break;
            default:
                throw new BadRequestException()
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
