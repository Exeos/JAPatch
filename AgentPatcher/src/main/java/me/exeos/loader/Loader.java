package me.exeos.loader;

import me.exeos.AgentPatcher;

public class Loader {
    public static void main(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException("Invalid args, start with: [argent_to_patch] [patch_classes]");

        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].replace("\\", "/");
        }

        System.out.println("starting patch...");
        new AgentPatcher(args[0], args[1]).patch();
        System.out.println("done!");
    }
}