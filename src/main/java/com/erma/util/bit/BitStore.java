package com.erma.util.bit;


/**
 * @Date 2023/1/6 17:52
 * @Created by yzfeng
 */
public class BitStore {
    private final long length;
    private final long[] value;

    public BitStore(long length) {
        long size = length % 64 == 0 ? length >> 6 : (length >> 6) + 1;
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("The length must be less than " + Integer.MAX_VALUE * 64L);
        }
        this.length = length;
        value = new long[(int) size];
    }

    public byte get(long index) {
        long[] position = getPosition(index);
        return getBit(value[(int) position[0]], position[1]);
    }

    private long[] getPosition(long index) {
        if (index > length) {
            throw new IllegalArgumentException("The index cannot be greater than " + length);
        }
        return new long[]{index >> 6, index % 64};
    }

    public void setOne(long index) {
        long[] position = getPosition(index);
        value[(int) position[0]] = setBitToOne(value[(int) position[0]], position[1]);
    }

    public void setZero(long index) {
        long[] position = getPosition(index);
        value[(int) position[0]] = setBitToZero(value[(int) position[0]], position[1]);
    }

    private byte getBit(long n, long m) {
        return (byte) ((n >> (m - 1)) & 1);
    }

    private long setBitToOne(long n, long m) {
        //将1左移m-1位找到第m位，得到000...1...000n在和这个数做或运算
        return n | (1L << (m - 1));
    }

    private long setBitToZero(long n, long m) {
        //将1左移m-1位找到第m位，取反后变成111...0...1111n再和这个数做与运算
        return n & ~(1L << (m - 1));
    }
}
