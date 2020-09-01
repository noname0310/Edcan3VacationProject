using System;

namespace GUIConsole.Command
{
    public class CommandCore
    {
        private CommandObj commandObj;

        public CommandCore()
        {
            commandObj = CommandObj.Instance;

            ReflectRegisterCommandsNEvents();
        }

        public void ReflectRegisterCommandsNEvents()
        {
            foreach (var item in GetType().GetMethods())
            {
                Attribute[] attributes = Attribute.GetCustomAttributes(item);
                if (attributes.Length == 0)
                {
                    switch (item.Name)
                    {
                        //case "OnClientDataInvoked":
                        //    SocketObj.DataInvoke DataInvokeDelegate = (SocketObj.DataInvoke)item.CreateDelegate(typeof(SocketObj.DataInvoke), this);
                        //    socketObj.OnDataInvoke += DataInvokeDelegate;
                        //    break;

                        default:
                            break;
                    }
                }
                else
                {
                    foreach (var att in attributes)
                    {
                        if (att.GetType() == typeof(CommandAttribute))
                        {
                            CommandAttribute commandAttribute = (CommandAttribute)att;
                            CommandObj.CommandDelegate commandDelegate = (CommandObj.CommandDelegate)item.CreateDelegate(typeof(CommandObj.CommandDelegate), this);

                            commandObj.CommandDelegates.Add(commandAttribute.Command.ToUpper(), commandDelegate);
                        }
                    }
                }
            }
        }
    }
}
