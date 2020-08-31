using System;
using System.Collections.Generic;
using System.Net;
using System.Text;
using TinyChatServer.Model;
using TinyChatServer.Server.ClientProcess;

namespace TinyChatServer.ChatServer.ChatLinker
{
    class LinkingHelper
    {
        private Dictionary<IPAddress, ChatClient> ChatClients;

        public LinkingHelper(Dictionary<IPAddress, ChatClient> chatClients)
        {
            ChatClients = chatClients;
        }

        public void LinkClient(ChatClient chatClient)
        {
            //chatClient.ChangeLink();
        }

        public void InitLinks()
        {
        }

        public void UpdateLinks()
        {
        }

        public double GPSDistanceMeter(GPSdata some, GPSdata other)
        {
            double theta;
            double distance;
            theta = some.Longitude - other.Longitude;
            distance =
                (
                Math.Sin(ConvertDegreesToRadians(some.Latitude)) * 
                Math.Sin(ConvertDegreesToRadians(other.Latitude))
                ) 
                + 
                (
                Math.Cos(ConvertDegreesToRadians(some.Latitude)) * 
                Math.Cos(ConvertDegreesToRadians(other.Latitude)) *
                Math.Cos(ConvertDegreesToRadians(theta))
                );
            distance = Math.Acos(distance);
            distance = ConvertRadiansToDegrees(distance);
            distance = distance * 60 * 1.1515;
            distance = distance * 1.609344 * 1000;
            return distance;
        }

        public static double ConvertDegreesToRadians(double degrees)
        {
            return (Math.PI / 180) * degrees;
        }

        public static double ConvertRadiansToDegrees(double radians)
        {
            return (180 / Math.PI) * radians;
        }
    }
}