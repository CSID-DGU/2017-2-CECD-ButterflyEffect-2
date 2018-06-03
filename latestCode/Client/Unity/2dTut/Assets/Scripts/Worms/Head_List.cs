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
    public GameObject Bot;

    List<GameObject> WormsList = new List<GameObject>();
    List<GameObject> BotList = new List<GameObject>();

    //private float angle = 0f;
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


    private bool[,] isWorm = new bool[4, 4];

    public void Spawn_Head(int i,GameObject WormLight, Color worm_color)
    {

        float w_unit = GetComponent<Create_World>().i_width / 12;
        float h_unit = GetComponent<Create_World>().i_height / 12;


        Debug.Log(w_unit + " , " + h_unit);
        while (true)
        {
            //x position between left and right border
            int x = (int)(Random.Range(0,4));
            //y position between top and bottom border
            int y = (int)(Random.Range(0,4));



            //Debug.Log("intx,inty is =" + (int)(x / w_unit) + "," + (int)(y / h_unit));
            int z = -10;



            //같은 공간에 Spawn 방지
            if (!isWorm[(x), (y)])
            {
                Debug.Log(x + " " + y);
                isWorm[(x), (y)] = true;
                x = (x * 3 - 5) * (int)w_unit;
                y = (y * 3 - 5) * (int)h_unit;
                //Debug.Log("called");
                WormsList.Add(Instantiate(Worms, new Vector3(x, y, z), new Quaternion(0f, 0f, z, 0f)));

                WormsList[i].GetComponent<HeadController>().Head_index_set(i);
                WormsList[i].GetComponent<MeshRenderer>().material.color = worm_color;
                WormsList[i].GetComponent<HeadController>().set_tail_color(worm_color);
                WormLight.GetComponent<Light>().color = worm_color;
                WormLight.transform.position = new Vector3(x- Global.head_size * (-0.04f), y+ Global.head_size * (-0.18f), Global.head_size*(-0.5f));

                break;
            }
        }
    }
    

    public void Spawn_Bot(int i, Color worm_color, int BotId)
    {
        float w_unit = GetComponent<Create_World>().i_width / 12;
        float h_unit = GetComponent<Create_World>().i_height / 12;

        Debug.Log(w_unit + " , " + h_unit);
        while (true)
        {
            //x position between left and right border
            int x = (int)(Random.Range(0, 4));
            //y position between top and bottom border
            int y = (int)(Random.Range(0, 4));

            //Debug.Log("intx,inty is =" + (int)(x / w_unit) + "," + (int)(y / h_unit));
            int z = -10;

            //같은 공간에 Spawn 방지
            if (!isWorm[(x), (y)])
            {
                isWorm[(x), (y)] = true;
                x = (x * 3 - 5) * (int)w_unit;
                y = (y * 3 - 5) * (int)h_unit;
                //Debug.Log("called");
                BotList.Add(Instantiate(Bot, new Vector3(x, y, z), new Quaternion(0f, 0f, z, 0f)));

                BotList[i].GetComponent<Bot_HeadController>().Head_index_set(3);
                BotList[i].GetComponent<MeshRenderer>().material.color = worm_color;
                BotList[i].GetComponent<Bot_HeadController>().set_tail_color(worm_color);

                BotList[i].GetComponent<Bot_HeadController>().Head_index_set(BotId);

                break;
            }
        }
    }


    public void WormBoost(string boost)
    {
        //tokenize string
        string[] str = boost.Split(' ');

        int worm_no = int.Parse(str[0]);

        for (int i = 0; i < worm_no;i++){
            if(WormsList[i]!=null){
                HeadController headController = WormsList[i].GetComponent<HeadController>();
                if (bool.Parse(str[i + 1]))
                {
                    headController.boost_enable();
                }
                else
                {
                    headController.boost_unable();
                }
            }
        }
    }

    // Update is called once per frame
    void FixedUpdate()
    {
        for (int i = 0; i < person_num; i++)
        {
            if(WormsList[i]!=null){
                  HeadController headController = WormsList[i].GetComponent<HeadController>();
                  headController.Z_rotate_update(z_rotate_angle[i]);
            }
        }
    }
}