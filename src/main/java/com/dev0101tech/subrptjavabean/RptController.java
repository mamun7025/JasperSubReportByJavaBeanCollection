package com.dev0101tech.subrptjavabean;

import net.sf.jasperreports.engine.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/api")
public class RptController {


    public void renderRpt(){
        String masterReportFileName = "D://zProfessional_Projects/dev-workspace/jasper-reports/jspr-sub-rpt-javabean/jsprsubrptjavabean/src/main/resources/reports" + "/jasper_report_template.jrxml";
        String subReportFileName = "D://zProfessional_Projects/dev-workspace/jasper-reports/jspr-sub-rpt-javabean/jsprsubrptjavabean/src/main/resources/reports" + "/address_report_template.jrxml";
        String destFileName = "D://zProfessional_Projects/dev-workspace/jasper-reports/jspr-sub-rpt-javabean/jsprsubrptjavabean/src/main/resources/reports" + "/jasper_report_template.JRprint";

        DataBeanList DataBeanList = new DataBeanList();
        ArrayList<DataBean> dataList = DataBeanList.getDataBeanList();
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);
        try {
            /* Compile the master and sub report */
            JasperReport jasperMasterReport = JasperCompileManager.compileReport(masterReportFileName);
            JasperReport jasperSubReport = JasperCompileManager.compileReport(subReportFileName);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("subreportParameter", jasperSubReport);
            JasperFillManager.fillReportToFile(jasperMasterReport, destFileName, parameters, beanColDataSource);
        } catch (JRException e) {
            e.printStackTrace();
        }
        System.out.println("Done filling!!! ...");
    }


    public byte[] renderRptByte() throws URISyntaxException {
        String  jasperRptFileName = "master_report";
        String  jasperSubRptFileName = "subreport_report";
        // Get absolute report folder path
//        File file = ResourceUtils.getFile("classpath:reports");
        URL res = getClass().getClassLoader().getResource("reports");
        assert res != null;
        File file = Paths.get(res.toURI()).toFile();
        String baseRptFolderPath = file.getAbsolutePath();
        System.out.println("Absolute path is : " + baseRptFolderPath);
        String fileSeparator = FileSystems.getDefault().getSeparator();
        String targetReportPath = baseRptFolderPath + fileSeparator + jasperRptFileName + ".jrxml";
        String subReportPath  =  baseRptFolderPath + fileSeparator;
        System.out.println("Target report path is : "+targetReportPath);

        DataBeanList DataBeanList = new DataBeanList();
        ArrayList<DataBean> dataList = DataBeanList.getDataBeanList();
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);
        try {
            /* Compile the master and sub report */
            JasperCompileManager.compileReportToFile(subReportPath + jasperSubRptFileName +  ".jrxml"); // compile it through IDE
            JasperReport jasperMasterReport = JasperCompileManager.compileReport(targetReportPath);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("SUBREPORT_DIR", subReportPath);
            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperMasterReport, parameters, beanColDataSource);
            // Export the report to a PDF file
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/get-rpt")
    public ResponseEntity<byte[]> getRpt() throws URISyntaxException {
        // renderRpt();
        String filename = "addressReport";
        byte[] data = renderRptByte();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename="+filename+".pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }


}
