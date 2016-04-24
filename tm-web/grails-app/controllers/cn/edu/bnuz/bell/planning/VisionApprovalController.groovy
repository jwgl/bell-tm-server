package cn.edu.bnuz.bell.planning

import org.springframework.security.access.annotation.Secured

/**
 * 培养方案审核
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_VISION_APPROVE'])
class VisionApprovalController {
    def index() {}
}
