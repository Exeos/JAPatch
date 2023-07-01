package me.exeos;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Paths;

public class AgentMain {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        agentmain(agentArgs, instrumentation);
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        if (agentArgs == null) {
            throw new IllegalArgumentException("Please provide path to patcher jar");
        }

        agentArgs = agentArgs.replace("\\", "/");

        try {
            PatcherLoader.loadPatcherPack(Paths.get(agentArgs));
        } catch (IOException e) {
            System.out.println("Failed to load patcher pack");
            throw new RuntimeException(e);
        }
        PatcherLoader.decryptClassPatches();

        try {
            instrumentation.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
                if (PatcherLoader.CLASS_PATCHES.getOrDefault(className, null) != null)
                    return PatcherLoader.CLASS_PATCHES.get(className);
                else
                    return classfileBuffer;
            });
        } catch (Exception e) {
            System.out.println("Failed to add transformer");
            e.printStackTrace();
        }
    }
}