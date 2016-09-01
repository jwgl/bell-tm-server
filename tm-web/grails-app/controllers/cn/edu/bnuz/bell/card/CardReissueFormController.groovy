package cn.edu.bnuz.bell.card

import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_PERM_CARD_REISSUE_WRITE'])
class CardReissueFormController {
    def index() { }
}
