package me.exeos.utils;

public class EncryptUtils {

    public static byte[] xor(byte[] data, byte key) {
        byte[] encrypted = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            encrypted[i] = (byte) (data[i] ^ key);
        }
        return encrypted;
    }
}