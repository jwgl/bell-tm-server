package cn.edu.bnuz.bell.tm.uaa

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.converters.JSON
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.core.authority.SimpleGrantedAuthority

@SpringBootApplication
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    void doWithApplicationContext() {
        JSON.registerObjectMarshaller(SimpleGrantedAuthority) {
            it.authority
        }
    }
}