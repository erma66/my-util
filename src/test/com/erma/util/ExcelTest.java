package com.erma.util;

import com.erma.util.excel.ExcelHandler;
import com.erma.util.excel.SheetHandler;
import com.erma.util.excel.ValueItem;
import com.erma.util.excel.ValueType;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Date 2023/2/1 15:51
 * @Created by yzfeng
 */
@Slf4j
public class ExcelTest {

    public static void main(String[] args) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\test.xlsx");
        List<ClaimFormExportVo> claimFormExportVoList = new ArrayList<>();
        claimFormExportVoList.add(new ClaimFormExportVo(LocalDate.now(), "test", "test1", "6589", "test test", new BigDecimal("15.4"), LocalDate.now(), new BigDecimal("20.1"), LocalDate.now(), LocalDate.now()));
        claimFormExportVoList.add(new ClaimFormExportVo(LocalDate.now(), "test", "test1", "6589", "test test", new BigDecimal("15.4"), LocalDate.now(), new BigDecimal("20.1"), LocalDate.now(), LocalDate.now()));
        claimFormExportVoList.add(new ClaimFormExportVo(LocalDate.now(), "test", "test1", "6589", "test test", new BigDecimal("15.4"), LocalDate.now(), new BigDecimal("20.1"), LocalDate.now(), LocalDate.now()));
        claimFormExportVoList.add(new ClaimFormExportVo(LocalDate.now(), "test", "test1", "6589", "test test", null, LocalDate.now(), new BigDecimal("20.1"), LocalDate.now(), LocalDate.now()));
        claimFormExportVoList.add(new ClaimFormExportVo(LocalDate.now(), "test", "test1", "6589", "test test", new BigDecimal("15.4"), LocalDate.now(), new BigDecimal("20.1"), LocalDate.now(), LocalDate.now()));
        claimFormExportVoList.add(new ClaimFormExportVo(LocalDate.now(), "test", "test1", "6589", "test test", null, null, null, null, null));
        claimFormExportVoList.add(new ClaimFormExportVo(LocalDate.now(), "test", "test1", "6589", "test test", new BigDecimal("15.4"), LocalDate.now(), new BigDecimal("20.1"), LocalDate.now(), LocalDate.now()));
        writeExcel(claimFormExportVoList, fileOutputStream);
    }

    private static void writeExcel(List<ClaimFormExportVo> claimFormDtoList, OutputStream outputStream) {
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("template/appointment_claim_form.xlsx");
            ExcelHandler excelHandle = new ExcelHandler(inputStream);
            SheetHandler sheet = excelHandle.getSheet(0);
            List<ValueItem> items = getClaimFormItems();
            sheet.setValueBatch(9, 0, claimFormDtoList, items);
            sheet.setCellValue(2, 1, LocalDate.now());
            sheet.setCellValue(3, 1, claimFormDtoList.size());
            sheet.setCellValue(4, 1, claimFormDtoList.stream()
                    .map(ClaimFormExportVo::getEstimatedReimbursementAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal::add)
                    .orElse(new BigDecimal("0")).doubleValue());
            excelHandle.write(outputStream);
        } catch (IOException | IllegalAccessException e) {
            log.warn("exprot claim form error", e);
            throw new RuntimeException("Export failed");
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    private static List<ValueItem> getClaimFormItems() {
        List<ValueItem> items = new ArrayList<>(10);
        items.add(new ValueItem("patientName", ValueType.STRING));
        items.add(new ValueItem("serviceDate", ValueType.LOCAL_DATE));
        items.add(new ValueItem("procedureCode", ValueType.STRING));
        items.add(new ValueItem("procedureDescription", ValueType.STRING));
        items.add(new ValueItem("providerName", ValueType.STRING));
        items.add(new ValueItem("codePayment", ValueType.DOUBLE));
        items.add(new ValueItem("claimSubmissionDate", ValueType.LOCAL_DATE));
        items.add(new ValueItem("estimatedReimbursementAmount", ValueType.DOUBLE));
        items.add(new ValueItem("estimatedClaimAcceptanceDate", ValueType.LOCAL_DATE));
        items.add(new ValueItem("nextAppointment", ValueType.LOCAL_DATE));
        return items;
    }

    @Value
    public static class ClaimFormExportVo {
        LocalDate serviceDate;
        String patientName;
        String providerName;
        String procedureCode;
        String procedureDescription;
        BigDecimal codePayment;
        LocalDate claimSubmissionDate;
        BigDecimal estimatedReimbursementAmount;
        LocalDate estimatedClaimAcceptanceDate;
        LocalDate nextAppointment;
    }


}
