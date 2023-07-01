package me.exeos.utils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static HashMap<String, byte[]> readZip(Path zipPath) throws IOException {
        HashMap<String, byte[]> files = new HashMap<>();
        ZipInputStream zip = new ZipInputStream(Files.newInputStream(zipPath));

        while (zip.getNextEntry() != null) {
            byte[] entryBytes = readZipEntry(zip);
            files.put(getName(entryBytes), entryBytes);
            zip.closeEntry();
        }

        return files;
    }

    public static void addFilesToZip(Path zipPath, HashMap<String, byte[]> files) throws IOException {
        File orgZip = new File(zipPath.toUri());
        File addedZip = File.createTempFile("temp" + System.currentTimeMillis() / 100, null);

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipPath));
             ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(addedZip.toPath()))) {

            /* Copy existing entries to addedZip */
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                writeZipEntry(zipOutputStream, zipEntry.getName(), readZipEntry(zipInputStream));
                zipInputStream.closeEntry();
            }

            /* Add files to temp addedZip */
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                writeZipEntry(zipOutputStream, entry.getKey(), entry.getValue());
            }
        }

        /* Replace zip file with addedZip  */
        orgZip.delete();
        addedZip.renameTo(orgZip);
    }

    private static String getName(byte[] entry) {
        ClassNode classNode = new ClassNode();
        new ClassReader(entry).accept(classNode, 7);

        return classNode.name;
    }

    private static byte[] readZipEntry(ZipInputStream from) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = from.read(buffer)) != -1) {
            byteStream.write(buffer, 0, bytesRead);
        }

        return byteStream.toByteArray();
    }

    private static void writeZipEntry(ZipOutputStream outputStream, String name, byte[] content) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        entry.setSize(content.length);

        outputStream.putNextEntry(entry);
        outputStream.write(content);
        outputStream.closeEntry();
    }
}
