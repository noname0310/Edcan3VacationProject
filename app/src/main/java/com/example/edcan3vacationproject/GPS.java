package com.example.edcan3vacationproject;

public class GPS extends Packet {
    public GPSdata GPSdata;

    public GPS() {
        PacketType = PacketType.GPS;
    }

    public GPS(GPSdata gPSdata) {
        this();
        GPSdata = gPSdata;
    }
}

