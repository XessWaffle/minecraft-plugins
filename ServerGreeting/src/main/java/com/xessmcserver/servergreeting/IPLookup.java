package com.xessmcserver.servergreeting;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class IPLookup {

    private static class IPRange implements Comparable<IPRange> {
        public long from, to;
        public IPRange(long from, long to){
            this.from = from;
            this.to = to;
        }

        public boolean isWithin(int ip) {
            return from <= ip && to >= ip;
        }

        @Override
        public int compareTo(IPRange o) {
            return (int)(o.from - this.from);
        }
    }

    private static File IPDBfile;

    private static TreeMap<IPRange, String> IPDB;

    public static void initializeIPDB(File dataFolder) throws IOException {
        IPDBfile = new File(dataFolder.getPath() + "/dbip-country-ipv4-num.csv");

        BufferedReader reader = new BufferedReader(new FileReader(IPDBfile));
        IPDB = new TreeMap<>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");

            IPRange range = new IPRange(Long.parseLong(data[0]), Long.parseLong(data[1]));
            String country = data[2];

            IPDB.putIfAbsent(range, country);
        }
    }

    public static String getIPLocation(InetAddress ip) {
        long result = 0;
        for (byte b: ip.getAddress())
        {
            result = result << 8 | (b & 0xFF);
        }

        IPRange check = new IPRange(result, 0);
        IPRange higher = IPDB.higherKey(check);
        String country = IPDB.higherEntry(check).getValue();

        System.out.println(result + ":" + higher.from + "->" + higher.to);
        return country;
    }

    public static void main(String[] args)
    {
        try {
            IPLookup.initializeIPDB(new File("data"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[][] publicTestIPByteArrays = {
                new byte[]{(byte) 108, (byte) 235, (byte) 203, (byte) 150},
                new byte[]{(byte) 95, (byte) 89, (byte) 151, (byte) 31},
                new byte[]{(byte) 104, (byte) 1, (byte) 154, (byte) 203},
                new byte[]{(byte) 23, (byte) 115, (byte) 82, (byte) 215},
                new byte[]{(byte) 192, (byte) 0, (byte) 2, (byte) 1},
                new byte[]{(byte) 8, (byte) 8, (byte) 4, (byte) 4},
                new byte[]{(byte) 74, (byte) 125, (byte) 224, (byte) 72},
                new byte[]{(byte) 23, (byte) 21, (byte) 0, (byte) 80},
                new byte[]{(byte) 34, (byte) 199, (byte) 87, (byte) 3},
                new byte[]{(byte) 50, (byte) 16, (byte) 20, (byte) 205},
                new byte[]{(byte) 3, (byte) 19, (byte) 78, (byte) 192},
                new byte[]{(byte) 18, (byte) 223, (byte) 32, (byte) 32}
        };

        for(int i = 0; i < publicTestIPByteArrays.length; i++) {
            try {
                InetAddress testAddress = InetAddress.getByAddress(publicTestIPByteArrays[i]);
                System.out.println(testAddress.toString() + ":" + IPLookup.getIPLocation(testAddress));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }


        }

    }

}
