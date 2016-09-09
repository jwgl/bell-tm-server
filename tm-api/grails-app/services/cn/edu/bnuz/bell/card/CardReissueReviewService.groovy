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


}
