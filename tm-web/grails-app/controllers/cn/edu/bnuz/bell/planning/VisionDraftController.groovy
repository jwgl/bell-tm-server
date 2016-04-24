package cn.edu.bnuz.bell.planning

import org.springframework.security.access.annotation.Secured

/**
 * 编辑培养方案。
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_VISION_WRITE'])
class VisionDraftController {
    def index() {}

    def show() {}
}
