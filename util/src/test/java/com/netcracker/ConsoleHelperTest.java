package com.netcracker;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

public class ConsoleHelperTest {
    private static final PrintStream SYSTEM_OUT = System.out;

    @Rule
    public final TextFromStandardInputStream systemIn = emptyStandardInputStream();
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private OutputStream systemOut;

    @Before
    public void setUpEachTest() {
        this.systemOut = new ByteArrayOutputStream();
        PrintStream outputStream = new PrintStream(systemOut);
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
        String rez = systemOut.toString().replaceAll("\\r|\\n", "");
        assertEquals("Hello", rez);
    }

    @Test
    public void shouldReturnMessageWrittenToInputStream() {
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
        String resultMessage = systemOut.toString();
        assertEquals("Can't recognize number. Please try again.", resultMessage.replaceAll("\\r|\\n", ""));
    }

    @Test
    public void exceptionWhenTryToCreateInstanceByReflection() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor<ConsoleHelper> constructor = ConsoleHelper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        exception.expect(InvocationTargetException.class);
        constructor.newInstance();
    }

}