package cn.edu.bnuz.bell.tm.web

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.web.context.request.RequestContextListener

@SpringBootApplication
@EnableZuulProxy
class Application extends GrailsAutoConfiguration {

    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Bean
    RequestContextListener requestContextListener(){
        new RequestContextListener();
    }
}