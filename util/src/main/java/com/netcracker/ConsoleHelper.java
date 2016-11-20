package com.netcracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    private ConsoleHelper() {
        throw new RuntimeException("Don't create instance of this class");
    }

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readMessage() throws IOException {
        return READER.readLine();
    }

    public static int readInt() throws IOException {
        return Integer.parseInt(readMessage());
    }

}
