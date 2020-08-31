using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;

namespace TinyChatServer.Server.ClientProcess
{
    class ClientSocketManager
    {
        public delegate void MessageHandler(string msg);
        public event MessageHandler OnMessageRecived;
        public event MessageHandler OnErrMessageRecived;

        public delegate void SocketHandler(ClientSocket client);
        public event SocketHandler OnClientDisConnect;
        public event SocketHandler OnClientDisConnected;

        public delegate void ClientDataHandler(ClientSocket client, string content);
        public event ClientDataHandler OnClientDataRecived;

        private readonly uint PacketSize;
        private List<ClientSocket> ClientSockets;

        public ClientSocketManager(uint packetsize)
        {
            PacketSize = packetsize;
            ClientSockets = new List<ClientSocket>();
        }

        public void Dispose()
        {
            List<ClientSocket> clonelist = new List<ClientSocket>(ClientSockets);
            foreach (var item in clonelist)
            {
                item.Dispose();
            }
        }

        public ClientSocket ClientProcess(Socket Client)
        {
            ClientSocket clientSocket = new ClientSocket(PacketSize, Client);
            ClientSockets.Add(clientSocket);
            clientSocket.OnMessageRecived += ClientSocket_OnMessageRecived;
            clientSocket.OnErrMessageRecived += ClientSocket_OnErrMessageRecived;
            clientSocket.OnClientDisConnect += ClientSocket_OnClientDisConnect;
            clientSocket.OnClientDisConnected += ClientSocket_OnClientDisConnected;
            clientSocket.OnClientDataRecived += ClientSocket_OnClientDataRecived;
            clientSocket.StartProcess();
            return clientSocket;
        }

        private void ClientSocket_OnErrMessageRecived(string msg) => OnErrMessageRecived?.Invoke(msg);
        private void ClientSocket_OnMessageRecived(string msg) => OnMessageRecived?.Invoke(msg);
        private void ClientSocket_OnClientDisConnect(ClientSocket client) => OnClientDisConnect?.Invoke(client);
        private void ClientSocket_OnClientDisConnected(ClientSocket client)
        {
            client.OnMessageRecived -= ClientSocket_OnMessageRecived;
            client.OnErrMessageRecived -= ClientSocket_OnErrMessageRecived;
            client.OnClientDisConnect -= ClientSocket_OnClientDisConnect;
            client.OnClientDisConnected -= ClientSocket_OnClientDisConnected;
            client.OnClientDataRecived -= ClientSocket_OnClientDataRecived;
            OnClientDisConnected?.Invoke(client);
            ClientSockets.Remove(client);
        }

        private void ClientSocket_OnClientDataRecived(ClientSocket clientSocket, byte[] ReceivedData, ParseResult parseResult)
        {
            string content;

            if (parseResult.ContentLength == 0)
                content = "";
            else
            {
                byte[] contentByte = new byte[parseResult.ContentLength];
                Buffer.BlockCopy(ReceivedData, sizeof(int), contentByte, 0, contentByte.Length);
                content = Encoding.UTF8.GetString(contentByte);
            }

            OnClientDataRecived?.Invoke(clientSocket, content);
        }
    }
}