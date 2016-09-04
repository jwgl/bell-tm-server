package cn.edu.bnuz.bell.card

import org.springframework.security.access.annotation.Secured

/**
 * 审核补办学生证申请。
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_CARD_REISSUE_CHECK'])
class CardReissueReviewController {
    def show() {}
}
