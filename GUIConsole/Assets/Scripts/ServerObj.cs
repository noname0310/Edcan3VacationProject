using System.Collections;
using UnityEngine;
using TinyChatServer.ChatServer;

public class ServerObj : MonoBehaviour
{
    public ChatServer ChatServer;
    private IEnumerator Coroutine;

    void Awake()
    {
        ChatServer = new ChatServer();
    }

    void Start()
    {
        ChatServer.OnMessageRecived += ChatServer_OnMessageRecived;
        ChatServer.OnErrMessageRecived += ChatServer_OnErrMessageRecived;
        StartServer();
    }

    void OnDestroy()
    {
        StopServer();
    }

    public void StartServer()
    {
        ChatServer.Start();
        Coroutine = ChatServer.GetSyncRoutine();
        StartCoroutine(Coroutine);
    }

    public void StopServer()
    {
        ChatServer.Stop();
        for (int i = 0; i < 30; i++)
            Coroutine.MoveNext();
        StopCoroutine(Coroutine);
    }

    private static void ChatServer_OnErrMessageRecived(string msg) => IGConsole.Instance.printWarnln(msg);

    private static void ChatServer_OnMessageRecived(string msg) => IGConsole.Instance.println(msg);
}