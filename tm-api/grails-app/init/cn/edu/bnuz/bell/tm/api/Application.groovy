package cn.edu.bnuz.bell.tm.api

import cn.edu.bnuz.bell.config.ExternalConfigLoader
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer

@SpringBootApplication
@EnableResourceServer
// 开发过程中关闭，测试时打开。选项打开时，修改类后不能重新加载。
// 见https://github.com/spring-projects/spring-loaded/issues/164
// @EnableGlobalMethodSecurity(securedEnabled=true)
class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    void setEnvironment(Environment environment) {
        ExternalConfigLoader.load(environment)
    }
}