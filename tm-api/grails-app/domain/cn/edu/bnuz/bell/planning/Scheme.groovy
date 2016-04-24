package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.workflow.AuditAction
import cn.edu.bnuz.bell.workflow.AuditStatus
import cn.edu.bnuz.bell.workflow.WorkflowInstance

/**
 * 培养方案-教学安排
 * @author Yang Lin
 */
class Scheme {
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
     * 上一版本
     */
    Scheme previous

    Set<SchemeCourse> courses

    WorkflowInstance workflowInstance

    static hasMany = [
        courses: SchemeCourse,
        tempCourses: SchemeTempCourse
    ]

    static mapping = {
        comment             '教学计划'
        id                  generator: 'identity', comment: '培养方案-教学安排ID'
        versionNumber       comment: '版本号'
        status              defaultValue: "0", comment: '状态-0：新建；1-待审核；2-待审批；3：不通过；4：通过'
        program             comment: '教学计划'
        previous            comment: '上一版本'
        workflowInstance    comment: '工作流实例'
    }

    static constraints = {
        previous            nullable: true
        workflowInstance    nullable: true
    }

    /**
     * 操作常数：新建
     */
    static Integer OP_CREATE = 0

    /**
     * 操作常数：更新
     */
    static Integer OP_UPDATE = 1

    /**
     * 操作常数：删除
     */
    static Integer OP_DELETE = 2

    String getWorkflowId() {
        this.previous ? 'scheme.revise' : 'scheme.create'
    }

    Boolean allowAction(AuditAction action) {
        return this.status.allow(action)
    }

    AuditStatus nextStatus(AuditAction action) {
        return this.status.next(action)
    }

    static Boolean allowAction(AuditStatus status, AuditAction action) {
        return status.allow(action)
    }

    static String getDepartmentId(Long id) {
        List<String> results = Scheme.executeQuery 'select m.department.id from Scheme s join s.program p join p.major m where s.id = :id', [id: id]
        results ? results[0] : null
    }

    static List getApprovers(Long id) {
        User.findAllWithPermission(PlanningPerms.SCHEME_APPROVE)
    }

    static List getCheckers(Long id) {
        User.findAllWithPermission(PlanningPerms.SCHEME_CHECK, getDepartmentId(id))
    }

    static Integer VERSION_INCREMENT = 1 << 8
    static Integer INITIAL_VERSION = 1 << 24
}
