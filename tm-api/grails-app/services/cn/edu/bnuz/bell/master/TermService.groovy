package cn.edu.bnuz.bell.master

/**
 * 学期服务
 * @author Yang Lin
 */
class TermService {

    def getActiveTerm() {
        Term.findByActive(true)
    }

    def getMinInSchoolGrade() {
        getActiveTerm().id.intdiv(10) - 3
    }
}
