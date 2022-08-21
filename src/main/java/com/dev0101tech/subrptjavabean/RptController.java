package com.dev0101tech.subrptjavabean;

import net.sf.jasperreports.engine.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
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

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("subreportParameter", jasperSubReport);
            JasperFillManager.fillReportToFile(jasperMasterReport, destFileName, parameters, beanColDataSource);
        } catch (JRException e) {
            e.printStackTrace();
        }
        System.out.println("Done filling!!! ...");
    }


    public byte[] renderRptByte(){
        String masterReportFileName = "D://zProfessional_Projects/dev-workspace/jasper-reports/jspr-sub-rpt-javabean/jsprsubrptjavabean/src/main/resources/reports" + "/jasper_report_template.jrxml";
        String subReportFileName = "D://zProfessional_Projects/dev-workspace/jasper-reports/jspr-sub-rpt-javabean/jsprsubrptjavabean/src/main/resources/reports" + "/address_report_template.jrxml";

        DataBeanList DataBeanList = new DataBeanList();
        ArrayList<DataBean> dataList = DataBeanList.getDataBeanList();
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);
        try {
            /* Compile the master and sub report */
            JasperReport jasperMasterReport = JasperCompileManager.compileReport(masterReportFileName);
            JasperReport jasperSubReport = JasperCompileManager.compileReport(subReportFileName);
            Map<String, Object> parameters = new HashMap<String, Object>();
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
    public ResponseEntity<byte[]> getRpt(){
//        renderRpt();
        String filename = "addressReport";
        byte[] data = renderRptByte();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename="+filename+".pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }


}
