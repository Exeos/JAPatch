package me.exeos;

import me.exeos.utils.EncryptUtils;
import me.exeos.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AgentPatcher {

    private final String operatingPath = "p_pack/";
    private final String patchClassPrefix = "E_CP=";
    private final String keyFile = "E_K";

    private final Path agentPath;
    private final Path patchClassesPath;

    private final byte key;

    public AgentPatcher(String agentPath, String patchClassesPath) {
        this.agentPath = Paths.get(agentPath);
        this.patchClassesPath = Paths.get(patchClassesPath);

        key = (byte) new Random().nextInt(256);
    }

    public void patch() {
        HashMap<String, byte[]> patcherPack;
        try {
            patcherPack = getEncryptedPatchClasses(ZipUtils.readZip(patchClassesPath));
            patcherPack.put(operatingPath + keyFile, new byte[] { key });
        } catch (IOException e) {
            System.out.println("Failed to create patch pack");
            throw new RuntimeException(e);
        }

        try {
            Path output = new File("patcher.jar").toPath();
            Files.copy(agentPath, output, StandardCopyOption.REPLACE_EXISTING);
            ZipUtils.addFilesToZip(output, patcherPack);
        } catch (IOException e) {
            System.out.println("Failed to create patcher");
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, byte[]> getEncryptedPatchClasses(HashMap<String, byte[]> files) {
        HashMap<String, byte[]> encryptedFiles = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : files.entrySet()) {
            String encryptedName = new String(EncryptUtils.xor(entry.getKey().getBytes(), key));
            byte[] encryptedData = EncryptUtils.xor(entry.getValue(), key);

            encryptedFiles.put(operatingPath + patchClassPrefix + encryptedName, encryptedData);
        }

        return encryptedFiles;
    }
}
