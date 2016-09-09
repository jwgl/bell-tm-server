package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.workflow.AuditAction
import cn.edu.bnuz.bell.workflow.CommitCommand
import org.springframework.security.access.annotation.Secured

/**
 * 教学计划编辑
 * @author Yang Lin
 */
@Secured(PlanningPerms.ROLE_SCHEME_WRITE)
class SchemeDraftController implements ServiceExceptionHandler {
    SchemeDraftService schemeDraftService
    SecurityService securityService

    /**
     * 编辑列表
     * @param userId 当前用户ID
     * @return
     */
    def index(String userId) {
        renderJson schemeDraftService.getSchemes(userId)
    }

    /**
     * 显示时数据
     * @param userId 当前用户ID
     * @param id Scheme ID
     * @return 显示数据
     */
    def show(String userId, Long id) {
        renderJson schemeDraftService.getSchemeForShow(id, userId)
    }

    /**
     * 新建时数据
     * @param userId 当前用户ID
     * @param id Scheme ID
     * @return 新建数据
     */
    def create(String userId, String type) {
        if (type == 'revise') {
            Long base = params.long('base')
            renderJson schemeDraftService.getSchemeForRevise(base, userId)
        } else {
            Integer programId = params.int('program')
            renderJson schemeDraftService.getSchemeForCreate(programId, userId)
        }
    }

    /**
     * 保存
     * @param userId 当前用户
     * @param type 类型 revise | 空（新建）
     * @return id
     */
    def save(String userId, String type) {
        if (type == 'revise') {
            def cmd = new SchemeReviseCommand()
            bindData(cmd, request.JSON)
            def scheme = schemeDraftService.revise(cmd, userId)
            renderJson([id: scheme.id])
        } else {
            def cmd = new SchemeCreateCommand()
            bindData(cmd, request.JSON)
            def scheme = schemeDraftService.create(cmd, userId)
            renderJson([id: scheme.id])
        }
    }

    /**
     * 编辑时数据
     * @param userId 当前用户ID
     * @param id Scheme ID
     * @return 编辑数据
     */
    def edit(String userId, Long id) {
        renderJson schemeDraftService.getSchemeForEdit(id, userId)
    }

    /**
     * 更新
     * @param userId 当前用户ID
     * @param id Scheme ID
     * @return 服务状态
     */
    def update(String userId, Long id) {
        def cmd = new SchemeUpdateCommand()
        bindData(cmd, request.JSON)
        cmd.id = id;
        schemeDraftService.update(cmd, userId)
        renderOk()
    }

    /**
     * 删除
     * @param userId 当前用户ID
     * @param id Scheme ID
     * @return 服务状态
     */
    def delete(String userId, Long id) {
        schemeDraftService.delete(id, userId)
        renderOk()
    }

    /**
     * 提交
     * @param userId 当前用户ID
     * @param id Scheme ID
     * @param op 操作
     * @return 服务状态
     */
    def patch(String userId, Long id, String op) {
        def operation = AuditAction.valueOf(op)
        switch (operation) {
            case AuditAction.COMMIT:
                def cmd = new CommitCommand()
                bindData(cmd, request.JSON)
                cmd.id = id
                schemeDraftService.commit(cmd, userId)
                break
        }
        renderOk()
    }

    /**
     * 获取审核人
     * @param schemeDraftId Scheme ID
     * @return 审核人列表
     */
    def checkers(Long schemeDraftId) {
        renderJson schemeDraftService.getCheckers(schemeDraftId)
    }

    /**
     * 查询课程
     * @return 课程列表
     */
    def courses() {
        String query = params.q
        Integer type = params.int('t')
        if (query == null || type == null) {
            throw new BadRequestException()
        }
        renderJson schemeDraftService.findCoursesByNameOrId(query, type, securityService.departmentId)
    }
}
