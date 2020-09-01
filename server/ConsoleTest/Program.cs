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
            chatServer.OnMessageRecived += ChatServer_OnMessageRecived;
            chatServer.OnErrMessageRecived += ChatServer_OnErrMessageRecived;
            chatServer.Start();
            chatServer.RunSyncRoutine();
        }

        private static void ChatServer_OnErrMessageRecived(string msg) => Console.WriteLine(msg);

        private static void ChatServer_OnMessageRecived(string msg) => Console.WriteLine(msg);
    }
}