namespace TinyChatServer.Model
{
    public enum PacketType
    {
        ClientConnected,
        ClientDisConnect,
        Message,
        GPS
    }

    public class Packet
    {
        public PacketType PacketType;
    }

    public class Message : Packet
    {
        public ChatClient ChatClient;
        public string Msg;

        public Message()
        {
            PacketType = PacketType.Message;
        }

        public Message(ChatClient chatClient, string msg) : this()
        {
            ChatClient = chatClient;
            Msg = msg;
        }
    }

    class GPS : Packet
    {
        public GPSdata GPSdata;

        public GPS()
        {
            PacketType = PacketType.GPS;
        }

        public GPS(GPSdata gPSdata) : this()
        {
            GPSdata = gPSdata;
        }
    }

    class ClientConnected : Packet
    {
        public ChatClient ChatClient;
        public GPSdata GPSdata;

        public ClientConnected()
        {
            PacketType = PacketType.ClientConnected;
        }

        public ClientConnected(ChatClient chatClient, GPSdata gPSdata) : this()
        {
            ChatClient = chatClient;
            GPSdata = gPSdata;
        }
    }

    class ClientDisConnect : Packet
    {
        public ClientDisConnect()
        {
            PacketType = PacketType.ClientDisConnect;
        }
    }

    public class ChatClient
    {
        public string Id;
        public string Name;
        public string UserEmail;

        public ChatClient()
        {

        }

        public ChatClient(string id, string name, string userEmail)
        {
            Id = id;
            Name = name;
            UserEmail = userEmail;
        }
    }

    public struct GPSdata
    {
        public double Longitude;
        public double Latitude;

        public GPSdata(double longitude, double latitude)
        {
            Longitude = longitude;
            Latitude = latitude;
        }
    }
}