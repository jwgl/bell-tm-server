package cn.edu.bnuz.bell.planning

import groovy.transform.CompileStatic
import cn.edu.bnuz.bell.security.Perms as tmPerm

@CompileStatic
interface PlanningPerms extends tmPerm {
    String VISION_READ =    'PERM_VISION_READ'
    String VISION_WRITE =   'PERM_VISION_WRITE'
    String VISION_CHECK =   'PERM_VISION_CHECK'
    String VISION_APPROVE = 'PERM_VISION_APPROVE'
    String SCHEME_READ =    'PERM_SCHEME_READ'
    String SCHEME_WRITE =   'PERM_SCHEME_WRITE'
    String SCHEME_CHECK =   'PERM_SCHEME_CHECK'
    String SCHEME_APPROVE = 'PERM_SCHEME_APPROVE'

    String ROLE_VISION_READ =    'ROLE_PERM_VISION_READ'
    String ROLE_VISION_WRITE =   'ROLE_PERM_VISION_WRITE'
    String ROLE_VISION_CHECK =   'ROLE_PERM_VISION_CHECK'
    String ROLE_VISION_APPROVE = 'ROLE_PERM_VISION_APPROVE'
    String ROLE_SCHEME_READ =    'ROLE_PERM_SCHEME_READ'
    String ROLE_SCHEME_WRITE =   'ROLE_PERM_SCHEME_WRITE'
    String ROLE_SCHEME_CHECK =   'ROLE_PERM_SCHEME_CHECK'
    String ROLE_SCHEME_APPROVE = 'ROLE_PERM_SCHEME_APPROVE'
}
