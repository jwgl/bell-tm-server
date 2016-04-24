package cn.edu.bnuz.bell.planning

import org.springframework.security.access.annotation.Secured

/**
 * 培养方案。
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_VISION_READ'])
class VisionPublicController {
    def index() {}

    def show(Long id) {}
}