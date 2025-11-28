package org.example;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class CryptoFilterReader extends FilterReader {
    private final byte[] keyBytes;
    private int position = 0;

    public CryptoFilterReader(Reader in, String key) {
        super(in);
        this.keyBytes = key.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public int read() throws IOException {
        int chInt = super.read();
        if (chInt == -1) return -1;
        int keyByte = keyBytes[position % keyBytes.length] & 0xFF;
        position = (position + 1) % keyBytes.length;

        return ((chInt - keyByte) & 0xFFFF);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int numRead = super.read(cbuf, off, len);

        if (numRead == -1) return -1;
        for (int i = 0; i < numRead; i++) {
            int keyByte = keyBytes[(position + i) % keyBytes.length] & 0xFF;
            cbuf[off + i] = (char) (((int) cbuf[off + i] - keyByte) & 0xFFFF);
        }

        position = (position + numRead) % keyBytes.length;
        return numRead;
    }
}
