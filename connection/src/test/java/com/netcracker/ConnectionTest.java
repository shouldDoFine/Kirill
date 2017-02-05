package com.netcracker;

import com.netcracker.domain.Message;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
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
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUpConnectionObjectWithByteStreams() throws IOException {
        ByteArrayOutputStream outerOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream outerOOS = new ObjectOutputStream(outerOutputStream)) {
            outerOOS.writeObject(messageToReceive);
        }
        ByteArrayInputStream innerInputStream = new ByteArrayInputStream(outerOutputStream.toByteArray());

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(buffer)) {
            oos.writeObject(new Message(TEXT, "InOrderNotToGetEOFException"));
        }
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

    public void setUpConnectionWithFileStream() throws IOException {
        FileOutputStream buffer = new FileOutputStream("src\\test\\" +
                "java\\com\\netcracker\\TestResource");

        try (ObjectOutputStream oos = new ObjectOutputStream(buffer)) {
            oos.writeObject(messageToSend);
        }

        FileInputStream innerInputStream = new FileInputStream("src\\test\\" +
                "java\\com\\netcracker\\TestResource");

        FileOutputStream innerOutputStream = new FileOutputStream("src\\test\\" +
                "java\\com\\netcracker\\TestResource");

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

    @Test
    public void canNotGetMessagesWhenConnectionClosed() throws ClassNotFoundException, IOException {
        setUpConnectionWithFileStream();
        connection.close();

        exception.expect(RuntimeException.class);
        connection.receive();
    }

    @Test
    public void canNotSendMessagesWhenConnectionClosed() throws ClassNotFoundException, IOException {
        setUpConnectionWithFileStream();
        connection.close();

        exception.expect(RuntimeException.class);
        connection.send(messageToSend);
    }
}