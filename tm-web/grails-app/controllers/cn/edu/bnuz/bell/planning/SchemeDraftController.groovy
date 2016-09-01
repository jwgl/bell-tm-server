package cn.edu.bnuz.bell.planning

import org.springframework.security.access.annotation.Secured

/**
 * 编辑教学计划
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_SCHEME_WRITE'])
class SchemeDraftController {
    def index() {}
}
