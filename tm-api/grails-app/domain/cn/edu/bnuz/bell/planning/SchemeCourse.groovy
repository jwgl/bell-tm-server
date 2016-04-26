package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.Course
import cn.edu.bnuz.bell.master.Period
import cn.edu.bnuz.bell.master.Property

/**
 * 计划课程
 * @author Yang Lin
 */
class SchemeCourse {
    /**
     * 实践学分
     */
    BigDecimal practiceCredit

    /**
     * 学时
     */
    Period period

    /**
     * 课程性质
     */
    Property property

    /**
     * 考核方式-1:考试;2-考查;3-论文
     */
    Integer assessType

    /**
     * 建议修读学期
     */
    Integer suggestedTerm

    /**
     * 可开课学期，见{@link cn.edu.bnuz.bell.planning.ProgramCourse#allowedTerm}
     */
    Integer allowedTerm

    /**
     * 课程分组，见{@link cn.edu.bnuz.bell.planning.ProgramCourse#courseGroup}
     */
    Integer courseGroup

    /**
     * 修订版本号，见Scheme.Version。
     * 如果不为空，表示当前记录被修订。
     */
    Integer reviseVersion

    /**
     * 引用被修订项
     */
    SchemeCourse previous

    /**
     * 专业方向
     */
    Direction direction

    /**
     * 课程
     */
    Course course

    /**
     * 扩展标志
     */
    Integer flag

    static embedded = ['period']

    static belongsTo = [scheme: Scheme]

    static mapping = {
        comment            '教学计划-课程'
        id                 generator: 'identity', comment: 'ID'
        practiceCredit     precision: 3, scale: 1, comment: '实践学分'
        property           comment: '课程性质'
        assessType         comment: '考核方式'
        suggestedTerm      comment: '建议修读学期'
        allowedTerm        comment: '可开课学期'
        courseGroup        comment: '课程分组'
        reviseVersion      comment: '修订版本号'
        previous           comment: '引用被修订项'
        direction          comment: '专业方向'
        course             comment: '课程'
        flag               comment: '扩展标志'
        scheme             comment: '培养方案'
    }

    static constraints = {
        courseGroup        nullable: true
        reviseVersion      nullable: true
        previous           nullable: true
        direction          nullable: true
        flag               nullable: true
    }

    /**
     * 标记删除课程
     * @param id SchemeCourse ID
     * @param version 删除版本号
     */
    static void markDelete(Long id, Integer version) {
        SchemeCourse.executeUpdate 'update SchemeCourse set reviseVersion = :version where id = :id', [id: id, version: version]
    }

    /**
     * 恢复删除课程
     * @param id SchemeCourse ID
     */
    static void revertDelete(Long id) {
        SchemeCourse.executeUpdate 'update SchemeCourse set reviseVersion = null where id = :id', [id: id]
    }
}
