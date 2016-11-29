package com.netcracker;

import com.netcracker.domain.Message;
import org.junit.Test;

import static com.netcracker.domain.MessageType.NAME_REQUEST;
import static com.netcracker.domain.MessageType.TEXT;
import static org.junit.Assert.assertEquals;

public class MessageTest {
    @Test
    public void shouldReturnMessageTypeAndDataWhenTryToGet() {
        Message message = new Message(TEXT, "Message text");

        assertEquals(message.getMessageType(), TEXT);
        assertEquals(message.getData(), "Message text");
    }

    @Test
    public void givenMessageWithoutTextShouldReturnNullDataWhenTryToGet() {
        Message message = new Message(NAME_REQUEST);

        assertEquals(message.getData(), null);
    }
}