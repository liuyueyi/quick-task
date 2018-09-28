package com.git.hui.task.plugin.ws.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

/**
 * Created by @author yihui in 16:16 18/9/26.
 */
public class EncrypteUtil {
    /**
     * uncompress data
     *
     * @param bytes
     * @return
     */
    public static String gizpDec(byte[] bytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        GZIPInputStream gzis = new GZIPInputStream(bais);
        byte[] buffer = new byte[1024];
        int n;
        while ((n = gzis.read(buffer)) >= 0) {
            baos.write(buffer, 0, n);
        }
        return baos.toString();
    }

    public static String deflaterDec(byte[] input, boolean nowrap) throws IOException, DataFormatException {
        Inflater inflater = new Inflater(nowrap);
        inflater.setInput(input);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
        try {
            byte[] buff = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buff);
                baos.write(buff, 0, count);
            }
        } finally {
            baos.close();
        }
        inflater.end();
        byte[] output = baos.toByteArray();
        return new String(output);
    }
}
