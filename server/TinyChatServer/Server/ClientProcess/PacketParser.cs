using System;

namespace TinyChatServer.Server.ClientProcess
{
    class PacketParser
    {
        public static ParseResult HeaderParse(byte[] buffer, uint recivedbyte)
        {
            if (recivedbyte < 4)
                return new ParseResult(false, 0, 0);

            return new ParseResult(true, BitConverter.ToInt32(buffer, 0), (int)recivedbyte - sizeof(int));
        }
    }

    public struct ParseResult
    {
        public bool HeaderParsed;
        public int ContentLength;
        public int ReceivedContentLength;

        public ParseResult(bool headerParsed, int contentLength, int receivedContentLength)
        {
            HeaderParsed = headerParsed;
            ContentLength = contentLength;
            ReceivedContentLength = receivedContentLength;
        }
    }
}