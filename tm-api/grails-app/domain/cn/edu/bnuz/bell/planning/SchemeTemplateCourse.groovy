package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.Course
import cn.edu.bnuz.bell.master.Period
import cn.edu.bnuz.bell.master.Property

/**
 * 计划模板-课程
 * @author Yang Lin
 */
class SchemeTemplateCourse {
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
     * 可开课学期，见{@link cn.edu.bnuz.bell.planning.ProgramCourse#allowedTerm
     */
    Integer allowedTerm

    /**
     * 课程
     */
    Course course

    /**
     * 是否锁定，如果锁定则不能编辑和删除。
     * 可通过{@link ProgramSettings#schemeTemplateLocked}解锁
     */
    Boolean locked

    /**
     * 显示顺序
     */
    Integer displayOrder

    /**
     * 课程号匹配模式
     */
    String matchPattern

    static embedded = ['period']

    static belongsTo = [schemeTemplate: SchemeTemplate]

    static mapping = {
        comment         '教学计划模板-课程'
        id              comment: 'ID'
        practiceCredit  precision: 3, scale: 1, comment: '实践学分'
        property        comment: '课程性质'
        assessType      comment: '考核方式'
        suggestedTerm   comment: '建议修读学期'
        allowedTerm     comment: '可开课学期'
        course          comment: '课程'
        locked          defaultValue: "true", commont: '是否锁定'
        displayOrder    comment: '显示顺序'
        matchPattern    length: 50, comment: '课程号匹配模式'
    }

    static constraints = {
        matchPattern    nullable: true, maxSize: 50
    }
}
