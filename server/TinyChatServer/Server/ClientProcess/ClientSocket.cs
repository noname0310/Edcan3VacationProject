using System;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.Collections.Generic;

namespace TinyChatServer.Server.ClientProcess
{
    public class ClientSocket : IEquatable<ClientSocket>
    {
        public delegate void MessageHandler(string msg);
        public event MessageHandler OnMessageRecived;
        public event MessageHandler OnErrMessageRecived;

        public delegate void SocketHandler(ClientSocket client);
        public event SocketHandler OnClientDisConnect;
        public event SocketHandler OnClientDisConnected;

        public delegate void ClientDataRecived(ClientSocket clientSocket, byte[] receivedData, ParseResult parseResult);
        public event ClientDataRecived OnClientDataRecived;

        public readonly IPEndPoint IPEndPoint;

        private readonly uint PacketSize;
        private byte[] Buffer;

        private readonly Socket Client;

        private bool HeaderParsed;
        private byte[] ContentFullBuffer;
        private int LeftContentByte;
        private int ContentOffSet;
        private ParseResult parseResult;

        public ClientSocket(uint packetSize, Socket client)
        {
            PacketSize = packetSize;
            Client = client;
            IPEndPoint = Client.RemoteEndPoint as IPEndPoint;
            HeaderParsed = false;
        }

        public void StartProcess()
        {
            Buffer = new byte[PacketSize];
            Process(Buffer.Length);
        }

        private void Process(int ReceiveLength)
        {
            for (int i = 0; i < Buffer.Length; i++)
                Buffer[i] = 0;

            Client.BeginReceive(Buffer, 0, (Buffer.Length < ReceiveLength) ? Buffer.Length : ReceiveLength, SocketFlags.None, Receivecallback, Client);
        }

        private void Receivecallback(IAsyncResult ar)
        {
            try
            {
                int receivedByte = Client.EndReceive(ar);

                if (!HeaderParsed)
                {
                    parseResult = PacketParser.HeaderParse(Buffer, (uint)receivedByte);

                    if (parseResult.HeaderParsed == false)
                    {
                        Process(Buffer.Length);
                    }
                    else if (parseResult.ContentLength == parseResult.ReceivedContentLength)
                    {
                        byte[] bufferClone = new byte[Buffer.Length];
                        System.Buffer.BlockCopy(Buffer, 0, bufferClone, 0, sizeof(int) + parseResult.ContentLength);

                        HeaderParsed = false;
                        Process(Buffer.Length);
                        OnClientDataRecived?.Invoke(this, bufferClone, parseResult);
                    }
                    else
                    {
                        LeftContentByte = parseResult.ContentLength - parseResult.ReceivedContentLength;
                        ContentOffSet = sizeof(int) + parseResult.ReceivedContentLength;

                        ContentFullBuffer = new byte[sizeof(int) + parseResult.ContentLength];
                        System.Buffer.BlockCopy(Buffer, 0, ContentFullBuffer, 0, receivedByte);

                        HeaderParsed = true;
                        Process(LeftContentByte);
                    }
                }
                else
                {
                    System.Buffer.BlockCopy(Buffer, 0, ContentFullBuffer, ContentOffSet, receivedByte);

                    LeftContentByte -= receivedByte;
                    ContentOffSet += receivedByte;

                    if (LeftContentByte <= 0)
                    {
                        byte[] bufferClone = new byte[ContentFullBuffer.Length];
                        System.Buffer.BlockCopy(ContentFullBuffer, 0, bufferClone, 0, sizeof(int) + parseResult.ContentLength);

                        HeaderParsed = false;
                        Process(Buffer.Length);

                        OnClientDataRecived?.Invoke(this, bufferClone, parseResult);
                    }
                    else
                        Process(LeftContentByte);
                }
            }
            catch(SocketException e)
            {
                OnErrMessageRecived?.Invoke(e.Message);
                OnMessageRecived?.Invoke("Error Handled, Client force disconnected");
                Dispose();
            }
        }

        public void Send(string content)
        {
            byte[] ContentBuffer = Encoding.UTF8.GetBytes(content);
            byte[] Header = BitConverter.GetBytes(ContentBuffer.Length);
            byte[] SendBuffer = new byte[Header.Length + ContentBuffer.Length];
            System.Buffer.BlockCopy(Header, 0, SendBuffer, 0, Header.Length);
            System.Buffer.BlockCopy(ContentBuffer, 0, SendBuffer, Header.Length, ContentBuffer.Length);

            //if (BitConverter.IsLittleEndian)
            //    Array.Reverse(SendBuffer);

            Client.BeginSend(SendBuffer, 0, SendBuffer.Length, 0, new AsyncCallback(SendCallback), Client);
        }

        private void SendCallback(IAsyncResult ar)
        {
            try
            {
                int bytesSent = Client.EndSend(ar);
                OnMessageRecived?.Invoke(string.Format("Sent {0} bytes to client.", bytesSent));
            }
            catch (Exception e)
            {
                OnErrMessageRecived?.Invoke(e.ToString());
            }
        }

        public void Dispose()
        {
            OnClientDisConnect.Invoke(this);
            Client.Shutdown(SocketShutdown.Both);
            Client.Close();
            Client.Dispose();
            OnClientDisConnected.Invoke(this);
        }

        public bool Equals(ClientSocket other)
        {
            return EqualityComparer<IPEndPoint>.Default.Equals(IPEndPoint, other.IPEndPoint) &&
                   EqualityComparer<Socket>.Default.Equals(Client, other.Client);
        }

        public override bool Equals(object obj)
        {
            return obj is ClientSocket socket &&
                   Equals(socket);
        }

        public override int GetHashCode()
        {
            int hashCode = 936257235;
            hashCode = hashCode * -1521134295 + EqualityComparer<IPEndPoint>.Default.GetHashCode(IPEndPoint);
            hashCode = hashCode * -1521134295 + EqualityComparer<Socket>.Default.GetHashCode(Client);
            return hashCode;
        }

        public static bool operator ==(ClientSocket left, ClientSocket right)
        {
            return EqualityComparer<ClientSocket>.Default.Equals(left, right);
        }

        public static bool operator !=(ClientSocket left, ClientSocket right)
        {
            return !(left == right);
        }
    }
}