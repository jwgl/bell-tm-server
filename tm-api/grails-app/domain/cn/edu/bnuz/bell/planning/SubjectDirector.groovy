package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.Subject
import cn.edu.bnuz.bell.organization.Teacher

/**
 * 专业负责人
 * @author Yang Lin
 */
class SubjectDirector {
    /**
     * 虚拟ID，对应属性#subject
     */
    String id;

    /**
     * 校内专业
    */
    Subject subject

    /**
    * 专业负责人
    */
    Teacher teacher

    static belongsTo = [subject: Subject]

    static mapping = {
        comment        '专业负责人'
        id             column: 'subject_id', type: 'string', sqlType: 'varchar(4)', generator: 'foreign', params: [ property: 'subject']
        subject        comment: '校内专业', insertable: false, updateable: false
        teacher        comment: '专业负责人'
    }
}
