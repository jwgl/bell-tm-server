package cn.edu.bnuz.bell.planning

import org.apache.commons.lang.builder.HashCodeBuilder

import cn.edu.bnuz.bell.master.Property

/**
 * 教学计划模板-课程性质
 * @author Yang Lin
 */
class SchemeTemplateProperty implements Serializable{
    /**
     * 培养方案模板
     */
    SchemeTemplate schemeTemplate

    /**
     * 课程性质
     */
    Property property

    /**
     * 课程性质别名，如果为空取课程性质名称
     */
    String label

    /**
     * 显示顺序
     */
    Integer displayOrder

    /**
     * 是否锁定，如果锁定则不允许添加、编辑和删除。
     * 可通过{@link ProgramSettings#schemeTemplateLocked}解锁。
     */
    Boolean locked

    static belongsTo = [schemeTemplate: SchemeTemplate]

    static mapping = {
        comment        '教学计划模板-课程性质'
        id             composite: ['schemeTemplate', 'property']
        schemeTemplate comment: '培养方案模板'
        property       fetch: 'join', comment: '课程性质'
        label          length: 50, comment: '课程性质模板'
        displayOrder   comment: '显示顺序'
        locked         defaultValue: "true", commont: '是否锁定'
    }

    static constraints = {
        label          nullable: true, maxSize: 50
    }

    boolean equals(other) {
        if (!(other instanceof SchemeTemplateProperty)) {
            return false
        }

        other.schemeTemplate?.id == schemeTemplate?.id && other.property?.id == property?.id
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        if (schemeTemplate)
            builder.append(schemeTemplate.id)
        if (property)
            builder.append(property.id)
        builder.toHashCode()
    }
}
