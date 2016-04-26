package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.master.Course
import cn.edu.bnuz.bell.master.Period
import cn.edu.bnuz.bell.master.Property
import groovy.transform.ToString

/**
 * 教学计划课程数据
 */
@ToString(includeNames = true, includePackage = false)
class SchemeCourseCommand {
    static Integer STATUS_CREATED = 1
    static Integer STATUS_DELETED = 2
    static Integer STATUS_UPDATED = 3
    static Integer STATUS_REVERTED = 4

    Long id
    Boolean isTempCourse
    String courseId
    Boolean isPractical
    BigDecimal practiceCredit
    Integer theoryPeriod
    Integer experimentPeriod
    Integer periodWeeks
    Integer assessType
    Integer suggestedTerm
    Integer allowedTerm
    Integer courseGroup
    Integer propertyId
    Integer directionId
    TempCourseCommand tempCourse
    Integer status
    Long previousId
    Long schemeId

    @ToString(includeNames = true, includePackage = false)
    class TempCourseCommand {
        String name
        BigDecimal credit
    }

    static constraints = {
        courseId    nullable: true
        tempCourse  nullable: true
        directionId nullable: true
        status      nullable: true
        previousId  nullable: true
        schemeId    nullable: true
    }

    @Override
    boolean equals(Object obj) {
        if (obj instanceof SchemeCourse) {
            SchemeCourse schemeCourse = (SchemeCourse)obj
            return courseId         == schemeCourse.getCourse().getId() &&
                   practiceCredit   == schemeCourse.getPracticeCredit() &&
                   theoryPeriod     == schemeCourse.getPeriod().getTheory() &&
                   experimentPeriod == schemeCourse.getPeriod().getExperiment() &&
                   periodWeeks      == schemeCourse.getPeriod().getWeeks() &&
                   assessType       == schemeCourse.getAssessType() &&
                   suggestedTerm    == schemeCourse.getSuggestedTerm() &&
                   allowedTerm      == schemeCourse.getAllowedTerm() &&
                   courseGroup      == schemeCourse.getCourseGroup()
        } else if (obj instanceof SchemeTempCourse) {
            SchemeTempCourse schemeCourse = (SchemeTempCourse)obj
            return courseId         == schemeCourse.getTempCourse().getId().toString() &&
                   practiceCredit   == schemeCourse.getPracticeCredit() &&
                   theoryPeriod     == schemeCourse.getPeriod().getTheory() &&
                   experimentPeriod == schemeCourse.getPeriod().getExperiment() &&
                   periodWeeks      == schemeCourse.getPeriod().getWeeks() &&
                   assessType       == schemeCourse.getAssessType() &&
                   suggestedTerm    == schemeCourse.getSuggestedTerm() &&
                   allowedTerm      == schemeCourse.getAllowedTerm() &&
                   courseGroup      == schemeCourse.getCourseGroup()
        } else {
            return false
        }
    }

    SchemeCourse toSchemeCourse() {
        new SchemeCourse(
                course: Course.load(courseId),
                practiceCredit: practiceCredit,
                period: new Period(
                        theory: theoryPeriod,
                        experiment: experimentPeriod,
                        weeks: periodWeeks
                ),
                property: Property.load(propertyId),
                direction: directionId ? Direction.load(directionId) : null,
                assessType: assessType,
                suggestedTerm: suggestedTerm,
                allowedTerm: allowedTerm,
                courseGroup: courseGroup,
        )
    }

    SchemeTempCourse toSchemeTempCourse() {
        new SchemeTempCourse(
                tempCourse: TempCourse.load(courseId),
                practiceCredit: practiceCredit,
                period: new Period(
                        theory: theoryPeriod,
                        experiment: experimentPeriod,
                        weeks: periodWeeks
                ),
                property: Property.load(propertyId),
                direction: directionId ? Direction.load(directionId) : null,
                assessType: assessType,
                suggestedTerm: suggestedTerm,
                allowedTerm: allowedTerm,
                courseGroup: courseGroup,
        )
    }

    TempCourse toTempCourse() {
        new TempCourse(
                name: tempCourse.name,
                credit: tempCourse.credit,
                period: new Period(
                        theory: theoryPeriod,
                        experiment: experimentPeriod,
                        weeks: periodWeeks
                ),
                property: Property.load(propertyId),
                educationLevel: 1,
                assessType: assessType,
        )
    }

    void update(SchemeCourse schemeCourse) {
        schemeCourse.course = Course.load(courseId)
        schemeCourse.practiceCredit = practiceCredit
        schemeCourse.period = new Period(
                theory: theoryPeriod,
                experiment: experimentPeriod,
                weeks: periodWeeks
        )
        schemeCourse.assessType = assessType
        schemeCourse.suggestedTerm = suggestedTerm
        schemeCourse.allowedTerm = allowedTerm
        schemeCourse.courseGroup = courseGroup
    }

    void update(SchemeTempCourse schemeTempCourse) {
        schemeTempCourse.tempCourse = TempCourse.load(courseId)
        schemeTempCourse.practiceCredit = practiceCredit
        schemeTempCourse.period = new Period(
                theory: theoryPeriod,
                experiment: experimentPeriod,
                weeks: periodWeeks
        )
        schemeTempCourse.assessType = assessType
        schemeTempCourse.suggestedTerm = suggestedTerm
        schemeTempCourse.allowedTerm = allowedTerm
        schemeTempCourse.courseGroup = courseGroup
    }
}
