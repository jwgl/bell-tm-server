package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.master.Subject
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Teacher
import grails.transaction.Transactional

@Transactional
class SubjectDirectorService {
    TermService termService

    def getSubjectDirectors() {
        def startGrade = termService.minInSchoolGrade
        SubjectDirector.executeQuery '''
select new Map(
    s.id as subjectId,
    s.name as subjectName,
    d.id as departmentId,
    d.name as departmentName,
    t.id as teacherId,
    t.name as teacherName,
    (select max(grade) from Major where subject = s) as maxGrade
)
from Subject s
join s.department d
left join SubjectDirector sd with sd.subject.id = s.id
left join Teacher t with sd.teacher.id = t.id
where s in (
  select m.subject
  from Major m
  where m.grade >= :startGrade
)
''', [startGrade: startGrade]
    }

    def save(SubjectDirectorCommand cmd) {
        Subject subject = Subject.get(cmd.subjectId)
        if (!subject) {
            throw new NotFoundException()
        }

        SubjectDirector subjectDirector = SubjectDirector.findBySubject(subject)
        if (!subjectDirector) {
            subjectDirector = new SubjectDirector()
            subjectDirector.subject = subject
        }
        subjectDirector.teacher = Teacher.load(cmd.teacherId)
        subjectDirector.save()
    }
}
