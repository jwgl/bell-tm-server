package cn.edu.bnuz.bell.card

class CardReissueFormStatis {
    /**
     * 申请次序
     */
    Long rank

    /**
     * 申请
     */
    CardReissueForm form

    static mapping = {
        comment '补办学生证申请表-统计'
        table   name: 'dv_card_reissue_form_statis'
        id      column: 'form_id', type: 'long', generator: 'foreign', params: [property: 'form']
        form    comment: '申请', insertable: false, updateable: false
    }
}
