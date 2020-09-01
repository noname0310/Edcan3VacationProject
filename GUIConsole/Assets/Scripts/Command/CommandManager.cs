using System.Text;

namespace GUIConsole.Command
{
    public class CommandManager : CommandCore
    {
        [Command("test")]
        public void TestCommand(string command, string[] args)
        {
            if (args.Length <= 0)
            {
                IGConsole.Instance.println("more parms need", false);
                return;
            }

            switch (args[0])
            {
                case "apple":
                    IGConsole.Instance.println("rust", false);
                    break;
                case "경직":
                    IGConsole.Instance.println("foo", false);
                    break;
                case "boom":
                    IGConsole.Instance.println("bar", false);
                    break;
                default:
                    IGConsole.Instance.println("invalid parameter", false);
                    break;
            }
        }

        [Command("test2")]
        public void HelpMsg(string command, string[] args)
        {
            IGConsole.Instance.println("" +
                "start - start server\n" +
                "stop - stop server\n" +
                "pr (send/receive) (view/hide) - view packet log\n" +
                "status - view clients status\n" +
                "kick (clientID/all/\"index\") (index) - kick client"
                , false);
        }
    }
}
