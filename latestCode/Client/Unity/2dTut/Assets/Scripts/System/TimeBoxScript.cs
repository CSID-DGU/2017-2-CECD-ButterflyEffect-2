using System.Collections;
using System.Collections.Generic;
using UnityEngine.UI;
using UnityEngine;


public class TimeBoxScript : MonoBehaviour {

    Text Timer;
    private float game_time;
    public GameObject Border_Top;
    AndroidJavaClass jc; AndroidJavaObject jo;

    bool game_end = false;
    bool game_start = false;

    // Use this for initialization
    void Start () {

        //set game time
        Timer = GetComponent<Text>();
        Timer.text = "";
        Timer.fontSize = (int)(Global.FontSize*0.9);
        //Timer.rectTransform.position = Border_Top.transform.position;


        jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        jo = jc.GetStatic<AndroidJavaObject>("currentActivity");
        
    }

    public void timeCountStart(float time)
    {
        game_start = true;
        game_time = time;
    }
	
	// Update is called once per frame
	void FixedUpdate () {


        if(game_start == false)
        {
            return;
        }

        if (game_time < 15.9f)
        {
            Timer.color = Color.yellow;
        }

        if (game_time < 9.9f)
        {
            Timer.color = Color.red;
        }
        


        game_time = game_time - Time.deltaTime;
        Timer.text = "Time left : " + string.Format("{0:f0}",game_time);

        if(game_end==true)
        {
            Destroy(gameObject);
        }
        else{
            if (game_time < 0)
            {
                game_end = true;
                jo.Call("timeOut", " ");




                
            }
        }
	}

    //Called by Android
    //void 

}
