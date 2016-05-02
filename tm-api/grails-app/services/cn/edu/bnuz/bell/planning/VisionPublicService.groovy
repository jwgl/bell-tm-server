package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.service.DataAccessService
import cn.edu.bnuz.bell.utils.CollectionUtils
import cn.edu.bnuz.bell.utils.GroupCondition
import cn.edu.bnuz.bell.workflow.AuditStatus

/**
 * 培养方案审核服务
 * @author Yang Lin
 */
class VisionPublicService {
    SchemePublicService schemePublicService
    TermService termService
    DataAccessService dataAccessService

    /**
     * 获取已审核培养方案（除专升本和课程班）
     * @return 培养方案列表
     */
    def getAllVisions() {
        def startGrade = termService.minInSchoolGrade
        List results = Vision.executeQuery '''
select new map(
  v.id as id,
  program.id as programId,
  department.name as departmentName,
  department.id as departmentId,
  subject.id as subjectId,
  program.type as programType,
  subject.name as subjectName,
  major.grade as grade
)
from Vision v
join v.program program
join program.major major
join major.subject subject
join subject.department department
where subject.isTopUp = false
and major.degree is not null
and major.grade >= :startGrade
and v.versionNumber = (
  select max(v2.versionNumber)
  from Vision v2
  where v2.status = :status
  and v2.program = v.program
)
order by department.id, subject.id, major.grade
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
            items.inject([:]) { acc, item -> acc[item.grade] = item.id; acc }
        }
    }

    /**
     * 按学院获取列表
     * @param departmentId 学院ID
     */
    def getVisionsByDepartment(String departmentId) {
        def startGrade = termService.minInSchoolGrade
        Vision.executeQuery '''
select new map(
  v.id as id,
  subject.name as subjectName,
  major.grade as grade
)
from Vision v
join v.program program
join program.major major
join major.subject subject
join subject.department department
where subject.isTopUp = false
and major.degree is not null
and major.grade >= :startGrade
and department.id = :departmentId
and v.versionNumber = (
  select max(v2.versionNumber)
  from Vision v2
  where v2.status = :status
  and v2.program = v.program
)
order by major.grade desc, subject.id
''', [startGrade: startGrade, departmentId: departmentId, status: AuditStatus.APPROVED]
    }

    /**
     * 获取指定的培养方案信息（用于显示）。
     * @param id 培养方案ID
     * @return 培养方案信息
     */
    Map getVisionInfo(Long id) throws NotFoundException {
        List<Map> results = Vision.executeQuery '''
select new map(
  vision.id as id,
  vision.versionNumber as versionNumber,
  prev.id as previousId,
  prev.versionNumber as previousVersionNumber,
  program.id as programId,
  program.type as programType,
  subject.name as subjectName,
  major.department.id as departmentId,
  major.grade as grade,
  vision.objective as objective,
  vision.specification as specification,
  vision.schoolingLength as schoolingLength,
  vision.awardedDegree as awardedDegree,
  degree.name as degreeName,
  vision.status as status,
  vision.workflowInstance.id as workflowInstanceId
)
from Vision vision
join vision.program program
join program.major major
join major.subject subject
join major.degree degree
left join vision.previous prev
where vision.id = :id
''', [id: id]
        if(!results) {
            throw new NotFoundException()
        }

        def vision = results[0]
        Integer programId = vision.programId
        vision.schemeId = schemePublicService.getLatestSchemeId(programId)

        vision
    }

    /**
     * 获取缓存的Vision信息
     * @param id Vision ID
     * @return JSONElement
     */
    def getCachedVisionInfo(Long id) {
        def vision = dataAccessService.getJson 'select jsonValue from Vision where id = :id', [id: id]
        if (!vision) {
            def info = getVisionInfo(id)
            Vision.executeUpdate 'update Vision set jsonValue = :value where id = :id', [id: id, value: info]
            vision = dataAccessService.getJson 'select jsonValue from Vision where id = :id', [id: id]
        }
        vision
    }
}
