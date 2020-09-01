using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class DrawLine : MonoBehaviour
{
    private LineRenderer LineRenderer;
    public GameObject Target;

    private void Start()
    {
        LineRenderer = GetComponent<LineRenderer>();
        LineRenderer.startWidth = .05f;
        LineRenderer.endWidth = .05f;
    }

    void Update()
    {
        LineRenderer.SetPosition(0, transform.position);
        LineRenderer.SetPosition(1, Target.transform.position);
    }
}
