package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ServiceExceptionHandler

/**
 * 教学计划模板
 * @author Yang Lin
 */
class SchemeTemplateController implements ServiceExceptionHandler {
    SchemeTemplateService schemeTemplateService
    def index() {
        renderJson schemeTemplateService.getList()
    }
}
