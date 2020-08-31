using System.Threading;
using System.Net;
using System.Net.Sockets;
using TinyChatServer.Server.ClientProcess;

namespace TinyChatServer.Server
{
    class SocketServer
    {
        public delegate void MessageHandler(string msg);
        public event MessageHandler OnMessageRecived;
        public event MessageHandler OnErrMessageRecived;

        public delegate void ClientDataHandler(ClientSocket client, string msg);
        public event ClientDataHandler OnClientDataRecived;

        public delegate void SocketHandler(ClientSocket client);
        public event SocketHandler OnClientConnected;
        public event SocketHandler OnClientDisConnect;
        public event SocketHandler OnClientDisConnected;

        private SocketListener SocketListener;
        private ClientSocketManager ClientSocketManager;
        private bool Running;

        public SocketServer(uint packetsize)
        {
            ClientSocketManager = new ClientSocketManager(packetsize);
            ClientSocketManager.OnMessageRecived += ClientSocketManager_OnMessageRecived;
            ClientSocketManager.OnErrMessageRecived += ClientSocketManager_OnErrMessageRecived;
            ClientSocketManager.OnClientDataRecived += ClientSocketManager_OnClientDataRecived;
            ClientSocketManager.OnClientDisConnect += ClientSocketManager_OnClientDisConnect;
            ClientSocketManager.OnClientDisConnected += ClientSocketManager_OnClientDisConnected;

            SocketListener = new SocketListener();
            SocketListener.OnMessageRecived += SocketListener_OnMessageRecived;
            SocketListener.OnErrMessageRecived += SocketListener_OnErrMessageRecived;
            SocketListener.OnClientConnected += SocketListener_OnClientConnected;
            Running = false;
        }

         ~SocketServer()
        {
            ClientSocketManager.OnMessageRecived -= ClientSocketManager_OnMessageRecived;
            ClientSocketManager.OnErrMessageRecived -= ClientSocketManager_OnErrMessageRecived;
            ClientSocketManager.OnClientDataRecived -= ClientSocketManager_OnClientDataRecived;
            ClientSocketManager.OnClientDisConnect -= ClientSocketManager_OnClientDisConnect;
            ClientSocketManager.OnClientDisConnected -= ClientSocketManager_OnClientDisConnected;
            SocketListener.OnMessageRecived -= SocketListener_OnMessageRecived;
            SocketListener.OnErrMessageRecived -= SocketListener_OnErrMessageRecived;
            SocketListener.OnClientConnected -= SocketListener_OnClientConnected;
        }

        private void ClientSocketManager_OnMessageRecived(string msg) => OnMessageRecived?.Invoke(msg);
        private void ClientSocketManager_OnErrMessageRecived(string msg) => OnErrMessageRecived?.Invoke(msg);
        private void ClientSocketManager_OnClientDataRecived(ClientSocket client, string content) => OnClientDataRecived?.Invoke(client, content);
        private void ClientSocketManager_OnClientDisConnect(ClientSocket client) => OnClientDisConnect.Invoke(client);
        private void ClientSocketManager_OnClientDisConnected(ClientSocket client) => OnClientDisConnected.Invoke(client);

        private void SocketListener_OnMessageRecived(string msg) => OnMessageRecived?.Invoke(msg);
        private void SocketListener_OnErrMessageRecived(string msg) => OnErrMessageRecived?.Invoke(msg);
        private void SocketListener_OnClientConnected(Socket ClientSocket)
        {
            ClientSocket clientSocket = ClientSocketManager.ClientProcess(ClientSocket);
            OnClientConnected?.Invoke(clientSocket);
        }

        public void Start(IPEndPoint iPEndPoint)
        {
            if (Running == true)
            {
                OnErrMessageRecived?.Invoke("Server is already running");
                return;
            }
            Running = true;
            SocketListener.Start(iPEndPoint);
        }
        public void Start(uint port)
        {
            IPHostEntry ipHostInfo = Dns.GetHostEntry(Dns.GetHostName());
            IPAddress ipAddress = ipHostInfo.AddressList[0];
            Start(new IPEndPoint(ipAddress, (int)port));
        }

        public void Stop()
        {
            if (Running == false)
            {
                OnErrMessageRecived?.Invoke("Server is not running");
                return;
            }

            SocketListener.Stop();
            ClientSocketManager.Dispose();
            Thread.Sleep(1000);

            Running = false;
        }
    }
}