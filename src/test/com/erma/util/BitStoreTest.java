package com.erma.util;

import com.erma.util.bit.BitStore;

/**
 * @Date 2023/2/1 15:46
 * @Created by yzfeng
 */
public class BitStoreTest {

    public static void main(String[] args) {
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
}
