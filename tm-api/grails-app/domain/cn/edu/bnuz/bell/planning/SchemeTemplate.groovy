package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.Property

/**
 * 计划模板
 * @author Yang Lin
 */
class SchemeTemplate {
    /**
     * 模板ID
     */
    Integer id

    /**
     * 模板名称
     */
    String name

    /**
     * 说明
     */
    String description

    /**
     * 残余属性，残余属性的学分等于总学分减去所有其它属性的学分。
     */
    Property residualProperty

    /**
     * 最小残余学分
     */
    Integer minResidualCredit

    static hasMany = [
        templateProperties: SchemeTemplateProperty,
        templateTerms: SchemeTemplateTerm,
        templateCourses: SchemeTemplateCourse,
    ]

    static mapping = {
        comment           '教学计划模板'
        id                generator: 'assigned', comment: '培养方案模板ID'
        name              length: 50, comment: '名称'
        description       length: 255, comment: '描述'
        residualProperty  comment: '残余属性'
        minResidualCredit comment: '最小残余学分'
    }

    static constraints = {
        residualProperty  nullable: true
        minResidualCredit nullable: true
    }
}
