using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class rotateFood: MonoBehaviour {

    private Rigidbody food;
    private Light foodlight;
    // Use this for initialization
    void Start () {
        food = gameObject.GetComponent<Rigidbody>();
        foodlight = gameObject.GetComponent<Light>();
    }

    int flag = 1;
    float modifier = 1;

	// Update is called once per frame
	void FixedUpdate () {

        if (flag == 1)
        {
            foodlight.range = Global.food_halo_size * modifier;
            modifier *= 1.01f;
            if (modifier > 1.1)
                flag = 0;
        }
        else
        {
            foodlight.range = Global.food_halo_size * modifier;
            modifier *= 0.99f;
            if (modifier < 0.9)
                flag = 1;
        }

        
        food.transform.Rotate(0.0f, 0.0f, 1.0f);

    }

    /*
    void OnTriggerEnter(Collider coll)
    {
        // Trigger Food?
        if (coll.name.StartsWith("Head"))
        {
            // Get longer in next Move call
            ate -= 2;

            coll.enabled = false;

            score++;

            // Message to Android

            // Android에 점수 전송
            if (jo != null)
                jo.Call("updateScore", Head_index + " " + score * 250);

        }
        if (coll.name.StartsWith("fd"))
        {
            --ate;

            coll.enabled = false;

            score++;

            Destroy(coll.gameObject);

            if (jo != null)
                jo.Call("updateScore", Head_index + " " + score * 250);
        }
        if (coll.name.StartsWith("tail") && !coll.name.EndsWith("[" + Head_index + "]"))
        {

            // Message to Android
            // Android에 해당 지렁이가 죽었음을 전송
            // AndroidJavaClass unityPlayer = new AndroidJavaClass("Android(java)Function 이 있는 패키지 이름 들어갈 곳");
            // unityPlayer.Call("함수 이름", "메세지 : Index");

            die = true;

        }
        // Collided with Tail or Border

    }
    */




}
