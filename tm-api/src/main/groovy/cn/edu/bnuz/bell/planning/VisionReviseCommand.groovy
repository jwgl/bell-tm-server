package cn.edu.bnuz.bell.planning

import groovy.transform.ToString

/**
 * 培养方案修订命令
 */
@ToString
class VisionReviseCommand {
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

    /**
     * 授予学位
     */
    String awardedDegree

    /**
     * 培养目标
     */
    String objective

    /**
     * 培养要求
     */
    String specification

    /**
     * 学制
     */
    String schoolingLength
}
