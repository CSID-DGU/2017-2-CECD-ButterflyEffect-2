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
    private float angle = 0f;
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
            z_rotate_angle[i - 1] = -(float.Parse(str[i]) - 90) * 2;
        }

    }

    public void Spawn_Head(int i)
    {

        //x position between left and right border
        int x = (int)Random.Range(border_Left.position.x,
                                  border_Right.position.x);
        //y position between top and bottom border
        int y = (int)Random.Range(border_Top.position.y,
                                  border_Bottom.position.y);

        int z = -3;


        WormsList.Add(Instantiate(Worms, new Vector3(x, y, z), new Quaternion(0f, 0f, z, 0f)));

        WormsList[i].GetComponent<HeadController>().Head_index_set(i);
        WormsList[i].GetComponent<MeshRenderer>().material.color = Global.player_Color[i];

    }

    public void WormBoost(string boost)
    {
        //tokenize string
        string[] str = boost.Split(' ');

        int worm_no = int.Parse(str[0]);

        bool boost_enable = bool.Parse(str[1]);

        if (boost_enable == true)
        {
            HeadController headController = WormsList[worm_no].GetComponent<HeadController>();
            headController.boost_enable();
        }

        


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