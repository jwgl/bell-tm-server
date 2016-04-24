package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import org.springframework.security.access.annotation.Secured

/**
 * 教学计划公共
 * @author Yang Lin
 */
@Secured(PlanningPerms.ROLE_SCHEME_READ)
class SchemePublicController implements ServiceExceptionHandler {
    SchemePublicService schemePublicService

    def index() {
        renderJson schemePublicService.getSchemes()
    }

    def show(Long id) {
        renderJson schemePublicService.getSchemeInfo(id)
    }
}
