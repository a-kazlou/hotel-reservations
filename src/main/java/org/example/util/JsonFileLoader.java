package org.example.util;

import org.example.exception.DataLoadingException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonFileLoader {
    private static final String EXTERNAL_FILE_READ_EXCEPTION = "Failed to read external file: ";
    private static final String FILE_READ_EXCEPTION = "File not found in resources: ";
    public static byte[] loadFile(String filePath) throws DataLoadingException {

        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return readAllBytes(path);
            }
        } catch (Exception e) {
            System.err.println(EXTERNAL_FILE_READ_EXCEPTION + e.getMessage());
        }

        try (InputStream is = JsonFileLoader.class.getResourceAsStream(filePath)) {
            if (is == null) {
                throw new DataLoadingException(FILE_READ_EXCEPTION + filePath);
            }
            return readAllBytes(is);
        } catch (IOException e) {
            throw new DataLoadingException(FILE_READ_EXCEPTION + filePath, e);
        }
    }

    private static byte[] readAllBytes(Path path) throws IOException {
        try (InputStream is = Files.newInputStream(path)) {
            return readAllBytes(is);
        }
    }

    private static byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}