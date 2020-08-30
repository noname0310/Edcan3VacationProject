using System;
using System.Net;
using TinyChatServer.Server;

namespace ConsoleTest
{
    class Program
    {
        static void Main(string[] args)
        {
            SocketListener asynchronousSocketListener = new SocketListener();
            asynchronousSocketListener.OnMessageRecived += AsynchronousSocketListener_OnMessageRecived;
            asynchronousSocketListener.OnErrMessageRecived += AsynchronousSocketListener_OnErrMessageRecived;
            IPHostEntry ipHostInfo = Dns.GetHostEntry(Dns.GetHostName());
            IPAddress ipAddress = ipHostInfo.AddressList[0];
            asynchronousSocketListener.StartListening(new IPEndPoint(ipAddress, 20310));
            Console.WriteLine("\nPress ENTER to continue...");
            Console.Read();
        }

        private static void AsynchronousSocketListener_OnErrMessageRecived(string msg)
        {
            Console.WriteLine(string.Format("ERR: {0}", msg));
        }

        private static void AsynchronousSocketListener_OnMessageRecived(string msg)
        {
            Console.WriteLine(msg);
        }
    }
}
