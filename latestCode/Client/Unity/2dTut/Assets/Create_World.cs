using System.Collections;
using System.Collections.Generic;
using UnityEngine;



public class Create_World : MonoBehaviour
{

    public float ppu = 1.0f;

    GameObject Background;
    new public Camera camera;

    // Borders (Fix using resolution)
    public Transform border_Top;
    public Transform border_Bottom;
    public Transform border_Left;
    public Transform border_Right;

    public Transform Tilemap;

    public Head_List HList;

    //Scene 실행 전 수행 (초기화)
    private void Awake()
    {
        //Black Screen 방지
        Screen.sleepTimeout = SleepTimeout.NeverSleep;

        //float i_width = (float)(Screen.width)/ (float)(Screen.width);
        //float i_height = (float)(Screen.height) / (float)(Screen.width);
        float i_width = Global.screen_width / Global.screen_width;
        float i_height = Global.screen_height / Global.screen_width;

        //fix game resolution 640x640
        i_width *= Global.game_res_width;
        i_height *= Global.game_res_height;

        Screen.SetResolution((int)i_width, (int)i_height, true);
        Debug.Log(i_width + "," + i_height);
        //border Scale edit
        border_Left.transform.localScale = new Vector3(1, i_height, 10);
        border_Right.transform.localScale = new Vector3(1, i_height, 10);
        border_Top.transform.localScale = new Vector3(i_width, 1, 10);
        border_Bottom.transform.localScale = new Vector3(i_width, 1, 10);

        //border Position edit
        border_Left.position = new Vector3(-i_width / 2, 0, 0);
        border_Right.position = new Vector3(i_width / 2, 0, 0);
        border_Top.position = new Vector3(0, i_height / 2, 0);
        border_Bottom.position = new Vector3(0, -i_height / 2, 0);

        //Tile Map Scale edit
        Tilemap.transform.localScale = new Vector3(i_width / 5, i_height / 5, 1);

        //화면 Size 조정. 높이 * pixel per unit /2;        
        camera.orthographicSize = i_height / ppu / 2;

        //초록 빨강 파랑


        Spawn();
        Spawn();
        Spawn();

    }

    int person_num = 0;

    //Called by Android(java)
    public void Spawn()
    {
        //person_num = int.Parse(player_count);
        HList.Spawn_Head(person_num++);
    }

}
