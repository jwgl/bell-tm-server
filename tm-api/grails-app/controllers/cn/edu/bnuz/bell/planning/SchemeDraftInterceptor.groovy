package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.http.HttpStatus

/**
 * 教学计划编辑拦截器
 * @author Yang Lin
 */
class SchemeDraftInterceptor {
    SecurityService securityService

    boolean before() {
        if (params.userId != securityService.userId) {
            render(status: HttpStatus.FORBIDDEN)
            return false
        } else {
            return true
        }
    }

    boolean after() { true }

    void afterView() {}
}
