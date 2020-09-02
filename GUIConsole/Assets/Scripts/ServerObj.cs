using UnityEngine;
using TinyChatServer.ChatServer;

public class ServerObj : MonoBehaviour
{
    public static ChatServer chatServer;

    void Awake()
    {
        chatServer = new ChatServer();
    }

    void Start()
    {
        chatServer.OnMessageRecived += ChatServer_OnMessageRecived;
        chatServer.OnErrMessageRecived += ChatServer_OnErrMessageRecived;
        chatServer.Start();
        StartCoroutine(chatServer.GetSyncRoutine());
    }

    void OnDestroy()
    {
        chatServer.Stop();
    }

    private static void ChatServer_OnErrMessageRecived(string msg) => IGConsole.Instance.printWarnln(msg);

    private static void ChatServer_OnMessageRecived(string msg) => IGConsole.Instance.println(msg);
}
