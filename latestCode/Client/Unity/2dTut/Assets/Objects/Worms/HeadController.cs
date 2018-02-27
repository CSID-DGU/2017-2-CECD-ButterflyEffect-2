using System.Collections;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;
using System;

public class HeadController : MonoBehaviour
{ 
    private Rigidbody rb;
    private Transform curtail;
    private Transform prevtail;
    private float dis;

    private int score = 0;

    private static AndroidJavaObject _admobPlugin;

    private int Head_index=255;
    private Color tail_color = Color.black;

    public void Head_index_set(int id)
    {
        //색 지정
        Head_index = id;
        tail_color = Global.player_Color[id];
    }

    //머리 이동 속도
    private float headspeed_mult = Global.init_headspeed_mult;
    //머리 회전 속도
    private float headcurspeed_mult = Global.init_headcurspeed_mult;

    //머리 회전 각도
    private float z_rotate_angle = 180.0f;

    public void Z_rotate_update(float z_angle)
    {
        z_rotate_angle = z_angle;
    }

    Vector3 move = new Vector3(0f, 0f, 0f);


    Vector3 direction = new Vector3(0.0f, 0.0f, 0.0f);

    // 유니티가 동작하는 액티비티를 저장하는 변수
    public AndroidJavaObject activity;

    void Awake()
    {

    }

    List<GameObject> tail = new List<GameObject>();

    //Did worm eat something?
    int ate = 2;

    //Did worm collide with other worms tail
    bool die = false;

    // Tail Prefab
    public GameObject tailPrefab;

    AndroidJavaClass pluginClass = new AndroidJavaClass("csid.butterflyeffect.ui.MainActivity");


    void Start()
    {
        rb = gameObject.GetComponent<Rigidbody>();
        rb.transform.localScale = new Vector3(Global.head_size, Global.head_size, Global.head_size);

        tailPrefab.transform.localScale = new Vector3(Global.tail_size, Global.tail_size, Global.tail_size);


        for (int i = 0; i < 2; i++)
            tail_create(rb.position);

      

    }

    void FixedUpdate()
    {
        rb.velocity = Vector3.zero;
        Vector3 newpose = rb.position;

        rb.transform.Rotate(0f, 0f, z_rotate_angle * Time.deltaTime*headcurspeed_mult);

        rb.transform.Translate(Mathf.Cos(Mathf.Deg2Rad * rb.transform.rotation.z) * headspeed_mult, Mathf.Sin(Mathf.Deg2Rad * rb.transform.rotation.z) * headspeed_mult, 0.0f);
        
        // 2.Move Tail to follow head
        if (tail.Count > 0)
        {

            dis = Vector3.Distance(rb.position, tail[0].transform.position) -Global.min_distance;
            float T = Time.deltaTime * (dis* dis / 160) * Global.tail_curspeed;


            if (T > 200.0f)
                T = 200.0f;
            else if(T<0f)
                    T = 0;

           // float temp = rb.transform.localScale.x * Global.tail_ratio;
            tail[0].transform.position = Vector3.MoveTowards(tail[0].transform.position, newpose, T);

          //  tail[0].transform.localScale = new Vector3(temp, temp, temp);

            for (int i = 1; i < tail.Count; i++)
            {

                
                
                curtail = tail[i].transform;
                prevtail = tail[i - 1].transform;

                dis = Vector3.Distance(prevtail.position, curtail.position) - Global.min_distance;

                newpose = prevtail.position;
                newpose.z = rb.position.z;

                T = Time.deltaTime * (dis * dis / 160) * Global.tail_curspeed;

 
                
                if (T > 200.0f)
                    T = 200.0f;
                else if (T < 0f)
                    T = 0;

                curtail.position = Vector3.MoveTowards(curtail.position, prevtail.position, T);


            }

        }

        // 3. Food CHK
        // Ate something? Then insert new Element into gap
        if (ate <= 0)
        {
            tail_create(newpose);
        }
        // 4. Collide head -> tail CHK
        /*
        if (die)
        {
            foreach(GameObject o in tail)
            {
                Destroy(o);
            }

            Destroy(gameObject);
            //지렁이가 죽었음을 Android에게 전달
        //   AndroidJavaClass unityPlayer = new AndroidJavaClass("Android(java)Function 이 있는 패키지 이름 들어갈 곳");
        //    unityPlayer.Call("함수 이름", "메세지");
        
        //  Destroy()
        }
        */
    }

    void tail_create(Vector3 newpose)
    {
        // Load Prefab into the world
        GameObject g = (GameObject)Instantiate(tailPrefab,
                                              newpose,
                                              Quaternion.identity);

        g.GetComponent<MeshRenderer>().material.color = tail_color;

        g.name =  "tail" + "[" + Head_index + "]";

        // parent 설정
        // g.gameObject.transform.SetParent(rb.transform, true);

        // Keep track of it in our tail list
        tail.Insert(tail.Count, g);

        // Reset the flag
        ate = 2;


    }

    void OnTriggerEnter(Collider coll)
    {

        // Trigger Food?
        if (coll.name.StartsWith("FoodPrefab"))
        {
            // Get longer in next Move call
            --ate;

            coll.enabled = false;

            score++;

            // Message to Android

            // Android에 점수 전송
            _admobPlugin.Call("updateScore", Head_index + " " + score * 250);

        }
        if (coll.name.StartsWith("tail") && !coll.name.EndsWith("["+Head_index+"]"))
        {
           
            // Message to Android
            // Android에 해당 지렁이가 죽었음을 전송
            // AndroidJavaClass unityPlayer = new AndroidJavaClass("Android(java)Function 이 있는 패키지 이름 들어갈 곳");
            // unityPlayer.Call("함수 이름", "메세지 : Index");

            die = true;

        }
        // Collided with Tail or Border

    }
}