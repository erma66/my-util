package com.erma.util.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Date 2023/1/17 15:37
 * @Created by yzfeng
 */
@Slf4j
public class SheetHandler {
    private Sheet sheet;

    public SheetHandler(Sheet sheet) {
        this.sheet = sheet;
    }

    public void setCellValue(int row, int column, double value) {
        Cell cell = getCell(row, column);
        cell.setCellValue(value);
    }

    public void setCellValue(int row, int column, String value) {
        Cell cell = getCell(row, column);
        cell.setCellValue(value);
    }

    public void setCellValue(int row, int column, boolean value) {
        Cell cell = getCell(row, column);
        cell.setCellValue(value);
    }

    public void setCellValue(int row, int column, LocalDate value) {
        Cell cell = getCell(row, column);
        cell.setCellValue(value);
    }

    public void setCellValue(int row, int column, LocalDateTime value) {
        Cell cell = getCell(row, column);
        cell.setCellValue(value);
    }

    private Cell getCell(int row, int column) {
        Row row1 = sheet.getRow(row);
        if (row1 == null) {
            row1 = sheet.createRow(row);
        }
        Cell cell = row1.getCell(column);
        if (cell == null) {
            cell = row1.createCell(column);
        }
        Objects.requireNonNull(cell, "cell is null");
        return cell;
    }

    public <T> void setValueBatch(int startRow, int startColumn, List<T> datas, List<ValueItem> items) throws IllegalAccessException {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        validateType(datas.get(0).getClass(), items);
        for (T obj : datas) {
            int column = startColumn;
            for (ValueItem item : items) {
                setValue(startRow, column++, obj, item);
            }
            startRow++;
        }
    }

    private <T> void setValue(int row, int column, T obj, ValueItem item) throws IllegalAccessException {
        try {
            Field field = obj.getClass().getDeclaredField(item.getProp());
            field.setAccessible(true);
            Object value = field.get(obj);
            switch (item.getType()) {
                case DOUBLE:
                    Optional.ofNullable(value).ifPresent(r -> setCellValue(row, column, Double.parseDouble(value.toString())));
                    break;
                case BOOLEAN:
                    Optional.ofNullable(value).ifPresent(r -> setCellValue(row, column, (Boolean) value));
                    break;
                case LOCAL_DATE:
                    Optional.ofNullable(value).ifPresent(r -> setCellValue(row, column, (LocalDate) value));
                    break;
                case LOCAL_DATE_TIME:
                    Optional.ofNullable(value).ifPresent(r -> setCellValue(row, column, (LocalDateTime) value));
                    break;
                case STRING:
                    Optional.ofNullable(value).ifPresent(r -> setCellValue(row, column, value.toString()));
                    break;
            }
        } catch (NoSuchFieldException e) {
            log.error("no such field", e);
        }
    }

    private void validateType(Class<?> aClass, List<ValueItem> items) {
        try {
            String message = "Field type and export type do not match, field:";
            for (ValueItem item : items) {
                Field field = aClass.getDeclaredField(item.getProp());
                switch (item.getType()) {
                    case DOUBLE:
                        if (!field.getType().equals(Integer.class) &&
                                !field.getType().equals(Long.class) &&
                                !field.getType().equals(Float.class) &&
                                !field.getType().equals(Double.class) &&
                                !field.getType().equals(Short.class) &&
                                !field.getType().equals(BigInteger.class) &&
                                !field.getType().equals(BigDecimal.class)) {
                            throw new RuntimeException(message + field.getName());
                        }
                        break;
                    case BOOLEAN:
                        if (!field.getType().equals(Boolean.class)) {
                            throw new RuntimeException(message + field.getName());
                        }
                        break;
                    case LOCAL_DATE:
                        if (!field.getType().equals(LocalDate.class)) {
                            throw new RuntimeException(message + field.getName());
                        }
                        break;
                    case LOCAL_DATE_TIME:
                        if (!field.getType().equals(LocalDateTime.class)) {
                            throw new RuntimeException(message + field.getName());
                        }
                        break;
                }
            }
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException("Field does not exist", ex);
        }
    }
}
