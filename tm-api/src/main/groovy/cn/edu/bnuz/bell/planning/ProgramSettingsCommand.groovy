package cn.edu.bnuz.bell.planning

import groovy.transform.ToString

/**
 * 计划设置数据
 */
@ToString(includeNames = true, includePackage = false)
class ProgramSettingsCommand {
    Boolean visionRevisible
    Integer templateId
    Boolean templateLocked
    Boolean schemeRevisible
    BigDecimal practiceCreditRatio
}
