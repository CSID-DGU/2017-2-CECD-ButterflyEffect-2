using System.Collections;
using System.Collections.Generic;
using UnityEngine.UI;
using UnityEngine;


public class TimeBoxScript : MonoBehaviour {

    Text Timer;
    public float game_time = 60f;
    public GameObject Border_Top;

	// Use this for initialization
	void Start () {
        //set game time
        Timer = GetComponent<Text>();
        game_time = 60f;

        Timer.rectTransform.position = Border_Top.transform.position;
		
	}
	
	// Update is called once per frame
	void FixedUpdate () {
        game_time = game_time - Time.deltaTime;

        Timer.text = "Time left : " + string.Format("{0:f2}",game_time)   ;
		
        
	}

    //Called by Android
    //void 

}
