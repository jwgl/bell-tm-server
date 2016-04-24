package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.http.HttpStatus

/**
 * 编辑培养方案控制器拦截器
 * @author Yang Lin
 */
class VisionDraftInterceptor {
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
