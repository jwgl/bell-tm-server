package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Department
import cn.edu.bnuz.bell.service.DataAccessService
import cn.edu.bnuz.bell.security.UserLogService
import cn.edu.bnuz.bell.utils.CollectionUtils
import cn.edu.bnuz.bell.utils.GroupCondition
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.AuditAction
import cn.edu.bnuz.bell.workflow.AuditStatus
import cn.edu.bnuz.bell.workflow.CommitCommand
import cn.edu.bnuz.bell.workflow.WorkflowService
import grails.compiler.GrailsCompileStatic
import grails.transaction.Transactional
import groovy.transform.TypeCheckingMode

/**
 * 教学计划编辑服务
 * @author Yang Lin
 */
@Transactional
@GrailsCompileStatic
class SchemeDraftService {
    SchemePublicService schemePublicService
    ProgramService programService
    UserLogService userLogService
    WorkflowService workflowService
    DataAccessService dataAccessService
    TermService termService
    /**
     * 获取所有者的教学计划
     * @param userId 用户ID
     * @return 教学计划列表
     */
    def getSchemes(String userId) {
        def startGrade = termService.minInSchoolGrade
        List results = Scheme.executeQuery '''
select new map(
  s.id as id,
  program.id as programId,
  subject.id as subjectId,
  program.type as programType,
  subject.name as subjectName,
  major.grade as grade,
  s.versionNumber as versionNumber,
  s.status as status
)
from Program program
join program.major major
join major.subject subject
left join Scheme s on s.program.id = program.id
where subject.isTopUp = false
and major.degree is not null
and major.grade >= :startGrade
and subject.id in (
  select sd.subject.id
  from SubjectDirector sd
  where sd.teacher.id = :userId
)
order by subject.id, major.grade desc, s.versionNumber desc
''', [startGrade: startGrade, userId: userId]

        List<GroupCondition> conditions = [
                new GroupCondition(
                        groupBy: 'subjectId',
                        into: 'programs',
                        mappings: [
                                subjectId  : 'id',
                                subjectName: 'name',
                        ]
                ),
                new GroupCondition(
                        groupBy: 'programId',
                        into: 'schemes',
                        mappings: [
                                programId  : 'id',
                                programType: 'type',
                                grade: 'grade',
                        ]
                )
        ]

        CollectionUtils.groupBy(results, conditions)
    }

    /**
     * 获取显示数据
     * @param id Scheme ID
     * @param userId 用户ID
     * @return 显示数据
     */
    def getSchemeForShow(Long id, String userId) {
        def scheme = schemePublicService.getSchemeInfo(id)

        if(!programService.isOwner(scheme.programId, userId)) {
            throw new ForbiddenException()
        }

        if (Scheme.allowAction(scheme.status, AuditAction.UPDATE)) {
            scheme.editable = true
        } else if (canRevise(id)) {
            scheme.revisable = true
        }

        return scheme
    }

    /**
     * 获取编辑数据
     * @param id Scheme ID
     * @param userId 用户ID
     * @return 编辑数据
     */
    def getSchemeForEdit(Long id, String userId) {
        def scheme = schemePublicService.getSchemeInfo(id)

        if (!programService.isOwner(scheme.programId, userId)) {
            throw new ForbiddenException()
        }

        if (!Scheme.allowAction(scheme.status, AuditAction.UPDATE)) {
            throw new BadRequestException()
        }

        // 除获取当前指定版本数据外，还需查询出被当前版本修改的项
        if (scheme.previousId) {
            scheme.courses.addAll(schemePublicService.getRevisedSchemeCoursesInfo(id))
        }

        return scheme
    }



    /**
     * 获取变更数据
     * @param id Scheme ID
     * @param userId 用户ID
     * @return 原始数据
     */
    def getSchemeForRevise(Long base, String userId) {
        def scheme = schemePublicService.getSchemeInfo(base)

        if (!programService.isOwner(scheme.programId, userId)) {
            throw new ForbiddenException()
        }

        if (!canRevise(base)) {
            throw new BadRequestException()
        }

        scheme.id = null
        scheme.previousId = base
        scheme.previousVersionNumber = scheme.versionNumber
        scheme.versionNumber += Scheme.VERSION_INCREMENT

        return scheme
    }

    /**
     * 获取新建数据
     * @param id Scheme ID
     * @param userId 用户ID
     * @return 新建数据
     */
    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    def getSchemeForCreate(Integer programId, String userId) {
        def scheme = dataAccessService.find(SchemeDto, '''
select new Dto (
  program.id as programId,
  program.type as programType,
  subject.name as subjectName,
  department.id as departmentId,
  department.name ad departmentName,
  major.grade as grade,
  program.credit as credit
)
from Program program
join program.major major
join major.subject subject
join major.department department
where program.id = :programId
''', [programId: programId])

        if (!programService.isOwner(programId, userId)) {
            throw new ForbiddenException()
        }

        if(!canCreate(programId)) {
            throw new BadRequestException()
        }

        scheme.template = programService.getSchemeTemplateInfo(programId)
        scheme.directions = programService.getProgramDirections(programId)

        return scheme
    }

    /**
     * 新建
     * @param cmd 数据
     * @param userId 用户
     * @return 新建项
     */
    Scheme create(SchemeCreateCommand cmd, String userId) {
        if (!programService.isOwner(cmd.programId, userId)) {
            throw new ForbiddenException()
        }

        if (!canCreate(cmd.programId)) {
            throw new BadRequestException()
        }

        Scheme scheme = new Scheme(
                program: Program.load(cmd.programId),
                versionNumber: cmd.versionNumber,
                status: AuditStatus.CREATED,
        )

        processSchemeCourses(scheme, cmd.courses)

        scheme.save()

        userLogService.log(AuditAction.CREATE, scheme)

        return scheme
    }

    /**
     * 修订
     * @param cmd 数据
     * @param userId 用户
     * @return 新建项
     */
    Scheme revise(SchemeReviseCommand cmd, String userId) {
        if (!programService.isOwner(cmd.programId, userId)) {
            throw new ForbiddenException()
        }

        if (!canRevise(cmd.previousId)) {
            throw new BadRequestException()
        }

        Scheme scheme = new Scheme(
                program: Program.load(cmd.programId),
                versionNumber: cmd.versionNumber,
                previous: Scheme.load(cmd.previousId),
                status: AuditStatus.CREATED
        )
        processSchemeCourses(scheme, cmd.courses)
        scheme.save()

        userLogService.log(AuditAction.CREATE, scheme)

        return scheme
    }

    /**
     * 更新
     * @param cmd 更新数据
     * @param userId 用户ID
     * @return Scheme
     */
    def update(SchemeUpdateCommand cmd, String userId) {
        Scheme scheme = Scheme.get(cmd.id)

        if (!scheme) {
            throw new NotFoundException()
        }

        if (!programService.isOwner(scheme.program.id, userId)) {
            throw new ForbiddenException()
        }

        if (!scheme.allowAction(AuditAction.UPDATE)) {
            throw new BadRequestException()
        }

        scheme.versionNumber = cmd.versionNumber
        processSchemeCourses(scheme, cmd.courses)
        scheme.save()

        userLogService.log(AuditAction.UPDATE, scheme)

        return scheme
    }

    private void processSchemeCourses(Scheme scheme, List<SchemeCourseCommand> schemeCourseCommands) {
        // 新建项
        ArrayList<SchemeCourseCommand> created = [];
        // 修改项（更改以前版本，需要插入新数据和标记旧数据）
        ArrayList<SchemeCourseCommand> modified = [];
        // 更新项
        ArrayList<SchemeCourseCommand> updated = [];
        // 恢复项
        ArrayList<SchemeCourseCommand> reverted = [];
        // 删除项（含被修改项）
        Map<Object, SchemeCourseCommand> deleted = [:]

        schemeCourseCommands.forEach { SchemeCourseCommand scc ->
            switch (scc.status) {
                case SchemeCourseCommand.STATUS_CREATED:
                    if (scc.previousId) {
                        modified << scc
                    } else {
                        created << scc
                    }
                    break;
                case SchemeCourseCommand.STATUS_DELETED:
                    deleted[scc.id] = scc
                    break;
                case SchemeCourseCommand.STATUS_UPDATED:
                    updated << scc
                    break;
                case SchemeCourseCommand.STATUS_REVERTED:
                    reverted << scc
                    break;
            }
        }

        created.each { c ->
            if (!c.isTempCourse) {
                SchemeCourse schemeCourse = c.toSchemeCourse()
                scheme.addToCourses(schemeCourse)
            } else {
                if (!c.courseId) {
                    def tempCourse = c.toTempCourse()
                    tempCourse.department = Department.load(programService.getDepartmentId(scheme.program.id))
                    tempCourse.save()
                    c.courseId = tempCourse.id
                }
                SchemeTempCourse schemeTempCourse = c.toSchemeTempCourse()
                scheme.addToTempCourses(schemeTempCourse)
            }
        }

        modified.each { c ->
            if (!c.isTempCourse) {
                // 如果引用项不是当前版本的，则插入
                SchemeCourse schemeCourse = c.toSchemeCourse()
                schemeCourse.previous = SchemeCourse.load(c.previousId)
                scheme.addToCourses(schemeCourse)
                // 标记删除
                SchemeCourse.markDelete(c.previousId, scheme.versionNumber)
            } else {
                // 如果没有引用或者引用项不是当前版本的，则插入
                if (!c.courseId) {
                    def tempCourse = c.toTempCourse()
                    tempCourse.department = Department.load(programService.getDepartmentId(scheme.program.id))
                    tempCourse.save()
                    c.courseId = tempCourse.id
                }
                SchemeTempCourse schemeTempCourse = c.toSchemeTempCourse()
                schemeTempCourse.previous = SchemeTempCourse.load(c.previousId)
                scheme.addToTempCourses(schemeTempCourse)
                // 标记删除
                SchemeTempCourse.markDelete(c.previousId, scheme.versionNumber)
            }

            // 删除被修改项
            deleted.remove(c.previousId)
        }

        updated.each { c ->
            if (!c.isTempCourse) {
                if (c.previousId) {
                    SchemeCourse previous = SchemeCourse.get(c.previousId)
                    if (c.equals(previous)) {
                        SchemeCourse.load(c.id).delete()
                        SchemeCourse.revertDelete(c.previousId)
                        return
                    }
                }

                SchemeCourse schemeCourse = SchemeCourse.get(c.id)
                c.update(schemeCourse)
                schemeCourse.save()
            } else {
                if (c.previousId) {
                    SchemeTempCourse previous = SchemeTempCourse.get(c.previousId)
                    if (c.equals(previous)) {
                        SchemeTempCourse.load(c.id).delete()
                        SchemeTempCourse.revertDelete(c.previousId)
                        return
                    }
                }

                SchemeTempCourse schemeTempCourse = SchemeTempCourse.get(c.id)
                c.update(schemeTempCourse)
                schemeTempCourse.save()
            }

            if (c.previousId) {
                // 删除被修改项
                deleted.remove(c.previousId)
            }
        }

        deleted.each { k, c ->
            if (!c.isTempCourse) {
                // 标记删除
                SchemeCourse.markDelete(c.id, scheme.versionNumber)
            } else {
                // 标记删除
                SchemeTempCourse.markDelete(c.id, scheme.versionNumber)
            }
        }

        reverted.each { c ->
            if (!c.isTempCourse) {
                if (c.schemeId == scheme.id) {
                    // 删除新增项
                    scheme.removeFromCourses(SchemeCourse.load(c.id))
                } else {
                    // 恢复删除项
                    SchemeCourse.revertDelete(c.id)
                }
            } else {
                if (c.schemeId == scheme.id) {
                    // 删除新增项
                    scheme.removeFromTempCourses(SchemeTempCourse.load(c.id))
                } else {
                    // 恢复删除项
                    SchemeTempCourse.revertDelete(c.id)
                }
            }
        }
    }

    /**
     * 删除
     * @param id Scheme ID
     * @param userId 用户ID
     */
    void delete(Long id, String userId) {
        Scheme scheme = Scheme.get(id)

        if (!scheme) {
            throw new NotFoundException()
        }

        if (!programService.isOwner(scheme.program.id, userId)) {
            throw new ForbiddenException()
        }

        if (!scheme.allowAction(AuditAction.DELETE)) {
            throw new BadRequestException()
        }

        userLogService.log(Scheme, AuditAction.DELETE, scheme)

        if (scheme.workflowInstance) {
            scheme.workflowInstance.delete()
        }

        scheme.delete()
    }


    /**
     * 提交审核
     * @param cmd 命令
     * @param userId 提交人
     */
    void commit(CommitCommand cmd, String userId) {
        Scheme scheme = Scheme.get(cmd.id)

        if (!scheme) {
            throw new NotFoundException()
        }

        if (!programService.isOwner(scheme.program.id, userId)) {
            throw new ForbiddenException()
        }

        if (!scheme.allowAction(AuditAction.COMMIT)) {
            throw new BadRequestException()
        }

        def action = AuditAction.COMMIT
        if (scheme.status == AuditStatus.REJECTED) {
            workflowService.setProcessed(scheme.workflowInstance, userId)
        } else {
            scheme.workflowInstance = workflowService.createInstance(scheme.workflowId, cmd.title, cmd.id)
        }
        workflowService.createWorkItem(scheme.workflowInstance, Activities.CHECK, userId, action, cmd.comment, cmd.to)

        scheme.status = scheme.nextStatus(action)
        scheme.save()

        userLogService.log(action, scheme)
    }

    /**
     * 是否可创建，只有不存在记录时，才可以创建
     * @param programId Program ID
     * @return 是否可创建
     */
    boolean canCreate(Integer programId) {
        Scheme.countByProgram(Program.load(programId)) == 0
    }

    /**
     * 是否可修订。只有当前记录是最新版本且通过审批，才可以修订。
     * @link ProgramSetting#schemeRevisible}
     * @param id basedOn Scheme ID
     * @return 是否可修订
     */
    boolean canRevise(Long id) {
        dataAccessService.exists '''
select scheme.id
from Scheme scheme
where scheme.id = :id
  and scheme.status = :status
  and scheme.versionNumber = (
    select max(s.versionNumber)
    from Scheme s
    where s.program = scheme.program
  )
  and scheme.program.id in (
    select program.id
    from ProgramSettings ps
    where ps.schemeRevisible = true
  )
''', [id: id, status: AuditStatus.APPROVED]
    }

    /**
     * 获取审核人
     * @param id Scheme ID
     * @return 审核人列表
     */
    List getCheckers(Long id) {
        Scheme.getCheckers(id)
    }

    /**
     * 查询课程，如果包含临时课程，则只选择本学院的临时课程
     * @param query 课号或名称
     * @param departmentId 学院ID
     * @return 课程列表
     */
    def findCoursesByNameOrId(String query, Integer type, String departmentId) {
        SchemeCourseDto.executeQuery '''
select new Map(
  id as id,
  name as name,
  credit as credit,
  theoryPeriod as theoryPeriod,
  experimentPeriod as experimentPeriod,
  periodWeeks as periodWeeks,
  assessType as assessType,
  departmentName as department,
  isTempCourse as isTempCourse
)
from SchemeCourseDto
where (id like :id or name like :name)
and (isTempCourse = false or departmentId = :departmentId)
and (:type = 0 or :type = 1 and isTempCourse = false or :type = 2 and isTempCourse = true)
order by locate(:query, id), locate(:query, name), id
''', [id: "%${query}%", name: "%${query}%", query: query, type: type, departmentId: departmentId], [max: 100]
    }
}
