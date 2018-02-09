using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Head_generate : MonoBehaviour {

    public Transform border_Top;
    public Transform border_Bottom;
    public Transform border_Left;
    public Transform border_Right;

    public GameObject Worms;
    List<GameObject> WormsList = new List<GameObject>();

	// Use this for initialization
	void Start () {

        //generate at start
        Spawn();

    }

    void Spawn()
    {

        //x position between left and right border
        int x = (int)Random.Range(border_Left.position.x,
                                  border_Right.position.x);
        //y position between top and bottom border
        int y = (int)Random.Range(border_Top.position.y,
                                  border_Bottom.position.y);

        int z = -3;


        WormsList.Add(Instantiate(Worms, new Vector3(x, y, z), new Quaternion(0f,0f,z,0f)));

    }

    // Update is called once per frame
    void Update () {
		
	}
}
