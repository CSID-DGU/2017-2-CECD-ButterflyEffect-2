using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class Create_World : MonoBehaviour
{

    public float ppu = 1.0f;

    GameObject Background;
    public GameObject WorldLight;
    new public Camera camera;

    public GameObject Text;

    // Borders (Fix using resolution)
    public Transform border_Top;
    public Transform border_Bottom;
    public Transform border_Left;
    public Transform border_Right;

    List<GameObject> WormLightList = new List<GameObject>();

    public GameObject Light;
    public Transform Tilemap;
    public Head_List HList;

    public float i_width;
    public float i_height;

    private float[,] isWorm = new float[6, 6];

    private float w_unit = Global.screen_width / 6;
    private float h_unit = Global.screen_height / 6;

    //Scene 실행 전 수행 (초기화)
    private void Awake()
    {
        /*
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 6; i++)
                Debug.Log(isWorm[i,j]);
                */

        for (int i = 0; i < 3; i++)
            WormLightList.Add(Instantiate(Light, new Vector3(0f, 0f, 0f), new Quaternion(0f, 0f, 0f,0f)));


        //Black Screen 방지
        Screen.sleepTimeout = SleepTimeout.NeverSleep;

        //float i_width = (float)(Screen.width)/ (float)(Screen.width);
        //float i_height = (float)(Screen.height) / (float)(Screen.width);

        Debug.Log("w : " + Global.screen_width +", h : " + Global.screen_height);

        i_width = Global.screen_width / Global.screen_width;
        i_height = Global.screen_height / Global.screen_width;

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

        //Typing "Spawn();" to test worms in Unity here
        Spawn_Bot();

        GameStart("60");

    }

    int person_num = 0;

    //Called by Android(java)
    public void Spawn(string worm_color)
    {
        string[] str = worm_color.Split(' ');
        
        Color worm_color_int = new Color32(byte.Parse(str[0]), byte.Parse(str[1]), byte.Parse(str[2]), 255);

        //person_num = int.Parse(player_count);
        HList.Spawn_Head(person_num, WormLightList[person_num], worm_color_int);
        person_num++;

    }

    public void Spawn_Bot()
    { 
        Color worm_color_int = new Color32(255, 255, 255, 255);
        HList.Spawn_Bot(0, worm_color_int);
    }

    //Called by Android
    public void GameStart(string s)
    {
        Text.GetComponent<TimeBoxScript>().game_time = int.Parse(s) + 3;
        Invoke("GameStart_S", 3.0f);
    }
    public void GameStart_S()
    {

        SpawnFood spfood = camera.GetComponent<SpawnFood>();

        spfood.FoodSpawnStart();

        for (int i = 0; i < 3; i++)
            if(WormLightList[i]!=null)
                WormLightList[i].active = false;

        WorldLight.active = true;

    }

    public void SceneRestart(string s)
    {
        SceneManager.LoadScene(SceneManager.GetActiveScene().name);

    }


}