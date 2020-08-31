using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace TinyChatServer.Server
{
    class SocketListener
    {
        public delegate void MessageHandler(string msg);
        public event MessageHandler OnMessageRecived;
        public event MessageHandler OnErrMessageRecived;

        public delegate void ClientConnected(Socket ClientSocket);
        public event ClientConnected OnClientConnected;

        public Socket Listener { get; private set; }

        private ManualResetEvent ManualResetEvent;
        private bool Listening;

        public SocketListener()
        {
            ManualResetEvent = new ManualResetEvent(false);
            Listening = false;
        }

        public void Start(IPEndPoint localEndPoint)
        {
            if (Listening)
            {
                OnErrMessageRecived?.Invoke("Listener is already running!");
                return;
            }

            Listener = new Socket(localEndPoint.Address.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            try
            {
                Listener.Bind(localEndPoint);
                Listener.Listen(100);

                Listening = true;
                while (Listening)
                {
                    ManualResetEvent.Reset();
                    OnMessageRecived?.Invoke("Waiting for a connection...");
                    Listener.BeginAccept(new AsyncCallback(AcceptCallback), Listener);
                    ManualResetEvent.WaitOne();
                }

            }
            catch (SocketException e)
            {
                OnErrMessageRecived?.Invoke(e.ToString());
                OnMessageRecived?.Invoke("Try listen again...");
                Stop();
                Start(localEndPoint);
            }
        }

        public void Stop()
        {
            if (!Listening)
            {
                OnErrMessageRecived?.Invoke("Listener is not running!");
                return;
            }

            Listener.Close();
            Listener.Dispose();
            Listening = false;
        }

        private void AcceptCallback(IAsyncResult ar)
        {
            ManualResetEvent.Set();
            Socket clientSocket = Listener.EndAccept(ar);
            OnClientConnected?.Invoke(clientSocket);
        }
    }
}