using System;
using System.Collections.Generic;
using System.Text;
using TinyChatServer.Model;
using TinyChatServer.Server.ClientProcess;
using TinyChatServer.ChatServer.ChatLinker;

namespace TinyChatServer.ChatServer
{
    class ChatClientManager
    {
        public delegate void MessageHandler(string msg);
        public event MessageHandler OnMessageRecived;
        public event MessageHandler OnErrMessageRecived;

        public IReadOnlyList<ChatClient> ReadOnlyChatClients;

        private List<ChatClient> ChatClients;
        private List<Link> Links;

        private LinkingHelper LinkingHelper;

        public ChatClientManager()
        {
            ChatClients = new List<ChatClient>();
            ReadOnlyChatClients = ChatClients;
            Links = new List<Link>();

            LinkingHelper = new LinkingHelper(ChatClients, Links);
        }

        public void AddClient(ClientSocket clientSocket, ClientConnected clientConnectedinfo)
        {
            ChatClient searched = null;

            foreach (var item in ChatClients)
            {
                if (item.ClientSocket == clientSocket)
                {
                    searched = item;
                    break;
                }
            }

            if (searched != null)
            {
                OnErrMessageRecived?.Invoke(
                       string.Format("ClientSocket {0} is already exist while trying AddClient", clientSocket.IPEndPoint.Address.ToString())
                       );
                return;
            }

            ChatClient chatClient = new ChatClient(
                clientSocket,
                clientConnectedinfo.ChatClient.UserEmail,
                clientConnectedinfo.ChatClient.Id,
                clientConnectedinfo.ChatClient.Name,
                new GPSdata(clientConnectedinfo.GPSdata)
                );
            LinkingHelper.LinkClient(chatClient);

            ChatClients.Add(chatClient);
        }

        public void RemoveClient(ClientSocket clientSocket)
        {
            ChatClient searched = null;

            foreach (var item in ChatClients)
            {
                if (item.ClientSocket == clientSocket)
                {
                    searched = item;
                    break;
                }
            }

            if (searched == null)
            {
                OnErrMessageRecived?.Invoke(
                    string.Format("ClientSocket {0} is not exist while trying RemoveClient", clientSocket.IPEndPoint.Address.ToString())
                    );
                return;
            }

            searched.SendData(new ClientDisConnect());
            ChatClients.Remove(searched);
        }

        public void UpdateLinks()
        {
            if (ChatClients.Count <= 1)
            {
                Links.Clear();
            }

            List<Link> LinksCopy = new List<Link>(Links);
            foreach (var item in LinksCopy)
            {

            }
        }
    }
}