package cn.edu.bnuz.bell.http

import grails.plugins.rest.client.RestBuilder
import org.grails.web.json.JSONElement
import org.grails.web.util.WebUtils
import org.springframework.beans.factory.annotation.Value

class ApiClientService {
    @Value('${bell.tm.api.url}')
    String apiUrl

    JSONElement get(String url) {
        def request = WebUtils.retrieveGrailsWebRequest().currentRequest
        RestBuilder rest = new RestBuilder()
        def resp = rest.get("${apiUrl}/${url}") {
            auth request.getHeader('authorization')
        }
        return resp.json
    }
}
