package com.netcracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    private ConsoleHelper(){
        throw new RuntimeException("Don't create instance of this class");
    }

    public static void writeMessage(String message){
        System.out.println(message);
    }

    public static String readMessage(){
        while(true) {
            try {
                return READER.readLine();
            } catch (IOException e) {
                writeMessage("Error. Please try again.");
            }
        }
    }

    public static int readInt(){
        while(true) {
            try {
                String one = readMessage();
                return Integer.parseInt(one);
            } catch (NumberFormatException e) {
                writeMessage("Can't recognize number. Please try again.");
            }
        }
    }

}
