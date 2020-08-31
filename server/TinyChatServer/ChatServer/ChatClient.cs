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
        public Link Link { get; private set; }

        public ChatClient(ClientSocket clientSocket, string userEmail, string id, string name, GPSdata gPSdata)
        {
            ClientSocket = clientSocket;
            UserEmail = userEmail;

            Id = id;
            Name = name;
            GPSdata = gPSdata;
            Link.OnClientMessageRecived += Link_OnClientMessageRecived;
            Link.AddClient(this);
        }

        private void Link_OnClientMessageRecived(ChatClient chatClient, Message msg)
        {
            SendData(msg);
        }

        public void ChangeLink(Link link)
        {
            if (Link != null)
            {
                Link.RemoveClient(this);
                Link.OnClientMessageRecived -= Link_OnClientMessageRecived;
            }

            Link = link;
            Link.OnClientMessageRecived += Link_OnClientMessageRecived;
            Link.AddClient(this);
        }

        public void SendData(Packet packet)
        {
            JObject jObject = JObject.FromObject(packet);
            ClientSocket.Send(jObject.ToString());
        }

        public void UpdateGPS(GPSdata gPSdata)
        {
            GPSdata = gPSdata;
        }
    }

    public struct GPSdata
    {
        public float Longitude;
        public float Latitude;

        public GPSdata(float longitude, float latitude)
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