package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.workflow.AuditStatus

/**
 * Scheme DTO
 * @author Yang Lin
 */
class SchemeDto {
    Long id
    Integer versionNumber
    Long previousId
    Integer previousVersionNumber
    Integer programId
    Integer programType
    String subjectName
    String departmentId
    Integer grade
    Integer credit
    AuditStatus status
    String workflowInstanceId

    Boolean editable
    Boolean revisable

    List courses
    List tempCourses
    Map template
    List directions

    SchemeDto(
            Long id,
            Integer versionNumber,
            Long previousId,
            Integer previousVersionNumber,
            Integer programId,
            Integer programType,
            String subjectName,
            String departmentId,
            Integer grade,
            Integer credit,
            AuditStatus status,
            String workflowInstanceId) {
        this.id = id
        this.versionNumber = versionNumber
        this.previousId = previousId
        this.previousVersionNumber = previousVersionNumber
        this.programId = programId
        this.programType = programType
        this.subjectName = subjectName
        this.departmentId = departmentId
        this.grade = grade
        this.credit = credit
        this.status = status
        this.workflowInstanceId = workflowInstanceId
    }

    SchemeDto(
            Integer programId,
            Integer programType,
            String subjectName,
            String departmentId,
            Integer grade,
            Integer credit) {
        this.programId = programId
        this.programType = programType
        this.subjectName = subjectName
        this.departmentId = departmentId
        this.grade = grade
        this.credit = credit
        this.versionNumber = Scheme.INITIAL_VERSION
        this.previousVersionNumber = 0
    }
}
