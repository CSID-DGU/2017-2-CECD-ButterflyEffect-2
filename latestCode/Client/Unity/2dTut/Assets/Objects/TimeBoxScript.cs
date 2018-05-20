using System.Collections;
using System.Collections.Generic;
using UnityEngine.UI;
using UnityEngine;


public class TimeBoxScript : MonoBehaviour {

    Text Timer;
    public float game_time = 5f;
    public GameObject Border_Top;
    AndroidJavaClass jc; AndroidJavaObject jo;

    bool game_end = false;
    bool game_start = false;

    // Use this for initialization
    void Start () {

        //set game time
        Timer = GetComponent<Text>();
        Timer.text = "";
        Timer.fontSize = (int)(Global.food_size*1.5f);
        //Timer.rectTransform.position = Border_Top.transform.position;


        Invoke("timeCountStart", 3.0f);

        jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        jo = jc.GetStatic<AndroidJavaObject>("currentActivity");
        
    }

    public void timeCountStart()
    {
        game_start = true;
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
