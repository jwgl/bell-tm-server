package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.Subject
import cn.edu.bnuz.bell.organization.Teacher

/**
 * 专业负责人
 * @author Yang Lin
 */
class SubjectDirector {
    /**
     * 校内专业
    */
    Subject subject

    /**
    * 专业负责人
    */
    Teacher teacher

    static mapping = {
        comment        '专业负责人'
        id             name: 'subject', generator: 'assigned'
        subject        type: 'string', length: 4, comment: '校内专业'
        teacher        comment: '专业负责人'
    }
}
