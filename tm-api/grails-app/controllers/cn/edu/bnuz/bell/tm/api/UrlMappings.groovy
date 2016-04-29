package cn.edu.bnuz.bell.tm.api

class UrlMappings {

    static mappings = {
        // 公共服务
        "/fields"(resources: 'field', includes: ['index'])

        "/workflows"(resources: 'workflow', includes: []) {
            "/workitems"(action: 'workitems', includes: ['index'], method: 'GET')
        }

        // 按用户获取信息
        "/users"(resources: 'user', includes: []) {
            "/works"(resources: 'workitem', includes: ['index']) {
                collection {
                    '/counts'(controller: 'workitem', action: 'counts', method: 'GET')
                }
            }
            "/visions"(resources: 'visionDraft') {
                "/checkers"(controller: 'visionDraft', action: 'checkers', method: 'GET')
            }
            "/schemes"(resources: 'schemeDraft') {
                "/checkers"(controller: 'schemeDraft', action: 'checkers', method: 'GET')
                collection {
                    "/courses"(controller: 'schemeDraft', action: 'courses', method: 'GET')
                }
            }
        }

        // 按学院获取信息
        "/departments"(resources: 'department', includes: []) {
            "/visions"(controller: 'visionPublic', action: 'indexByDepartment', method: 'GET')
            "/schemes"(controller: 'schemePublic', action: 'indexByDepartment', method: 'GET')
            "/schemeDirections"(controller: 'schemePublic', action: 'schemeDirectionsByDepartment', method: 'GET')
        }

        // 培养方案
        "/visions"(resources: 'visionPublic', includes: ['index', 'show']) {
            "/reviews"(resources: 'visionReview', includes: ['show', 'patch']) {
                "/approvers"(controller: 'visionReview', action: 'approvers', method: 'GET')
            }
        }

        // 教学计划
        "/schemes"(resources: 'schemePublic', includes: ['index', 'show']) {
            "/reviews"(resources: 'schemeReview', includes: ['show', 'patch']) {
                "/approvers"(controller: 'schemeReview', action: 'approvers', method: 'GET')
            }
            "/properties"(resources: 'property', includes: []) {
                "/courses"(controller: 'schemePublic', action: 'propertyCourses', method: 'GET')
            }
            "/directions"(resources: 'direction', includes: []) {
                "/courses"(controller: 'schemePublic', action: 'directionCourses', method: 'GET')
            }
        }

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: 'application', action:'index')

        "500"(view: '/error')
        "404"(view: '/notFound')
        "403"(view: '/forbidden')
        "401"(view: '/unauthorized')
    }
}
