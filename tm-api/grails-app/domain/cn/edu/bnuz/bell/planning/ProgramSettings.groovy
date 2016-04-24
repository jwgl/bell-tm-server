package cn.edu.bnuz.bell.planning

/**
 * 计划设置
 * @author Yang Lin
 */
class ProgramSettings {
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
     * 是否可修订培养方案
     */
    Boolean visionRevisible

    static mapping = {
        comment '教学计划-设置'
        id                   name: 'program'
        program              comment: '教学计划', type: 'integer'
        schemeTemplate       comment: '教学安排模板'
        schemeTemplateLocked defaultValue: "true", comment: '是否锁定模板'
        schemeRevisible      defaultValue: "true", comment: '是否可修订教学计划'
        visionRevisible      defaultValue: "true", comment: '是否可修订培养方案'
    }
}
