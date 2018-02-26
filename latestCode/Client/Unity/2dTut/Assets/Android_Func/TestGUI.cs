using System.Collections;
using System.Collections.Generic;
using UnityEngine;



public class TestGUI : MonoBehaviour {

	// Use this for initialization
	void Start () {
        //안드로이드 자바 클래스 호출
        //Activity 이름, 매개변수(전송)
        //AndroidManager.Instance.activity.Call("initActivity", "Unity", "messageFromUnity");
	}

    // Update is called once per frame
    void OnGUI()
    {
   //     GUI.Label(new Rect(10f, 10f, 200f, 30f), AndroidManager.Instance.androidLog);        
    }
}
