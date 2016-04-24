package cn.edu.bnuz.bell.workflow

import cn.edu.bnuz.bell.security.SecurityService

/**
 * 工作项控制器。
 * @author Yang Lin
 */
class WorkitemController {
    SecurityService securityService
    WorkflowService workflowService

    def index() {
        switch (params.is) {
            case 'open':
                renderJson workflowService.getOpenWorkitems(securityService.userId)
                break
            case 'closed':
                renderJson workflowService.getClosedWorkitems(securityService.userId)
                break
        }
    }

    def counts() {
        renderJson workflowService.getCounts(securityService.userId)
    }
}
