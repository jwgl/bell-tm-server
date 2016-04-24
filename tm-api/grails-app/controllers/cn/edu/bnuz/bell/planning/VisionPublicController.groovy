package cn.edu.bnuz.bell.planning

import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.annotation.Secured

/**
 * 培养方案公共控制器。
 * @author Yang Lin
 */
@Secured(PlanningPerms.ROLE_VISION_READ)
class VisionPublicController implements ServiceExceptionHandler {
    /**
     * 所有在校年级培养方案
     */
    VisionPublicService visionPublicService
    def index() {
        renderJson visionPublicService.getAllVisions()
    }

    /**
     * 按学院获取培养方案列表
     * EndPoint /departments/01/visions
     * @param id 学院ID
     */
    def indexByDepartment(String departmentId) {
        renderJson visionPublicService.getVisionsByDepartment(departmentId)
    }

    /**
     * 获取指定ID培养方案
     * @param id 培养方案ID
     */
    def show(Long id) {
        renderJson visionPublicService.getVisionInfo(id)
    }
}
