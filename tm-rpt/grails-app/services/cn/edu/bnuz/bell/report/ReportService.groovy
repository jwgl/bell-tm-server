package cn.edu.bnuz.bell.report

import grails.core.GrailsApplication
import grails.transaction.Transactional
import grails.web.context.ServletContextHolder
import org.eclipse.birt.core.framework.Platform
import org.eclipse.birt.core.framework.PlatformServletContext
import org.eclipse.birt.report.engine.api.*
import org.hibernate.SessionFactory
import org.hibernate.internal.SessionImpl
import org.springframework.beans.factory.annotation.Value

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Transactional
class ReportService {
    IReportEngine reportEngine
    SessionFactory sessionFactory
    GrailsApplication grailsApplication

    @Value('${birt.report.input.dir}')
    String inputDir;


    @PostConstruct
    private void init() {
        log.debug('init')
        EngineConfig config = new EngineConfig()
        config.setPlatformContext(new PlatformServletContext(ServletContextHolder.servletContext))

        Platform.startup(config)
        IReportEngineFactory factory = (IReportEngineFactory)Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY)
        reportEngine = factory.createReportEngine(config)

    }

    @PreDestroy
    private void shutdown() {
        log.debug('shutdown')
        reportEngine.destroy()
        Platform.shutdown()
    }

    def runAndRender(ReportCommand cmd) {
        log.debug('service')
        String designFile = ServletContextHolder.servletContext.getRealPath("reports/${cmd.reportDesign}.rptdesign")
        IReportRunnable reportDesign = reportEngine.openReportDesign(designFile)
        cmd.title = reportDesign.getProperty(IReportRunnable.TITLE)
        IRunAndRenderTask runAndRenderTask = reportEngine.createRunAndRenderTask(reportDesign)
        runAndRenderTask.appContext.put('OdaJDBCDriverPassInConnection', ((SessionImpl)sessionFactory.currentSession).connection())
        runAndRenderTask.appContext.put('OdaJDBCDriverPassInConnectionCloseAfterUse', false)
        runAndRenderTask.setParameterValues(cmd.parameters)

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        RenderOption renderOption = cmd.renderOption
        renderOption.setOutputStream(outputStream)

        runAndRenderTask.setRenderOption(renderOption)
        runAndRenderTask.run()
        runAndRenderTask.close()

        return outputStream
    }
}
