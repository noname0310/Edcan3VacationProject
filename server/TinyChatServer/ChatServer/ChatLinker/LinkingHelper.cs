using System;
using System.Collections.Generic;
using System.Text;

namespace TinyChatServer.ChatServer.ChatLinker
{
    class LinkingHelper
    {
        private List<ChatClient> ChatClients;

        public LinkingHelper(List<ChatClient> chatClients)
        {
            ChatClients = chatClients;
        }
    }
}