package com.example.edcan3vacationproject;

public class Message extends Packet{
    public ChatClient ChatClient;

    public String Msg;

    public Message()
    {
        PacketType = PacketType.Message;
    }



    public Message(ChatClient chatClient, String msg) {
        this();
        ChatClient = chatClient;
        Msg = msg;
    }
}
