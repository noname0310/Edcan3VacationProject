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
                "status - view clients status\n"
                , false);
        }
    }
}
