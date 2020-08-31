using System;
using System.Net;
using System.Threading;
using TinyChatServer.ChatServer;

namespace ConsoleTest
{
    class Program
    {
        static void Main(string[] args)
        {
            ChatServer chatServer = new ChatServer();
            chatServer.Start();
        }
    }
}