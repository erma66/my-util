package com.erma.util;

import com.erma.util.async.AsyncTaskUtils;
import com.erma.util.bit.BitStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        // write your code here

//        batchTest();
        testBit();
    }

    private static void testBit() {
        BitStore bitStore = new BitStore(100);
        System.out.println(bitStore.get(99));
        bitStore.setOne(99);
        System.out.println(bitStore.get(99));
        bitStore.setZero(99);
        System.out.println(bitStore.get(99));
        bitStore.setOne(64);
        System.out.println(bitStore.get(64));
        bitStore.setZero(64);
        System.out.println(bitStore.get(64));
        System.out.println(bitStore.get(100));
        System.out.println(bitStore.get(101));
    }

    private static void batchTest() {
        // 线程安全集合，用来保存结果
        Map<Integer, String> result = new ConcurrentHashMap<>();
        // 初始化总数据
        List<DemoData> dataList = getDemoData();
        // 异步任务结果处理集合
        List<CompletableFuture> futureList = new ArrayList<>(100);
        // 分批处理，每次处理100条
        for (int i = 0; i < dataList.size(); i += 100) {
            futureList.add(
                    AsyncTaskUtils.execute(
                            Main::filterData,
                            dataList.subList(i, i + 100),
                            errorData -> {
                                handleResult(errorData, result);
                            },
                            exception -> {
                                System.out.println(exception.getMessage());
                            }));
        }
        // 等待所有任务完成
        try {
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("错误数据数:" + result.size());
    }

    /**
     * 过滤数据的方法
     *
     * @param demoDataList
     * @return
     */
    private static List<ErrorData> filterData(List<DemoData> demoDataList) {
        List<ErrorData> errorDataList = new ArrayList<>();
        for (int i = 0; i < demoDataList.size(); i++) {
            String error = validData(demoDataList.get(i));
            if (error != null && !error.isEmpty()) {
                errorDataList.add(new ErrorData(demoDataList.get(i).getIndex(), error));
            }
        }
        return errorDataList;
    }

    /**
     * 校验数据的方法
     *
     * @param demoData
     * @return
     */
    private static String validData(DemoData demoData) {
        if (demoData.getIndex() % 3 == 0) {
            return "error data";
        }
        return null;
    }

    /**
     * 处理过滤结果
     *
     * @param errorDataList
     * @param result
     */
    private static void handleResult(List<ErrorData> errorDataList, Map<Integer, String> result) {
        result.putAll(
                errorDataList.stream()
                        .collect(Collectors.toMap(ErrorData::getIndex, ErrorData::getError)));
    }

    /**
     * 初始化数据
     *
     * @return
     */
    private static List<DemoData> getDemoData() {
        List<DemoData> demoData = new ArrayList<>(10000);
        for (int i = 0; i < 10000; i++) {
            demoData.add(new DemoData(i, "demo" + i));
        }
        return demoData;
    }

    static class DemoData {
        private int index;
        private String name;

        public DemoData(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class ErrorData {
        private int index;
        private String error;

        public ErrorData(int index, String error) {
            this.index = index;
            this.error = error;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
