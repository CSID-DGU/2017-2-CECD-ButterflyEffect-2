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

    // Use this for initialization
    void Start () {
        //set game time
        Timer = GetComponent<Text>();
        Timer.fontSize = (int)Global.food_size*2;
        Timer.rectTransform.position = Border_Top.transform.position;

        jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        jo = jc.GetStatic<AndroidJavaObject>("currentActivity");

    }
	
	// Update is called once per frame
	void FixedUpdate () {

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
