using System.Collections;
using System.Collections.Generic;
using UnityEngine;



public class Create_World : MonoBehaviour {

    public float ppu=1.0f;

    GameObject Background;
    public Camera camera;

    // Borders (Fix using resolution)
    public Transform border_Top;
    public Transform border_Bottom;
    public Transform border_Left;
    public Transform border_Right;

    //Scene 실행 전 수행 (초기화)
    private void Awake()
    {
        //Black Screen 방지
        Screen.sleepTimeout = SleepTimeout.NeverSleep;

        Resolution[] resolutions = Screen.resolutions;
        foreach (Resolution res in resolutions)
        {
            print(res.width + "x" + res.height);
        }
        Screen.SetResolution(resolutions[0].width, resolutions[0].height, true);


        //Resolution 정보 Load
        float i_width = (float)Screen.width/10;
        float i_height = (float)Screen.height/10;
        Screen.SetResolution((int)i_width, (int)i_height, true);
        Debug.Log("Init clear");
        //border Scale edit
        border_Left.transform.localScale = new Vector3(1, i_height, 10);
        border_Right.transform.localScale = new Vector3(1, i_height, 10);
        border_Top.transform.localScale = new Vector3(i_width, 1, 10);
        border_Bottom.transform.localScale = new Vector3(i_width, 1, 10);
        
        //border Position edit
        border_Left.position = new Vector3(-i_width/2, 0, 0);
        border_Right.position = new Vector3(i_width/2, 0, 0);
        border_Top.position = new Vector3(0, i_height/2, 0);
        border_Bottom.position = new Vector3(0, -i_height/2, 0);

        //화면 Size 조정. 높이 * pixel per unit /2;        
        camera.orthographicSize = i_height/ppu/ 2;
    }
}
