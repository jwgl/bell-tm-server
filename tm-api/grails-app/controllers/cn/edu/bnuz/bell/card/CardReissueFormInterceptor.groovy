package cn.edu.bnuz.bell.card


import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.http.HttpStatus

/**
 * 补办学生证申请拦截器
 * @author Yang Lin
 */
class CardReissueFormInterceptor {
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
