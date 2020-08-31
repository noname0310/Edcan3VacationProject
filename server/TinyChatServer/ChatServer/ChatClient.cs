using System;
using System.Collections.Generic;
using System.Text;
using TinyChatServer.Model;
using TinyChatServer.Server.ClientProcess;
using TinyChatServer.ChatServer.ChatLinker;
using Newtonsoft.Json.Linq;

namespace TinyChatServer.ChatServer
{
    public class ChatClient
    {
        public readonly ClientSocket ClientSocket;
        public readonly string UserEmail;

        public string Id { get; set; }
        public string Name { get; set; }
        public GPSdata GPSdata { get; set; }

        public List<ChatClient> LinkedClients;

        public ChatClient(ClientSocket clientSocket, string userEmail, string id, string name, GPSdata gPSdata)
        {
            ClientSocket = clientSocket;
            UserEmail = userEmail;

            Id = id;
            Name = name;
            GPSdata = gPSdata;
            LinkedClients = new List<ChatClient>();
        }

        public void OnClientMessageRecived(Message message)
        {
            if (UserEmail == message.ChatClient.UserEmail)
                return;

            SendData(message);

            foreach (var item in LinkedClients)
            {
                item.OnClientMessageRecived(message);
            }
        }

        public void SendData(Packet packet)
        {
            JObject jObject = JObject.FromObject(packet);
            ClientSocket.AsyncSend(jObject.ToString());
        }

        public void UpdateGPS(GPSdata gPSdata)
        {
            GPSdata = gPSdata;
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

        public GPSdata(Model.GPSdata gPSdata)
        {
            Longitude = gPSdata.Longitude;
            Latitude = gPSdata.Latitude;
        }
    }
}