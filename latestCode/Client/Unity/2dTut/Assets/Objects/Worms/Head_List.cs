using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Head_List : MonoBehaviour
{

    public Transform border_Top;
    public Transform border_Bottom;
    public Transform border_Left;
    public Transform border_Right;

    public GameObject Worms;
    List<GameObject> WormsList = new List<GameObject>();
    private float angle = 90f;
    private float[] z_rotate_angle = { 0f, 0f, 0f };
    int person_num;

    // 유니티가 동작하는 액티비티를 저장하는 변수
    public AndroidJavaObject activity;


    //Called by Android(java)
    public void WormMoveAngle(string degree)
    {

        string[] str = degree.Split(' ');

        person_num = int.Parse(str[0]);


        for (int i = 1; i <= person_num; i++)
        {
            angle = float.Parse(str[i]);

            z_rotate_angle[i - 1] = (angle - 90)*2;


            //z_rotate_angle[i - 1] = (angle - 90f);

        }

    }


    // Use this for initialization
    void Start()
    {
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


        WormsList.Add(Instantiate(Worms, new Vector3(x, y, z), new Quaternion(0f, 0f, z, 0f)));

    }

    // Update is called once per frame
    void Update()
    {
        for (int i = 0; i < person_num; i++)
        {
            HeadController headController = WormsList[i].GetComponent<HeadController>();
            headController.Z_rotate_update(z_rotate_angle[i]);
        }

    }
}