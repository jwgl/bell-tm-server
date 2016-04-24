package cn.edu.bnuz.bell.planning

import groovy.transform.CompileStatic
import cn.edu.bnuz.bell.security.Roles as tmRoles

@CompileStatic
interface PlanningRoles extends tmRoles {
    String SUBJECT_DIRECTOR = "ROLE_SUBJECT_DIRECTOR"
}
