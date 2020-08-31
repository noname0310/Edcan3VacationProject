using System;
using System.Collections.Generic;
using System.Text;
using TinyChatServer.Model;
using TinyChatServer.Server.ClientProcess;

namespace TinyChatServer.ChatServer.ChatLinker
{
    class LinkingHelper
    {
        private List<ChatClient> ChatClients;
        private List<Link> Links;

        public LinkingHelper(List<ChatClient> chatClients, List<Link> links)
        {
            ChatClients = chatClients;
            Links = links;
        }

        public void LinkClient(ChatClient chatClient)
        {
            //chatClient.ChangeLink();
        }

        public void UpdateLinks()
        {
            if (ChatClients.Count == 0)
            {
                Links.Clear();
                return;
            }


            foreach (var item in ChatClients)
            {
            }
            LinkedList<ChatClient> chatClientsCopy = new LinkedList<ChatClient>(ChatClients);
        }
    }
}