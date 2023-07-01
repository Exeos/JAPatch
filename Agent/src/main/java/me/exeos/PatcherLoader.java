package me.exeos;

import me.exeos.utils.EncryptUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PatcherLoader {

    private static final String OPERATING_PATH = "p_pack/";
    private static final String PATCH_CLASS_FILE = "E_CP=";
    private static final String KEY_FILE = "E_K";

    private static final HashMap<String, byte[]> ENC_CLASS_PATCHES = new HashMap<>();
    public static final HashMap<String, byte[]> CLASS_PATCHES = new HashMap<>();

    private static byte key;

    public static void loadPatcherPack(Path patcherPath) throws IOException {
        ZipInputStream zip = new ZipInputStream(Files.newInputStream(patcherPath));
        ZipEntry zipEntry;

        while ((zipEntry = zip.getNextEntry()) != null) {
            if (zipEntry.getName().startsWith(OPERATING_PATH + PATCH_CLASS_FILE)) {
                int subStrLength = (OPERATING_PATH + PATCH_CLASS_FILE).length();
                ENC_CLASS_PATCHES.put(zipEntry.getName().substring(subStrLength), readZipEntry(zip));
            } else if (zipEntry.getName().equals(OPERATING_PATH + KEY_FILE))
                key = readZipEntry(zip)[0];

            zip.closeEntry();
        }
    }

    public static void decryptClassPatches() {
        for (Map.Entry<String, byte[]> entry : ENC_CLASS_PATCHES.entrySet()) {
            String name = new String(EncryptUtils.xor(entry.getKey().getBytes(), key));
            byte[] data = EncryptUtils.xor(entry.getValue(), key);

            CLASS_PATCHES.put(name, data);
        }
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
}