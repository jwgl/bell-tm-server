package cn.edu.bnuz.bell.card

/**
 * Created by yanglin on 2016/9/5.
 */
class CardReissueOrderCommand {
    Long id

    List<OrderItem> addedItems

    List<OrderItem> removedItems

    class OrderItem {
        Long id
        Long formId
    }
}
