package cn.edu.bnuz.bell.card


import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(CardReissueFormInterceptor)
class CardReissueFormInterceptorSpec extends Specification {

    def setup() {
    }

    def cleanup() {

    }

    void "Test cardReissueForm interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"cardReissueForm")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
