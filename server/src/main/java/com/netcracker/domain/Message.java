package com.netcracker.domain;

public class Message {
    private final MessageType messageType;
    private final String data;

    public Message(MessageType messageType) {
        this.messageType = messageType;
        this.data = "";
    }

    public Message(MessageType messageType, String data) {
        this.messageType = messageType;
        this.data = data;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getData() {
        return data;
    }
}
