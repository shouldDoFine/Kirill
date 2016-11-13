package com.netcracker;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

public class ConsoleHelperTest {
    private static final PrintStream SYSTEM_OUT = System.out;
    @Rule
    public final TextFromStandardInputStream systemIn = emptyStandardInputStream();
    private OutputStream resultMessageStream;

    @Before
    public void setUpEachTest() {
        resultMessageStream = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(resultMessageStream);
        System.setOut(outputStream);
    }

    @Before
    public void reloadReaderInConsoleHelperClass() throws NoSuchFieldException, IllegalAccessException {
        Field bufferedReader = ConsoleHelper.class.getDeclaredField("READER");
        bufferedReader.setAccessible(true);
        Field modifiers = bufferedReader.getClass().getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(bufferedReader, bufferedReader.getModifiers() & ~Modifier.FINAL);
        bufferedReader.set(BufferedReader.class, new BufferedReader(new InputStreamReader(System.in)));
    }

    @AfterClass
    public static void oneTimeTearDown() {
        System.setOut(SYSTEM_OUT);
    }

    @Test
    public void writeMessageShouldPrintMessage() {
        ConsoleHelper.writeMessage("Hello");
        String rez = resultMessageStream.toString().replaceAll("\\r|\\n", "");
        assertEquals("Hello", rez);
    }

    @Test
    public void readMessageTest() {
        systemIn.provideLines("hi");
        String message = ConsoleHelper.readMessage();
        assertEquals("hi", message);
    }

    @Test
    public void readIntTest() throws IOException {
        systemIn.provideLines("54646");
        int number = ConsoleHelper.readInt();
        assertEquals(54646, number);
    }

    @Test
    public void readIntTestShouldWriteError() {
        systemIn.provideLines("54646", "string", "54646");
        int firstNumber = ConsoleHelper.readInt();
        int secongNumber = ConsoleHelper.readInt();
        String resultMessage = resultMessageStream.toString();
        String expected = "Can't recognize number. Please try again.";
        assertEquals(expected, resultMessage.replaceAll("\\r|\\n", ""));
    }

}