package com.netcracker;

import com.netcracker.domain.Message;
import org.junit.Test;

import java.io.*;
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
    private static final Message testMessage = new Message(TEXT, "InOrderNotToGetEOFException");

    private void setUpConnectionObjectWithByteStreams() throws Exception {
        ByteArrayOutputStream outerOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream outerOOS = new ObjectOutputStream(outerOutputStream)) {
            outerOOS.writeObject(messageToReceive);
        }

        innerOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(innerOutputStream)) {
            oos.writeObject(testMessage);
        }
        originalNumberOfBytes = innerOutputStream.toByteArray().length;

        ByteArrayInputStream innerInputStream = new ByteArrayInputStream(outerOutputStream.toByteArray());
        setUpConnection(innerInputStream, innerOutputStream);
    }

    private void setUpConnectionObjectWithFileStreams() throws Exception {
        String resourcePath = "src\\test\\java\\com\\netcracker\\TestResource";

        FileOutputStream buffer = new FileOutputStream(resourcePath);
        try (ObjectOutputStream oos = new ObjectOutputStream(buffer)) {
            oos.writeObject(testMessage);
        }

        FileInputStream innerInputStream = new FileInputStream(resourcePath);
        FileOutputStream innerOutputStream = new FileOutputStream(resourcePath);
        setUpConnection(innerInputStream, innerOutputStream);
    }

    private void setUpConnection(InputStream is, OutputStream os) throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(os);
        when(socket.getInputStream()).thenReturn(is);
        when(socket.getRemoteSocketAddress()).thenReturn(address);
        connection = new Connection(socket);
    }

    @Test
    public void shouldSendMessageWhenAskingForIt() throws Exception {
        setUpConnectionObjectWithByteStreams();
        connection.send(messageToSend);

        assertTrue(innerOutputStream.toByteArray().length > originalNumberOfBytes);
    }

    @Test
    public void shouldReceiveMessageWhenAskingForIt() throws Exception {
        setUpConnectionObjectWithByteStreams();

        assertEquals(messageToReceive.getData(), connection.receive().getData());
    }

    @Test
    public void shouldReturnRemoteSocketAddressWhenAskingForIt() throws Exception {
        setUpConnectionObjectWithByteStreams();

        assertEquals(address, connection.getRemoteSocketAddress());
    }

    @Test(expected = RuntimeException.class)
    public void canNotGetMessagesWhenConnectionClosed() throws Exception {
        setUpConnectionObjectWithFileStreams();
        connection.close();

        connection.receive();
    }

    @Test(expected = RuntimeException.class)
    public void canNotSendMessagesWhenConnectionClosed() throws Exception {
        setUpConnectionObjectWithFileStreams();
        connection.close();

        connection.send(messageToSend);
    }
}