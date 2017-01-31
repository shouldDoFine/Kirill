package com.netcracker;

import com.netcracker.domain.Message;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

import static com.netcracker.domain.MessageType.TEXT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectionTest {
    private static Connection connection;
    private static ByteArrayOutputStream innerOutputStream;
    private static int originalNumberOfBytes;
    private static final SocketAddress address = mock(SocketAddress.class);
    private static final Message messageToReceive = new Message(TEXT, "MessageToReceive");
    private static final Message messageToSend = new Message(TEXT, "MessageToSend");


    @BeforeClass
    public static void setUpConnectionObject() throws IOException {
        ByteArrayOutputStream outerOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outerOOS = new ObjectOutputStream(outerOutputStream);
        outerOOS.writeObject(messageToReceive);
        outerOOS.close();
        ByteArrayInputStream innerInputStream = new ByteArrayInputStream(outerOutputStream.toByteArray());

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buffer);
        oos.writeObject(new Message(TEXT, "InOrderNotToGetEOFException"));
        oos.close();
        byte[] rawData = buffer.toByteArray();
        originalNumberOfBytes = rawData.length;

        innerOutputStream = new ByteArrayOutputStream();
        innerOutputStream.write(rawData);

        Socket socket = mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(innerOutputStream);
        when(socket.getInputStream()).thenReturn(innerInputStream);
        when(socket.getRemoteSocketAddress()).thenReturn(address);

        connection = new Connection(socket);
    }

    @Test
    public void shouldSendMessageWhenAskingForIt() throws IOException, ClassNotFoundException {
        connection.send(messageToSend);

        assertTrue(innerOutputStream.toByteArray().length > originalNumberOfBytes);
    }

    @Test
    public void shouldReceiveMessageWhenAskingForIt() throws IOException, ClassNotFoundException {
        assertEquals(messageToReceive.getData(), connection.receive().getData());
    }

    @Test
    public void shouldReturnRemoteSocketAddressWhenAskingForIt() {
        assertEquals(address, connection.getRemoteSocketAddress());
    }
}