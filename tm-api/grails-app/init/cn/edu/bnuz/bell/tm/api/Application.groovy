package cn.edu.bnuz.bell.tm.api

import cn.edu.bnuz.bell.card.CardReissueStatus
import cn.edu.bnuz.bell.config.ExternalConfigLoader
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.converters.JSON
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer

@SpringBootApplication
@EnableResourceServer
@EnableGlobalMethodSecurity(securedEnabled=true)
class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    void setEnvironment(Environment environment) {
        ExternalConfigLoader.load(environment)
    }

    @Override
    void doWithApplicationContext() {
        JSON.registerObjectMarshaller(CardReissueStatus) {
            it.name()
        }
    }
}