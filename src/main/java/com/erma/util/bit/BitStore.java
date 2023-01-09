package com.erma.util.bit;


/**
 * @Date 2023/1/6 17:52
 * @Created by yzfeng
 */
public class BitStore {
    private final long length;
    private final long[] value;

    public BitStore(long length) {
        long size = length % 64 == 0 ? length >> 6 : length >> 6 + 1;
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("The length must be less than " + Integer.MAX_VALUE * 64L);
        }
        this.length = length;
        value = new long[(int) size];
    }

    public byte get(long index) {
        Position position = getPosition(index);
        return getBit(value[position.getSeq()], position.getNewIndex());
    }

    private Position getPosition(long index) {
        if (index > length) {
            throw new IllegalArgumentException("The index cannot be greater than " + length);
        }
        return new Position((int) (index >> 6L), index % 64);
    }

    public void setOne(long index) {
        Position position = getPosition(index);
        value[position.getSeq()] = setBitToOne(value[position.getSeq()], position.getNewIndex());
    }

    public void setZero(long index) {
        Position position = getPosition(index);
        value[position.getSeq()] = setBitToZero(value[position.getSeq()], position.getNewIndex());
    }

    private byte getBit(long n, long m) {
        return (byte) ((n >> (m - 1L)) & 1L);
    }

    private long setBitToOne(long n, long m) {
        //将1左移m-1位找到第m位，得到000...1...000n在和这个数做或运算
        return n | (1L << (m - 1L));
    }

    private long setBitToZero(long n, long m) {
        //将1左移m-1位找到第m位，取反后变成111...0...1111n再和这个数做与运算
        return n & ~(1L << (m - 1L));
    }

    private class Position {
        private int seq;
        private long newIndex;

        public Position(int seq, long newIndex) {
            this.seq = seq;
            this.newIndex = newIndex;
        }

        public int getSeq() {
            return seq;
        }

        public long getNewIndex() {
            return newIndex;
        }
    }
}
