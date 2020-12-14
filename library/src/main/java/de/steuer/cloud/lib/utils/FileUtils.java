package de.steuer.cloud.lib.utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class FileUtils {

    public static void writeFile(final File file, final InputStream inputStream) throws IOException {
        if(inputStream == null)
            throw new IllegalArgumentException("InputStream cannot be null!");

        if(!file.exists())
            file.createNewFile();

        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        final byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1)
            fileOutputStream.write(buffer, 0, length);

        fileOutputStream.close();
        inputStream.close();
    }

    public static void writeFile(final File file, final String contents) throws IOException {
        writeFile(file, new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8)));
    }

    public static void writeFile(final String fileName, final InputStream inputStream) throws IOException {
        writeFile(new File(fileName), inputStream);
    }

    public static void writeFile(final String fileName, final String contents) throws IOException {
        writeFile(fileName, new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8)));
    }

    public static String readFile(final Reader reader) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder stringBuilder = new StringBuilder();

        while (bufferedReader.readLine() != null) {
            stringBuilder.append(stringBuilder.length() != 0 ? "\n" : "");
            stringBuilder.append(bufferedReader.readLine());
        }

        bufferedReader.close();
        reader.close();

        return stringBuilder.toString();
    }

    public static String readFile(final InputStream inputStream) throws IOException {
        return readFile(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    public static String readFile(final String fileName) throws IOException {
        final File file = new File(fileName);

        if(!file.exists() || file.isDirectory())
            throw new FileNotFoundException();

        return readFile(new FileInputStream(file));
    }

    public static String readFile(final File file) throws IOException {
        if(!file.exists() || file.isDirectory())
            throw new FileNotFoundException();

        return readFile(new FileInputStream(file));
    }

    public static void copyFile(final File src, final File dest) throws IOException {
        if(!src.exists())
            throw new FileNotFoundException();

        if(src.isDirectory() || dest.isDirectory()) {
            throw new FileNotFoundException();
        }

        if(!dest.exists())
            dest.createNewFile();

        final FileInputStream fileInputStream = new FileInputStream(src);
        final FileOutputStream fileOutputStream = new FileOutputStream(dest);

        fileInputStream.getChannel().transferTo(0, fileInputStream.getChannel().size(), fileOutputStream.getChannel());

        fileInputStream.close();
        fileOutputStream.close();
    }
}
