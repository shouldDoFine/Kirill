package com.netcracker;

import com.netcracker.domain.Message;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    public void send(Message message) {
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("Невозможно отправить сообщение!");
        }
    }

    public Message receive() throws ClassNotFoundException {
        try {
            return (Message) inputStream.readObject();
        } catch (IOException e) {
            throw new RuntimeException("Невозможно получить сообщение!");
        }
    }

    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
        inputStream.close();
        socket.close();
    }
}
