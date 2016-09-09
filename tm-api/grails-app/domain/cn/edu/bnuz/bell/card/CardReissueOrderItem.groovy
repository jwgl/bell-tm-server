package cn.edu.bnuz.bell.card

/**
 * 补办学生证制作订单项
 * @author Yang Lin
 */
class CardReissueOrderItem {
    CardReissueForm form
    Date dateReceived

    static belongsTo = [
        order : CardReissueOrder
    ]

    static mapping = {
        comment      '补办学生证制作订单项'
        id           generator: 'identity', comment: '订单项ID'
        form         comment: '申请表'
        dateReceived comment: '接收时间'
    }

    static constraints = {
        dateReceived nullable: true
    }
}
