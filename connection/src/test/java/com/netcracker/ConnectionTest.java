package com.netcracker;

import com.netcracker.domain.Message;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketAddress;

import static com.netcracker.domain.MessageType.TEXT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ConnectionTest {
    private final Connection testConnection = mock(Connection.class);

    @Test
    public void shouldSentMessageWhenAskingForIt() throws IOException {
        Message messageShouldBeSent = new Message(TEXT, "messageShouldBeSent");

        testConnection.send(messageShouldBeSent);

        verify(testConnection).send(messageShouldBeSent);
    }

    @Test
    public void shouldReceiveMessageWhenAskingForIt() throws IOException, ClassNotFoundException {
        Message messageShouldBeReceived = new Message(TEXT, "messageShouldBeReceived");

        when(testConnection.receive()).thenReturn(messageShouldBeReceived);

        assertEquals(messageShouldBeReceived, testConnection.receive());
    }

    @Test
    public void shouldReturnRemoteSocketAddressWhenAskingForIt() {
        SocketAddress socketAddress = mock(SocketAddress.class);

        when(testConnection.getRemoteSocketAddress()).thenReturn(socketAddress);

        assertEquals(socketAddress, testConnection.getRemoteSocketAddress());
    }
}