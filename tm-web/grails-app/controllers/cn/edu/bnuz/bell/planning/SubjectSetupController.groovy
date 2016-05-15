package cn.edu.bnuz.bell.planning

import org.springframework.security.access.annotation.Secured

/**
 * 计划管理
 * @author Yang Lin
 */
@Secured(['ROLE_PERM_SUBJECT_SETUP'])
class SubjectSetupController {
    def index() { }
}
