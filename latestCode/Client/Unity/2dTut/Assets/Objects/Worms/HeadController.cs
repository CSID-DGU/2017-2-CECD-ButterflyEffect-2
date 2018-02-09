using System.Collections;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;
using System;

public class HeadController : MonoBehaviour
{
    public float playerspeedmult;

    private Rigidbody rb;
    private Transform Headtransform;
    private Transform curtail;
    private Transform prevtail;
    private float dis;
    private int head_number = 0;


    //머리 이동 속도
    public float headspeed = 0.25f;
    //머리 회전 속도
    public float headcurspeed = 1f;

    //꼬리 추적 속도
    public float curspeed = 200f;
    public float min_distance = 1;
    private float z_rotate_angle = 0.0f;

    Vector3 move = new Vector3(0f, 0f, 0f);


    public string angle_String = "no angle";

    Vector3 direction = new Vector3(0.0f, 0.0f, 0.0f);

    // 유니티가 동작하는 액티비티를 저장하는 변수
    public AndroidJavaObject activity;


    float angle = 0.0f;

    void Awake()
    {
        // 현재 실행 중인 유니티 액티비티를 가져와서 변수에 저장
        //AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        //activity = jc.GetStatic<AndroidJavaObject>("currentActivity");
        //Debug.Log("Activity Call Complete");
    }

    public void AndroidLog(string degree)
    {

        string[] str = degree.Split(' ');

        int person_num = int.Parse(str[0]);

        Debug.Log("human: " + str[0] + ", degree : " + str[1]);



        //angle is move input from frame
        angle_String = str[1];
        angle = float.Parse(str[1]);

        if (angle < 80 && angle > 10)
        {
            z_rotate_angle = -40.0f;
        }
        else if (angle > 80 && angle < 100)
        {
            z_rotate_angle = 0.0f;
        }
        else if (angle > 100 && angle < 170)
        {
            z_rotate_angle = 40.0f;
        }
        else
        {
            z_rotate_angle = 0.0f;
        }
    }

    List<Transform> tail = new List<Transform>();

    //Did snake eat something?
    bool ate = false;

    // Tail Prefab
    public GameObject tailPrefab;


    void Start()
    {
        rb = gameObject.GetComponent<Rigidbody>();


    }

    void FixedUpdate()
    {


        // 1.Add force head into new direction
        //GetAxis = 방향키
        //float moveHorizontal = Input.GetAxis("Horizontal");
        //float moveVertical = Input.GetAxis("Vertical");
        //Vector 3를 직접 새로 생성하여 2차원 방향 이동 구현
        //AndroidLog("1 30");
        //Vector3 movement = move;
        //rb.velocity = Quaternion.AngleAxis(angle,Vector3.up) * rb.velocity;
        //rb.AddForce(movement * playerspeedmult);
        //float a = rb.velocity.x;
        //rb.transform.rotation = Quaternion.Euler(0, 0, -angle/10);
        //rb.AddForce(rb.transform.forward * 100);
        //rb.AddForce(rb.transform.rotation);

        rb.velocity = Vector3.zero;
        Vector3 newpose = rb.position;

        rb.transform.Rotate(0f, 0f, z_rotate_angle * Time.deltaTime);

        rb.transform.Translate(Mathf.Cos(Mathf.Deg2Rad * rb.transform.rotation.z) * headspeed, Mathf.Sin(Mathf.Deg2Rad * rb.transform.rotation.z) * headspeed, 0.0f);


        // 2.Move Tail to follow head
        if (tail.Count > 0)
        {

            dis = Vector3.Distance(rb.position, tail[0].position);
            float T = Time.deltaTime * dis * dis / min_distance * curspeed;

            if (T > 100.0f)
                T = 100.0f;

            tail[0].position = Vector3.MoveTowards(tail[0].position, newpose, T);

            for (int i = 1; i < tail.Count; i++)
            {
                curtail = tail[i];
                prevtail = tail[i - 1];

                dis = Vector3.Distance(prevtail.position, curtail.position);

                newpose = prevtail.position;
                newpose.z = rb.position.z;

                T = Time.deltaTime * dis * dis / min_distance * curspeed;

                if (T > 100.0f)
                    T = 100.0f;

                curtail.position = Vector3.MoveTowards(curtail.position, prevtail.position, T);
            }

        }

        // 3. Food CHK
        // Ate something? Then insert new Element into gap
        if (ate)
        {
            // Load Prefab into the world
            GameObject g = (GameObject)Instantiate(tailPrefab,
                                                  newpose,
                                                  Quaternion.identity);

            // Keep track of it in our tail list
            tail.Insert(tail.Count, g.transform);

            // Reset the flag
            ate = false;
        }
    }

    void OnTriggerEnter(Collider coll)
    {
        Debug.Log("trigger is called");
        // Trigger Food?
        if (coll.name.StartsWith("FoodPrefab"))
        {
            // Get longer in next Move call
            ate = true;



            // Remove the Food
            Destroy(coll.gameObject);
        }
        // Collided with Tail or Border
        else
        {
            // ToDo 'You lose' screen
        }
    }
}