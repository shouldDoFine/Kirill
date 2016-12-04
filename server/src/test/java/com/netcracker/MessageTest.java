package com.netcracker;

import com.netcracker.domain.Message;
import org.junit.Test;

import static com.netcracker.domain.MessageType.NAME_REQUEST;
import static com.netcracker.domain.MessageType.TEXT;
import static org.junit.Assert.assertEquals;

public class MessageTest {
    @Test
    public void shouldReturnMessageTypeWhenTryingToGet() {
        Message message = new Message(TEXT, "Message text");

        assertEquals(message.getMessageType(), TEXT);
    }

    @Test
    public void shouldReturnTextWhenTryingToGet() {
        Message message = new Message(TEXT, "Message text");

        assertEquals(message.getData(), "Message text");
    }

    @Test
    public void givenMessageWithoutTextWhenTryingToGetDataThenReturnNullData() {
        Message message = new Message(NAME_REQUEST);

        assertEquals(true, message.getData().isEmpty());
    }
}