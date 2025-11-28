package org.example;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class CryptoFilterWriter extends FilterWriter {
    private final byte[] keyBytes;
    private int position = 0;

    public CryptoFilterWriter(Writer out, String key) {
        super(out);
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must be non-empty");
        }
        this.keyBytes = key.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        char[] transformed = new char[len];

        for (int i = 0; i < len; i++) {
            int keyByte = keyBytes[(position + i) % keyBytes.length] & 0xFF;
            transformed[i] = (char) (((int) cbuf[off + i] + keyByte) & 0xFFFF);
        }

        position = (position + len) % keyBytes.length;
        super.write(transformed, 0, len);
    }

    @Override
    public void write(int c) throws IOException {
        int keyByte = keyBytes[position % keyBytes.length] & 0xFF;
        position = (position + 1) % keyBytes.length;
        super.write((char) ((c + keyByte) & 0xFFFF));
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        write(str.toCharArray(), off, len);
    }
}
