package cn.edu.bnuz.bell.planning

import groovy.transform.ToString

/**
 * 教学计划创建命令
 */

@ToString(includeNames = true, includePackage = false)
class SchemeCreateCommand {
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
