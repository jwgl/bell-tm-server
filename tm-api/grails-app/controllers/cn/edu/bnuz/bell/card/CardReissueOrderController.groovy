package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_PERM_CARD_REISSUE_CHECK'])
class CardReissueOrderController implements ServiceExceptionHandler {
    SecurityService securityService
    CardReissueOrderService cardReissueOrderService

    def index() {
        renderJson cardReissueOrderService.getAll()
    }

    def show(Long id) {
        renderJson cardReissueOrderService.getInfo(id)
    }

    def save() {
        def userId = securityService.userId
        def cmd = new CardReissueOrderCommand()
        bindData cmd, request.JSON
        def order = cardReissueOrderService.create(userId, cmd)
        renderJson({id: order.id})
    }

    def edit(Long id) {
        renderJson cardReissueOrderService.getInfo(id)
    }

    def update(Long id) {
        def userId = securityService.userId
        def cmd = new CardReissueOrderCommand()
        bindData cmd, request.JSON
        cmd.id = id
        cardReissueOrderService.update(userId, cmd)
        renderOk()
    }

    def patch(Long id) {
        def status
        switch (request.JSON.type) {
            case 'RECEIVE':
                status = cardReissueOrderService.receive(id, request.JSON.formId, request.JSON.received)
                break
            case 'RECEIVE_ALL':
                status = cardReissueOrderService.receiveAll(id, request.JSON.received)
                break
            default:
                throw new BadRequestException()
        }
        renderJson([status: status])
    }
}
