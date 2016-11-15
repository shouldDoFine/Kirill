package com.netcracker;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
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
    @Rule
    public final SystemOutRule systemOut = new SystemOutRule().enableLog();
    @Rule
    public final TextFromStandardInputStream systemIn = emptyStandardInputStream();
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void reloadReaderInConsoleHelperClass() throws NoSuchFieldException, IllegalAccessException {
        Field bufferedReader = ConsoleHelper.class.getDeclaredField("READER");
        bufferedReader.setAccessible(true);
        Field modifiers = bufferedReader.getClass().getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(bufferedReader, bufferedReader.getModifiers() & ~Modifier.FINAL);
        bufferedReader.set(BufferedReader.class, new BufferedReader(new InputStreamReader(System.in)));
    }

    @Test
    public void shouldWriteMessageToSystemOut() {
        ConsoleHelper.writeMessage("Hello");

        String rez = systemOut.getLog().replaceAll("\\n|\\r", "");
        assertEquals("Hello", rez);
    }

    @Test
    public void shouldReturnMessageWrittenToInputStream() throws IOException {
        systemIn.provideLines("hi");

        String message = ConsoleHelper.readMessage();

        assertEquals("hi", message);
    }

    @Test
    public void shouldBeAllowToReadIntFromInputStream() throws IOException {
        systemIn.provideLines("54646");

        int number = ConsoleHelper.readInt();

        assertEquals(54646, number);
    }

    @Test
    public void shouldThrowWhenAttemptingToReadGarbageAsInt() throws IOException {
        systemIn.provideLines("54646", "string");

        ConsoleHelper.readInt();
        exception.expect(NumberFormatException.class);
        ConsoleHelper.readInt();
    }

    @Test
    public void shouldThrowWhenAttemptingToCreateClassInstance() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor<ConsoleHelper> constructor = ConsoleHelper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        exception.expect(InvocationTargetException.class);
        constructor.newInstance();
    }

}