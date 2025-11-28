package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CipherUtils {

    public static void encryptStream(InputStream in, OutputStream out, String key) throws IOException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        int keyLen = keyBytes.length;
        if (keyLen == 0) throw new IllegalArgumentException("Key must be non-empty");

        int index = 0;
        int byteRead;
        while ((byteRead = in.read()) != -1) {
            int keyByte = keyBytes[index % keyLen] & 0xFF;
            int encodedByte = (byteRead + keyByte) & 0xFF;

            out.write(encodedByte);
            index++;
        }
    }

    public static void decryptStream(InputStream in, OutputStream out, String key) throws IOException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        int keyLen = keyBytes.length;
        if (keyLen == 0) throw new IllegalArgumentException("Key must be non-empty");

        int index = 0;
        int byteRead;
        while ((byteRead = in.read()) != -1) {
            int keyByte = keyBytes[index % keyLen] & 0xFF;
            int decodedByte = (byteRead - keyByte) & 0xFF;

            out.write(decodedByte);
            index++;
        }
    }
}
