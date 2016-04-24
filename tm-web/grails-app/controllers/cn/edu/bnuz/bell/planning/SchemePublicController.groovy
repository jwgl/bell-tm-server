package cn.edu.bnuz.bell.planning

import org.springframework.security.access.annotation.Secured

/**
 * 教学计划。
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_SCHEME_READ'])
class SchemePublicController {
    def index() {}

    def show() {}
}
