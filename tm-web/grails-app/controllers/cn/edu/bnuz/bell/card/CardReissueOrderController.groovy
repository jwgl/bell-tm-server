package cn.edu.bnuz.bell.card

import org.springframework.security.access.annotation.Secured

/**
 * 补办学生证订单。
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_CARD_REISSUE_CHECK'])
class CardReissueOrderController {

    def index() { }
}
