package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.Period;
import cn.edu.bnuz.bell.master.Property
import cn.edu.bnuz.bell.organization.Department

/**
 * 临时课程
 * @author Yang Lin
 */
class TempCourse {
    /**
     * 课程名称
     */
    String name

    /**
     * 学分
     */
    BigDecimal credit

    /**
     * 学时
     */
    Period period

    /**
     * 课程性质
     */
    Property property

    /**
     * 层次-1:本科;2:硕士;3:博士;9:其它
     */
    Integer educationLevel

    /**
     * 考核方式-1:考试;2-考查;3-论文
     */
    Integer assessType

    /**
     * 所属学院
     */
    Department department

    static embedded = ['period']

    static mapping = {
        comment        '课程'
        id             generator: 'identity', comment: '课程ID'
        name           length:50,     comment: '名称'
        credit         precision: 3, scale: 1, comment: '学分'
        property       comment: '课程性质'
        educationLevel comment: '层次'
        assessType     comment: '考核方式'
        department     comment: '所属学院'
    }
}
