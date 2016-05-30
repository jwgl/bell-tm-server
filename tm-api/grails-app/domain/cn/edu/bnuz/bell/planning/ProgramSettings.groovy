package cn.edu.bnuz.bell.planning

/**
 * 计划设置
 * @author Yang Lin
 */
class ProgramSettings {
    /**
     * 虚拟ID，对应属性#program
     */
    Integer id

    /**
     * 执行计划，主键
     */
    Program program

    /**
     * 教学计划模板
     */
    SchemeTemplate schemeTemplate

    /**
     * 是否锁定模板
     */
    Boolean schemeTemplateLocked

    /**
     * 是否可修订教学计划
     */
    Boolean schemeRevisible

    /**
     * 实践学分比例（0.25，0.3）
     */
    BigDecimal practiceCreditRatio

    /**
     * 是否可修订培养方案
     */
    Boolean visionRevisible

    static mapping = {
        comment '教学计划-设置'
        id                   column: 'program_id', type: 'integer', generator: 'foreign', params: [ property: 'program']
        program              comment: '教学计划', insertable: false, updateable: false
        schemeTemplate       comment: '教学安排模板'
        schemeTemplateLocked defaultValue: "true", comment: '是否锁定模板'
        schemeRevisible      defaultValue: "true", comment: '是否可修订教学计划'
        practiceCreditRatio  precision: 4, scale: 2, defaultValue: "0", comment: '实践学分比例'
        visionRevisible      defaultValue: "true", comment: '是否可修订培养方案'
    }
}
