package com.project.messanger.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;




@Component
public class Birt {

    private IReportEngine reportEngine;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    protected void initialize() throws BirtException {
        EngineConfig config = new EngineConfig();
        Platform.startup(config);
        IReportEngineFactory factory = (IReportEngineFactory)
                Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        reportEngine = factory.createReportEngine(config);
    }

    @PreDestroy
    public void cleanup() {
        if (reportEngine != null) {
            reportEngine.destroy();
            Platform.shutdown();
        }
    }

    public void generateUsersByDateReport(Date startDate, Date endDate,
                                          HttpServletResponse response,
                                          HttpServletRequest request) {
        IRunAndRenderTask task = null;
        try {
            String reportPath = "C:\\eclipse\\workspace\\report\\secondReport.rptdesign";
            IReportRunnable reportDesign = reportEngine.openReportDesign(reportPath);

            task = reportEngine.createRunAndRenderTask(reportDesign);

            // Устанавливаем параметры для отчета
            Map<String, Object> params = new HashMap<>();
            params.put("startDate", new java.sql.Date(startDate.getTime()));
            params.put("endDate", new java.sql.Date(endDate.getTime()));
            task.setParameterValues(params);

            // Настройка вывода PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=users_report.pdf");

            PDFRenderOption options = new PDFRenderOption();
            options.setOutputFormat("pdf");
            options.setOutputStream(response.getOutputStream());
            task.setRenderOption(options);

            // Передаем соединение с БД и контекст запроса
            task.getAppContext().put(
                    "OdaJDBCDriverPassInConnection",
                    dataSource.getConnection()
            );
            task.getAppContext().put(
                    EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT,
                    request
            );

            task.run();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации отчета", e);
        } finally {
            if (task != null) {
                task.close();
            }
        }
    }

    public void generateUsersByDateReport1(HttpServletResponse response,
                                          HttpServletRequest request) {
        IRunAndRenderTask task = null;
        try {
            String reportPath = "C:\\eclipse\\workspace\\report\\secondReport.rptdesign";
            IReportRunnable reportDesign = reportEngine.openReportDesign(reportPath);

            task = reportEngine.createRunAndRenderTask(reportDesign);

            // Устанавливаем параметры для отчета
            Map<String, Object> params = new HashMap<>();
            task.setParameterValues(params);

            // Настройка вывода PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=users_report.pdf");

            PDFRenderOption options = new PDFRenderOption();
            options.setOutputFormat("pdf");
            options.setOutputStream(response.getOutputStream());
            task.setRenderOption(options);

            // Передаем соединение с БД и контекст запроса
            task.getAppContext().put(
                    "OdaJDBCDriverPassInConnection",
                    dataSource.getConnection()
            );
            task.getAppContext().put(
                    EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT,
                    request
            );

            task.run();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации отчета", e);
        } finally {
            if (task != null) {
                task.close();
            }
        }
    }
}


