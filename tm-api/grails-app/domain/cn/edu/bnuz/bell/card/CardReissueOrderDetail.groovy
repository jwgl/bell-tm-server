package cn.edu.bnuz.bell.card

/**
 * 补办学生证制作订单明细
 * @author Yang Lin
 */
class CardReissueOrderDetail {
	CardReissueForm form
	Date dateReceived

	static belongsTo = [
		order : CardReissueOrderHeader
	]

	static mapping = {
		comment      '补办学生证制作订单明细'
        form         comment: '申请表'
        dateReceived comment: '接收时间'
	}

	static constraints = {
		dateReceived nullable: true
	}
}
