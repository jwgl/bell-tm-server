package cn.edu.bnuz.bell.planning

import org.springframework.security.access.annotation.Secured

/**
 * 审核培养方案。
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_SCHEME_CHECK'])
class SchemeReviewController {
    def show() {}
}
