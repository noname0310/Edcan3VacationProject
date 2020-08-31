using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using TinyChatServer.Model;
using TinyChatServer.Server.ClientProcess;
using TinyChatServer.ChatServer.ChatLinker;

namespace TinyChatServer.ChatServer
{
    class ChatClientManager
    {
        public delegate void MessageHandler(string msg);
        //public event MessageHandler OnMessageRecived;
        public event MessageHandler OnErrMessageRecived;

        public IReadOnlyDictionary<IPAddress, ChatClient> ReadOnlyChatClients;

        private Dictionary<IPAddress, ChatClient> ChatClients;

        private LinkingHelper LinkingHelper;

        public ChatClientManager()
        {
            ChatClients = new Dictionary<IPAddress, ChatClient>();
            ReadOnlyChatClients = ChatClients;

            LinkingHelper = new LinkingHelper(ChatClients);
        }

        public void Dispose()
        {
            ChatClients.Clear();
        }

        public ChatClient AddClient(ClientSocket clientSocket, ClientConnected clientConnectedinfo)
        {
            ChatClient searched = null;

            foreach (var item in ChatClients)
            {
                if (item.Value.ClientSocket == clientSocket)
                {
                    searched = item.Value;
                    break;
                }
            }

            if (searched != null)
            {
                OnErrMessageRecived?.Invoke(
                       string.Format("ClientSocket {0} is already exist while trying AddClient", clientSocket.IPAddress.ToString())
                       );
                return searched;
            }

            ChatClient chatClient = new ChatClient(
                clientSocket,
                clientConnectedinfo.ChatClient.UserEmail,
                clientConnectedinfo.ChatClient.Id,
                clientConnectedinfo.ChatClient.Name,
                new GPSdata(clientConnectedinfo.GPSdata)
                );
            LinkingHelper.LinkClient(chatClient);

            ChatClients.Add(clientSocket.IPAddress, chatClient);
            return chatClient;
        }

        public void RemoveClient(ClientSocket clientSocket)
        {
            ChatClient searched = null;

            foreach (var item in ChatClients)
            {
                if (item.Value.ClientSocket == clientSocket)
                {
                    searched = item.Value;
                    break;
                }
            }

            if (searched == null)
            {
                OnErrMessageRecived?.Invoke(
                    string.Format("ClientSocket {0} is not exist while trying RemoveClient", clientSocket.IPAddress.ToString())
                    );
                return;
            }

            searched.SendData(new ClientDisConnect());
            ChatClients.Remove(searched.ClientSocket.IPAddress);
        }
    }
}