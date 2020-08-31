package com.example.edcan3vacationproject;

class ClientConnected extends Packet {
    public ChatClient ChatClient;
    public GPSdata GPSdata;

    public ClientConnected() {
        PacketType = PacketType.ClientConnected;
    }

    public ClientConnected(ChatClient chatClient, GPSdata gPSdata){
        this();
        ChatClient = chatClient;
        GPSdata = gPSdata;
    }
}
