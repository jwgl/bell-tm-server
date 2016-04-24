package cn.edu.bnuz.bell.planning

import groovy.transform.ToString

/**
 * 教学计划更新命令
 */

@ToString(includeNames = true, includePackage = false)
class SchemeUpdateCommand {
    Long id
    Integer versionNumber
    Integer programId

    List<SchemeCourseCommand> courses
}
