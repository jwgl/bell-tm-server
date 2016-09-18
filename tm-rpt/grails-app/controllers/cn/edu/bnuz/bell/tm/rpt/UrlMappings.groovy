package cn.edu.bnuz.bell.tm.rpt

class UrlMappings {

    static mappings = {
        "/cardReissueOrders"(resources: 'cardReissueOrder', includes: ['show']) {
            "/pictures"(action: 'pictures', method: 'GET')
            "/distribute"(action: 'distribute', method: 'GET')
        }

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
