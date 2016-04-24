package cn.edu.bnuz.bell.planning

import groovy.transform.ToString

/**
 * 教学计划修订命令
 */

@ToString(includeNames = true, includePackage = false)
class SchemeReviseCommand {
    /**
     * 修订基础ID
     */
    Long previousId

    /**
     * 教学计划ID
     */
    Integer programId

    /**
     * 版本号
     */
    Integer versionNumber

    List<SchemeCourseCommand> courses
}
