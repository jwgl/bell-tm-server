package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ServiceExceptionHandler

/**
 * 教学计划设置
 * @author Yang Lin
 */
class ProgramSettingsController implements ServiceExceptionHandler{
    ProgramSettingsService programSettingsService

    def index() {
        def grade = params.int('grade')
        if (grade) {
            renderJson programSettingsService.getListByGrade(grade)
        } else {
            renderJson programSettingsService.getList()
        }
    }

    def patch(Integer id) {
        def data = request.JSON
        switch (data.field) {
            case 'templateLocked':
                programSettingsService.setSchemeTemplateLocked(id, data.value as Boolean)
                break
            case 'schemeRevisible':
                programSettingsService.setSchemeRevisible(id, data.value as Boolean)
                break
            case 'schemeTemplate':
                programSettingsService.setSchemeTemplate(id, data.value as Integer)
                break
            case 'visionRevisible':
                programSettingsService.setVisionRevisible(id, data.value as Boolean)
                break
            default:
                throw new BadRequestException()
        }
        renderOk()
    }

    def grades() {
        renderJson programSettingsService.getGrades()
    }
}
