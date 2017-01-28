package com.netcracker;

import com.netcracker.domain.Message;
import org.junit.Test;

import static com.netcracker.domain.MessageType.NAME_REQUEST;
import static com.netcracker.domain.MessageType.TEXT;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class MessageTest {
    @Test
    public void shouldReturnMessageTypeWhenAskingForIt() {
        Message message = new Message(TEXT, "Message text");

        assertEquals(message.getMessageType(), TEXT);
    }

    @Test
    public void shouldReturnTextWhenAskingForIt() {
        Message message = new Message(TEXT, "Message text");

        assertEquals("Message text", message.getData());
    }

    @Test
    public void shouldContainEmptyDataWhenNonWasProvided() {
        Message message = new Message(NAME_REQUEST);

        assertTrue(message.getData().isEmpty());
    }
}