package com.erma.util.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Date 2023/1/17 15:27
 * @Created by yzfeng
 */
public class ExcelHandler {
    private Workbook workbook;

    public ExcelHandler() throws IOException {
        this.workbook = WorkbookFactory.create(true);
    }

    /**
     * 从一个文件路径读取，会改变源文件的内容
     *
     * @param filePath
     * @throws IOException
     */
    public ExcelHandler(String filePath) throws IOException {
        this.workbook = WorkbookFactory.create(new File(filePath));
    }

    /**
     * 从一个文件读取，会改变源文件的内容
     *
     * @param file
     * @throws IOException
     */
    public ExcelHandler(File file) throws IOException {
        this.workbook = WorkbookFactory.create(file);
    }

    /**
     * 从流中读取，不会改变源的内容
     *
     * @param inputStream
     * @throws IOException
     */
    public ExcelHandler(InputStream inputStream) throws IOException {
        this.workbook = WorkbookFactory.create(inputStream);
    }

    public SheetHandler getSheet(int i) {
        if (workbook.getNumberOfSheets() == 0) {
            return createSheet("Sheet1");
        }
        return new SheetHandler(workbook.getSheetAt(i));
    }

    public SheetHandler createSheet(String name) {
        return new SheetHandler(workbook.createSheet(name));
    }

    public void write(OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
        outputStream.flush();
        workbook.close();
    }

}
