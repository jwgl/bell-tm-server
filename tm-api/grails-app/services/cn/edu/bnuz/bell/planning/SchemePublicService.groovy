package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.service.DataAccessService
import cn.edu.bnuz.bell.utils.CollectionUtils
import cn.edu.bnuz.bell.utils.GroupCondition
import cn.edu.bnuz.bell.workflow.AuditStatus

/**
 * 教学计划公共服务。
 * @author Yang Lin
 */
class SchemePublicService {
    ProgramService programService
    DataAccessService dataAccessService
    TermService termService
    /**
     * 获取已审核培养方案（除专升本和课程班）
     * @return
     */
    def getSchemes() {
        def startGrade = termService.minInSchoolGrade
        List results = Scheme.executeQuery '''
select new map(
  scheme.id as id,
  department.name as departmentName,
  department.id as departmentId,
  subject.id as subjectId,
  program.type as programType,
  subject.name as subjectName,
  major.grade as grade
)
from Scheme scheme
join scheme.program program
join program.major major
join major.subject subject
join subject.department department
where subject.isTopUp = false
and major.degree is not null
and major.grade >= :startGrade
and scheme.versionNumber = (
  select max(s.versionNumber)
  from Scheme s
  where s.status = :status
  and s.program = scheme.program
)
order by department.id, subject.id, program.id, major.grade
''', [startGrade: startGrade, status: AuditStatus.APPROVED]

        List<GroupCondition> conditions = [
                new GroupCondition(
                        groupBy: 'departmentId',
                        into: 'subjects',
                        mappings: [
                                departmentId  : 'id',
                                departmentName: 'name'
                        ]
                ),
                new GroupCondition(
                        groupBy: 'subjectId',
                        into: 'grades',
                        mappings: [
                                subjectId  : 'id',
                                subjectName: 'name',
                                programType: 'type'
                        ]
                )
        ]

        CollectionUtils.groupBy(results, conditions) { items ->
            items.inject([:]) { acc, item -> acc[item.grade] = item.id; acc}
        }
    }


    /**
     * 按学院获取列表
     * @param departmentId 学院ID
     */
    def getSchemesByDepartment(String departmentId) {
        def startGrade = termService.minInSchoolGrade
        Scheme.executeQuery '''
select new map(
  s.id as id,
  subject.name as subjectName,
  major.grade as grade
)
from Scheme s
join s.program program
join program.major major
join major.subject subject
join subject.department department
where subject.isTopUp = false
and major.degree is not null
and major.grade >= :startGrade
and department.id = :departmentId
and s.versionNumber = (
  select max(s2.versionNumber)
  from Scheme s2
  where s2.status = :status
  and s2.program = s.program
)
order by major.grade desc, subject.id
''', [startGrade: startGrade, departmentId: departmentId, status: AuditStatus.APPROVED]
    }

    def getSchemeDirectionsByDepartment(String departmentId) {
        def startGrade = termService.minInSchoolGrade
        Scheme.executeQuery '''
select new map(
  s.id as schemeId,
  subject.name as subjectName,
  major.grade as grade,
  direction.id as directionId,
  direction.name as directionName
)
from Scheme s
join s.program program
join program.directions direction
join program.major major
join major.subject subject
join major.department department
where subject.isTopUp = false
and major.degree is not null
and major.grade >= :startGrade
and department.id = :departmentId
and s.versionNumber = (
  select max(s2.versionNumber)
  from Scheme s2
  where s2.status = :status
  and s2.program = s.program
)
order by major.grade desc, subject.id
''', [startGrade: startGrade, departmentId: departmentId, status: AuditStatus.APPROVED]
    }

    /**
     * 获取指定的方案信息（用于显示）。
     *
     * @param id 方案ID
     * @return 方案信息
     */
    SchemeDto getSchemeInfo(Long id) {
        def scheme = dataAccessService.find SchemeDto, '''
select new Dto (
  scheme.id,
  scheme.versionNumber,
  prev.id,
  prev.versionNumber,
  program.id,
  program.type,
  subject.name,
  department.id as departmentId,
  major.grade,
  program.credit,
  scheme.status,
  scheme.workflowInstance.id
)
from Scheme scheme
join scheme.program program
join program.major major
join major.subject subject
join major.department department
left join scheme.previous prev
where scheme.id = :id
''', [id: id]

        scheme.courses = getSchemeCoursesInfo(id)
        scheme.tempCourses = getSchemeTempCoursesInfo(id)
        scheme.template = programService.getSchemeTemplateInfo(scheme.programId)
        scheme.directions = programService.getProgramDirections(scheme.programId)

        scheme
    }

    /**
     * 获取指定版本的课程信息
     * @param schemeId Scheme ID
     * @return 课程信息列表
     */
    List getSchemeCoursesInfo(Long schemeId) {
        SchemeCourse.executeQuery '''
select new map(
  sc.id as id,
  c.id as courseId,
  c.name as courseName,
  c.credit as credit,
  sc.property.id as propertyId,
  sc.direction.id as directionId,
  sc.practiceCredit as practiceCredit,
  sc.period.theory as theoryPeriod,
  sc.period.experiment as experimentPeriod,
  sc.period.weeks as periodWeeks,
  sc.assessType as assessType,
  sc.suggestedTerm as suggestedTerm,
  sc.allowedTerm as allowedTerm,
  sc.courseGroup as courseGroup,
  sc.scheme.id as schemeId,
  sc.reviseVersion as reviseVersion,
  sc.previous.id as previousId
)
from SchemeCourse sc
join sc.course c
join sc.scheme s,
Scheme scheme
where scheme.id = :schemeId
and s.program = scheme.program
and s.versionNumber <= scheme.versionNumber
and (sc.reviseVersion is null or sc.reviseVersion > scheme.versionNumber)
''', [schemeId: schemeId]
    }

    /**
     * 获取指定的方案的临时课程信息
     * @param id Scheme ID
     * @return 方案的临时课程信息列表
     */
    List getSchemeTempCoursesInfo(Long schemeId) {
        SchemeTempCourse.executeQuery '''
select new map(
  sc.id as id,
  c.id as courseId,
  c.name as courseName,
  c.credit as credit,
  sc.property.id as propertyId,
  sc.direction.id as directionId,
  sc.practiceCredit as practiceCredit,
  sc.period.theory as theoryPeriod,
  sc.period.experiment as experimentPeriod,
  sc.period.weeks as periodWeeks,
  sc.assessType as assessType,
  sc.suggestedTerm as suggestedTerm,
  sc.allowedTerm as allowedTerm,
  sc.courseGroup as courseGroup,
  sc.scheme.id as schemeId,
  sc.reviseVersion as reviseVersion,
  sc.previous.id as previousId
)
from SchemeTempCourse sc
join sc.tempCourse c
where sc.scheme.id = :schemeId
''', [schemeId: schemeId]
    }

    /**
     * 获取当前计划版本修订的课程信息
     * @param schemeId Scheme ID
     * @return 课程信息列表
     */
    List getRevisedSchemeCoursesInfo(Long schemeId) {
        SchemeCourse.executeQuery '''
select new map(
  sc.id as id,
  c.id as courseId,
  c.name as courseName,
  c.credit as credit,
  sc.property.id as propertyId,
  sc.direction.id as directionId,
  sc.practiceCredit as practiceCredit,
  sc.period.theory as theoryPeriod,
  sc.period.experiment as experimentPeriod,
  sc.period.weeks as periodWeeks,
  sc.assessType as assessType,
  sc.suggestedTerm as suggestedTerm,
  sc.allowedTerm as allowedTerm,
  sc.courseGroup as courseGroup,
  sc.scheme.id as schemeId,
  sc.reviseVersion as reviseVersion,
  sc.previous.id as previousId
)
from SchemeCourse sc
join sc.course c
join sc.scheme s,
Scheme scheme
where scheme.id = :schemeId
and s.program =  scheme.program
and sc.reviseVersion = scheme.versionNumber
''', [schemeId: schemeId]
    }

    /**
     * 获取最新已审批的SchemeId
     * @param programId 教学计划ID
     * @return Scheme ID
     */
    Long getLatestSchemeId(Integer programId) {
        List<Long> results = Scheme.executeQuery '''
select scheme.id
from Scheme scheme
where scheme.program.id = :programId
  and versionNumber = (
    select max(s.versionNumber)
    from Scheme s
    where s.program = :programId
    and s.status = :status
  )
''', [programId: programId, status: AuditStatus.APPROVED]

        results ? results[0] : null
    }

    /**
     * 获取指定ID和性质的课程信息
     * @param schemeId Scheme ID
     * @param propertyId 性质ID
     * @return 课程列表
     */
    def getPropertyCourses(Long schemeId, Integer propertyId) {
        def results = []
        results.addAll this.getSchemeCoursesInfo(schemeId)
        results.addAll this.getSchemeTempCoursesInfo(schemeId)
        results.findAll {it.propertyId == propertyId}
    }

    /**
     * 获取指定ID和方向的课程信息
     * @param schemeId Scheme ID
     * @param directionId 方向ID
     * @return 课程列表
     */
    def getDirectionCourses(Long schemeId, Integer directionId) {
        def results = []
        results.addAll this.getSchemeCoursesInfo(schemeId)
        results.addAll this.getSchemeTempCoursesInfo(schemeId)
        results.findAll {it.directionId == directionId}
    }
}
