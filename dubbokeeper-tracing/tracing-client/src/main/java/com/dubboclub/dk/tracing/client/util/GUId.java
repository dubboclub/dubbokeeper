package com.dubboclub.dk.tracing.client.util;

import com.alibaba.dubbo.common.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.UUID;

/**
 * Created by Zetas on 2016/7/8.
 */
public class GUId {

    private final long maxId = 1 << 10;
    private final long maxSequence = 1 << 12;

    private final short timeShiftLeft = 64;
    private final short sequenceShiftLeft = 42;
    private final short idShiftLeft = 32;
    private final short ipShiftLeft = 0;

    private BigInteger ip;
    private BigInteger id;

    private long sequence;
    private long lastTimestamp;

    private static class Holder {
        private static GUId instance;

        static {
            try {
                instance = new GUId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private GUId() throws IOException {
        long ipTmp = ip();
        long idTmp = id();

        if (ipTmp < 0 || idTmp < 0) {
            throw new IllegalArgumentException("can not be less than 0");
        }

        ip = BigInteger.valueOf(ipTmp).shiftLeft(ipShiftLeft);
        id = BigInteger.valueOf(idTmp).shiftLeft(idShiftLeft);
    }

    public static GUId singleton() {
        return Holder.instance;
    }

    /*public synchronized String nextId() {
        long timestamp = timeGen();
        if (lastTimestamp == timestamp) {
            sequence = sequence + 1 & maxSequence;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        BigInteger time = BigInteger.valueOf(timestamp).shiftLeft(timeShiftLeft);
        BigInteger seq = BigInteger.valueOf(sequence).shiftLeft(sequenceShiftLeft);
        return time.or(seq).or(id).or(ip).toString(32);
    }*/

    public synchronized long nextId() {
        return UUID.randomUUID().getMostSignificantBits();
    }

    private long ip() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        byte[] address = inetAddress.getAddress();
        long a = (long) (address[0] & 0xff) << 24;
        long b = (long) (address[1] & 0xff) << 16;
        long c = (long) (address[2] & 0xff) << 8;
        long d = (long) (address[3] & 0xff);
        return a | b | c | d;
    }

    private long id() throws IOException {
        String userHome = System.getProperty("user.home");

        if (StringUtils.isNotEmpty(userHome)) {
            File dstDirectory = new File(userHome, ".dst");
            boolean dstDirectoryExists = dstDirectory.exists();
            if (!dstDirectoryExists) {
                dstDirectoryExists = dstDirectory.mkdir();
            }
            if (dstDirectoryExists) {
                File lock = new File(dstDirectory, "unique-id.lock");
                boolean lockExists = dstDirectory.exists();
                if (!lockExists) {
                    lockExists = lock.createNewFile();
                }
                if (lockExists) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(lock, "rw");
                    FileChannel fileChannel = randomAccessFile.getChannel();
                    for (int i = 0; i < maxId; i++) {
                        if (fileChannel.tryLock(i, 1, false) != null) {
                            return i;
                        }
                    }
                }
            }
        }

        return -1;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private static long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(GUId.singleton().nextId());
        }
    }
}
