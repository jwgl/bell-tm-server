package cn.edu.bnuz.bell.planning

import org.springframework.security.access.annotation.Secured

/**
 * 教学计划设置
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_PROGRAM_SETUP'])
class ProgramSetupController {
    def index() { }
}
