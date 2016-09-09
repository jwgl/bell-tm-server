package cn.edu.bnuz.bell.card

import org.springframework.security.access.annotation.Secured

/**
 * 补办学生证申请（管理员）。
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_CARD_REISSUE_CHECK'])
class CardReissueAdminController {

    def index() {}

    def show() {}
}
