package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.service.DataAccessService
import grails.transaction.Transactional

/**
 * 教学计划设置服务
 * @author Yang Lin
 */
@Transactional
class ProgramSettingsService {
    DataAccessService dataAccessService

    def getList() {
        getListByGrade(maxGrade)
    }

    def getListByGrade(Integer grade) {
        ProgramSettings.executeQuery '''
select new Map(
    d.id as departmentId,
    d.name as departmentName,
    s.id as subjectId,
    s.name as subjectName,
    m.grade as grade,
    degree.name as degreeName,
    p.id as programId,
    p.type as programType,
    ps.visionRevisible as visionRevisible,
    st.id as templateId,
    st.name as templateName,
    ps.schemeTemplateLocked as templateLocked,
    ps.schemeRevisible as schemeRevisible,
    ps.practiceCreditRatio as practiceCreditRatio
)
from ProgramSettings ps
join ps.program p
join p.major m
join m.subject s
join m.degree degree
join m.department d
left join ps.schemeTemplate st
where s.isTopUp = false
and m.degree is not null
and m.grade = :grade
''', [grade: grade]
    }

    def getGrades() {
        ProgramSettings.executeQuery '''
select distinct m.grade
from ProgramSettings ps
join ps.program p
join p.major m
order by m.grade desc
'''
    }

    private def getMaxGrade() {
        dataAccessService.getInteger '''
select max(m.grade)
from ProgramSettings ps
join ps.program p
join p.major m
'''
    }

    def update(Integer programId, ProgramSettingsCommand cmd) {
        ProgramSettings programSettings = ProgramSettings.get(programId)
        programSettings.visionRevisible = cmd.visionRevisible
        programSettings.schemeTemplateLocked = cmd.templateLocked
        programSettings.schemeRevisible = cmd.schemeRevisible
        programSettings.schemeTemplate = SchemeTemplate.load(cmd.templateId)
        programSettings.practiceCreditRatio = cmd.practiceCreditRatio
        programSettings.save()
    }
}
