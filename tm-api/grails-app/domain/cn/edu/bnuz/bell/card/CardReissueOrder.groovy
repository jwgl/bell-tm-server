package cn.edu.bnuz.bell.card

import cn.edu.bnuz.bell.organization.Teacher;

/**
 * 补办学生证制作订单
 * @author Yang Lin
 */
class CardReissueOrder {
	/**
	 * 创建人
	 */
	Teacher creator

	/**
	 * 修改时间
	 */
	Teacher modifier

	/**
	 * 创建时间
	 */
	Date dateCreated

	/**
	 * 修改时间
	 */
	Date dateModified

	static hasMany = [items : CardReissueOrderItem]

	static mapping = {
		comment      '补办学生证制作订单'
        id           generator: 'identity', comment: '订单ID'
        creator      comment: '创建人'
        modifier     comment: '修改人'
        dateCreated  comment: '创建时间'
        dateModified comment: '修改时间'
	}

	static constraints = {
		modifier     nullable: true
		dateModified nullable: true
	}
}
