using GUIConsole.Command;
using TinyChatServer.ChatServer;
using UnityEngine;

namespace Assets.Scripts
{
    public class SocketCommands : CommandCore
    {
        ChatServer ChatServer;

        public SocketCommands(ChatServer chatServer)
        {
            ChatServer = chatServer;
        }

        [Command("start")]
        public void ServerStartCommand(string command, string[] args)
        {
            ChatServer.Start();
        }

        [Command("stop")]
        public void ServerStopCommand(string command, string[] args)
        {
            ChatServer.Stop();
        }

        [Command("quit")]
        public void ServerQuitCommand(string command, string[] args)
        {
            Application.Quit();
        }

        [Command("range")]
        public void ServerSetRangeCommand(string command, string[] args)
        {
            if (args.Length == 0)
                IGConsole.Instance.println(string.Format("Current Chat Link Range: {0}m", ChatServer.IChatClientManager.SearchRange));
            else
            {
                int parsedint;
                if (int.TryParse(args[0], out parsedint)) 
                {
                    ChatServer.IChatClientManager.SearchRange = parsedint;
                    IGConsole.Instance.println(string.Format("Chat link range setted to {0}m", parsedint));
                }
                else
                {
                    IGConsole.Instance.println("invaid parms!");
                }
            }
        }

        [Command("status")]
        public void ServerStatusCommand(string command, string[] args)
        {
            
        }

        [Command("help")]
        public void HelpMsg(string command, string[] args)
        {
            IGConsole.Instance.println("" +
                "start - start server\n" +
                "stop - stop server\n" +
                "quit - quit server\n" +
                "range - set chat range\n" +
                "status - view clients status\n"
                , false);
        }
    }
}
