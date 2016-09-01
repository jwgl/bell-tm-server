package cn.edu.bnuz.bell.tm.web

class UrlMappings {

    static mappings = {
        "/"(controller: 'index')

        "/fields"(resources: 'field', includes: ['index'])

        "/activities"(resources: 'activity', includes: ['index', 'show'])

        "/visions"(resources:'visionPublic', includes: ['index', 'show']) {
            "/reviews"(resources: 'visionReview', includes:['show'])
        }

        "/schemes"(resources: 'schemePublic', includes: ['index', 'show']) {
            "/reviews"(resources: 'schemeReview', includes:['show'])
        }

        "/users"(resources: 'user') {
            "/works"(resources: 'workitem', includes: ['index', 'show'])
            "/visions"(resources: 'visionDraft', includes: ['index'])
            "/schemes"(resources: 'schemeDraft', includes: ['index'])
            "/cardReissues"(resources: 'cardReissueForm', includes: ['index'])
            "/picture"(resource: 'picture', includes: ['show'])
        }

        group "/settings", {
            "/subject"(controller: "subjectSetup")
            "/program"(controller: "programSetup")
        }

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
