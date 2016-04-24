package cn.edu.bnuz.bell.tm.api

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.grails.web.errors.GrailsExceptionResolver
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer

@SpringBootApplication
@EnableResourceServer
// 开发过程中关闭，测试时打开。选项打开时，修改类后不能重新加载。
// 见https://github.com/spring-projects/spring-loaded/issues/164
// @EnableGlobalMethodSecurity(securedEnabled=true)
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    Closure doWithSpring() {{->
        exceptionHandler(GrailsExceptionResolver) {
            exceptionMappings = [
                    'java.lang.Exception': '/application/serverError',
                    'org.springframework.security.access.AccessDeniedException': '/application/forbidden'
            ]
        }
    }}

    @Override
    void doWithApplicationContext() {

    }
}