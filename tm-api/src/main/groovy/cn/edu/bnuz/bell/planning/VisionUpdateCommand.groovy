package cn.edu.bnuz.bell.planning

import groovy.transform.ToString

/**
 * 培养方案更新命令
 */
@ToString
class VisionUpdateCommand {
    Long id

    /**
     * 授予学位
     */
    String awardedDegree

    /**
     * 培养目标
     */
    String objective

    /**
     * 业务规格
     */
    String specification

    /**
     * 学制
     */
    String schoolingLength

    /**
     * 教学计划ID
     */
    Integer programId
}
