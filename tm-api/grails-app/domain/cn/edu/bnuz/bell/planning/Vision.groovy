package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.orm.PostgreSQLJsonUserType
import cn.edu.bnuz.bell.workflow.AuditAction
import cn.edu.bnuz.bell.workflow.AuditStatus
import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.workflow.WorkflowInstance
import org.grails.web.json.JSONElement

/**
 * 培养方案-目标与规格
 * @author Yang Lin
 */
class Vision {
    /**
     * 教学计划
     */
    Program program

    /**
     * 版本号
     * a.b.c.d <=> a << 24 & b << 16 & c << 8 & d
     * a : 0 - 255
     * b : 0 - 255
     * c : 0 - 255
     * d : 0 - 255
     */
    Integer versionNumber

    /**
     * 状态-0：新建；1-待审核；2-待审批；3：不通过；4：通过
     */
    AuditStatus status

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
     * 授予学位
     */
    String awardedDegree

    /**
     * 上一版本
     */
    Vision previous

    /**
     * 工作流实例
     */
    WorkflowInstance workflowInstance

    /**
     * JSON表示
     */
    JSONElement jsonValue

    static mapping = {
        comment             '培养方案-目标与规格'
        id                  generator: 'identity', comment: '培养方案-目标与规格ID'
        program             type: 'integer', comment: '教学计划'
        versionNumber       comment: '版本号'
        status              defaultValue: "0", comment: '状态-0：新建；1-待审核；2-待审批；3：不通过；4：通过'
        objective           length: 2000, comment: '培养目标'
        specification       length: 2000, comment: '培养要求'
        schoolingLength     length: 2000, comment: '学制'
        awardedDegree       length: 1000, comment: '授予学位'
        previous            comment: '上一版本'
        workflowInstance    comment: '工作流实例'
        jsonValue           type: PostgreSQLJsonUserType, comment: 'JSON表示'
    }

    static constraints = {
        schoolingLength      nullable: true, maxSize: 2000
        awardedDegree        nullable: true, maxSize: 1000
        previous             nullable: true
        workflowInstance     nullable: true
        jsonValue            nullable: true
    }

    Boolean allowAction(AuditAction action) {
        this.status.allow(action)
    }

    AuditStatus nextStatus(AuditAction action) {
        this.status.next(action)
    }

    String getWorkflowId() {
        this.previous ? 'vision.revise' : 'vision.create'
    }

    static Boolean allowAction(AuditStatus status, AuditAction action) {
        status.allow(action)
    }

    static String getDepartmentId(Long id) {
        List<String> results = Vision.executeQuery 'select m.department.id from Vision v join v.program p join p.major m where v.id = :id', [id: id]
        results ? results[0] : null
    }

    static List getApprovers(Long id) {
        User.findAllWithPermission(PlanningPerms.VISION_APPROVE)
    }

    static List getCheckers(Long id) {
        User.findAllWithPermission(PlanningPerms.VISION_CHECK, getDepartmentId(id))
    }

    static Integer VERSION_INCREMENT = 1 << 8
    static Integer INITIAL_VERSION = 1 << 24
}