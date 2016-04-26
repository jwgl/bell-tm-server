package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.service.DataAccessService

/**
 * 教学计划服务
 * @author Yang Lin
 */
class ProgramService {
    DataAccessService dataAccessService
    boolean isOwner(Integer programId, String userId) {
        dataAccessService.exists '''
select p.id
from Program p
join p.major m,
SubjectDirector sd
where m.subject = sd.subject
and p.id = :programId
and sd.teacher.id = :userId
''', [programId: programId, userId: userId]
    }

    Integer getDepartmentId(Integer programId) {
        dataAccessService.getString '''
select s.department.id
from Program p
join p.major m
join m.subject s
''', [programId: programId]
    }

    /**
     * 获取教学安排模板
     * @param id 教学计划ID
     * @return 模板信息
     */
    Map getSchemeTemplateInfo(Integer programId) {
        List<Map> results = SchemeTemplate.executeQuery '''
select new Map(
    st.id as id,
    st.residualProperty.id as residualPropertyId,
    st.minResidualCredit as minResidualCredit,
    ps.schemeTemplateLocked as schemeTemplateLocked
)
from ProgramSettings ps
join ps.schemeTemplate st
where ps.program.id = :programId
''', [programId: programId]

        if(!results) {
            return null
        }

        Map template = results[0]

        template.properties = SchemeTemplateProperty.executeQuery '''
select new map (
  p.id as id,
  coalesce(stp.label, p.name) as name,
  p.isCompulsory as isCompulsory,
  p.hasDirections as hasDirections,
  pp.credit as credit,
  stp.locked as locked
)
from ProgramSettings ps
join ps.schemeTemplate st
join st.templateProperties stp
join stp.property p
left join ProgramProperty pp with pp.program.id = ps.program.id and pp.property.id = p.id
where ps.program.id = :programId
order by stp.displayOrder
''', [programId: programId]

        template.terms = SchemeTemplateTerm.executeQuery '''
select stt.term as term
from ProgramSettings ps
join ps.schemeTemplate st
join st.templateTerms stt
where ps.program.id = :programId
order by stt.displayOrder
''', [programId: programId]

        template.courses = SchemeTemplateCourse.executeQuery '''
select new Map(
  c.id as courseId,
  c.name as courseName,
  c.credit as credit,
  stc.property.id as propertyId,
  stc.practiceCredit as practiceCredit,
  stc.period.theory as theoryPeriod,
  stc.period.experiment as experimentPeriod,
  stc.period.weeks as periodWeeks,
  stc.assessType as assessType,
  stc.suggestedTerm as suggestedTerm,
  stc.allowedTerm as allowedTerm,
  stc.locked as locked,
  stc.matchPattern as matchPattern
)
from ProgramSettings ps
join ps.schemeTemplate st
join st.templateCourses stc
join stc.course c
where ps.program.id = :programId
order by stc.displayOrder
''', [programId: programId]

        return template
    }

    List getProgramDirections(Integer programId) {
        Program.executeQuery '''
select new map(
 direction.id as id,
 direction.name as name
)
from Program program
join program.directions direction
where program.id = :programId
order by direction.id
''', [programId: programId]
    }
}
