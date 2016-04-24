package cn.edu.bnuz.bell.tm.api

class UrlMappings {

    static mappings = {
        // 公共服务
        "/fields"(resources: 'field', includes: ['index'])

        "/workflows"(resources: 'workflow', includes: []) {
            "/workitems"(action: 'workitems', includes: ['index'], method: 'GET')
        }

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

        "/departments"(resources: 'department', includes: []) {
            "/visions"(controller: 'visionPublic', action: 'indexByDepartment', method: 'GET')
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
        }

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: 'application', action:'index')

        "500"(view: '/application/serverError')
        "404"(view: '/application/notFound')
        "403"(view: '/application/forbidden')
        "401"(view: '/application/unauthorized')
    }
}
