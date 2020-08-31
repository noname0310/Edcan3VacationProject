using System;
using System.Collections.Generic;
using System.Text;
using TinyChatServer.Model;

namespace TinyChatServer.ChatServer.ChatLinker
{
    public class Link
    {
        public delegate void ClientMessageHandler(ChatClient client, Message msg);
        public event ClientMessageHandler OnClientMessageRecived;

        public IReadOnlyList<ChatClient> ReadonlyLinkedClients;
        private List<ChatClient> LinkedClients;

        public Link()
        {
            LinkedClients = new List<ChatClient>();
            ReadonlyLinkedClients = LinkedClients;
        }

        public void AddClient(ChatClient client)
        {
            LinkedClients.Add(client);
        }

        public void RemoveClient(ChatClient client)
        {
            LinkedClients.Remove(client);
        }

        public void OnClientMessage(ChatClient client, Message msg)
        {
            OnClientMessageRecived?.Invoke(client, msg);
        }
    }
}