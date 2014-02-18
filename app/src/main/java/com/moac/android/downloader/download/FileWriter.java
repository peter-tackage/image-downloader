package com.moac.android.downloader.download;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileWriter {

    private static final String TAG = FileWriter.class.getSimpleName();

    public void write(InputStream inputStream, String fileDestination, long contentLength) throws IOException {

        File output = new File(fileDestination);
        if (output.exists() && output.isFile()) {
            output.delete();
        }

        BufferedOutputStream fos = null;
        try {
            fos = new BufferedOutputStream(new FileOutputStream(output.getPath()));
            final int BUFFER_SIZE = 4096;
            byte[] buffer = new byte[BUFFER_SIZE];
            int totalBytesRead = 0;
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
            fos.flush();
            if (totalBytesRead < contentLength) {
                throw new IOException("Read " + bytesRead + " from stream, was expecting " + contentLength);
            }
            Log.i(TAG, "Output file is apparently size: " + output.length());
        } finally {
            Utils.closeQuietly(fos);
        }
    }
}
