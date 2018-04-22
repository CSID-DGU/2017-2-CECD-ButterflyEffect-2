using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

public class Bot_HeadController : MonoBehaviour {
    public GameObject food;
    private Rigidbody rb;
    private Transform curtail;
    private Transform prevtail;
    private float dis;
    private static AndroidJavaObject _admobPlugin;
    private int Head_index = 255;
    private Color32 tailcolor = Color.black;
    bool isboost = false;

    private float boost_mult = 1.0f;
    
    public void Head_index_set(int id)
    {
        Head_index = id;
    }

    public void set_tail_color(Color32 worm_color)
    {
        tailcolor = new Color32(
            (byte)(worm_color.r*0.95),
            (byte)(worm_color.g*0.95),
            (byte)(worm_color.b*0.95), 255);
    }

    //머리 이동 속도
    private float headspeed_mult = Global.init_headcurspeed_mult;

    //머리 회전 속도
    private float headcurspeed_mult = Global.init_headcurspeed_mult;

    //머리 회전 각도
    private float z_rotate_angle = 120.0f;

    public void Z_rotate_update(float z_angle)
    {
        //z_rotate_angle = z_angle;
        z_rotate_angle = 90;
    }

    Vector3 move = new Vector3(0.0f, 0.0f, 0.0f);
    Vector3 direction = new Vector3(0.0f, 0.0f, 0.0f);

    //  유니티가 동작하는 액티비티를 저장하는 변수
    public AndroidJavaObject activity;

    List<GameObject> tail = new List<GameObject>();

    // Did worm eat something?
    int ate = 2;

    // Did worm collide with other worms tail
    bool die = false;

    // Tail Prefab
    public GameObject tailPrefab;
    
	// Use this for initialization
	void Start () {
        rb = gameObject.GetComponent<Rigidbody>();
        rb.transform.localScale = new Vector3(Global.head_size, Global.head_size, Global.head_size);

        tailPrefab.transform.localScale = new Vector3(Global.tail_size, Global.tail_size, Global.tail_size);

        for (int i = 0; i < 1; i++)
            tail_create(rb.position);
	}

    float min_distance = Global.min_distance;
    float tail_curspeed = Global.tail_curspeed;
    float boost_fuel = 0.2f;

	// Update is called once per frame
	void FixedUpdate () {
        Vector3 newpose = rb.position;

        rb.transform.Rotate(0f, 0f, z_rotate_angle * Time.deltaTime * headcurspeed_mult);

        rb.transform.Translate(Mathf.Cos(Mathf.Deg2Rad * rb.transform.rotation.z) * headcurspeed_mult * boost_mult, Mathf.Sin(Mathf.Deg2Rad * rb.transform.rotation.z)* headcurspeed_mult * boost_mult, 0.0f);

        if (tail.Count > 0)
        {
            dis = Vector3.Distance(rb.position, tail[0].transform.position) - min_distance;
            float T = Time.deltaTime * (dis * dis / 160) * Global.tail_curspeed;

            if (T > 200.0f)
                T = 200.0f;
            else if (T < 0f)
                T = 0;

            tail[0].transform.position = Vector3.MoveTowards(tail[0].transform.position, newpose, T);

            for(int i = 1; i < tail.Count; i++)
            {
                curtail = tail[i].transform;
                prevtail = tail[i - 1].transform;

                dis = Vector3.Distance(prevtail.position, curtail.position) - min_distance;

                newpose = prevtail.position;
                newpose.z = rb.position.z;

                T = Time.deltaTime * (dis * dis / 160) * Global.tail_curspeed;

                if (T > 200.0f)
                    T = 200.0f;
                else if (T < 0f)
                    T = 0;

                curtail.position = Vector3.MoveTowards(curtail.position, prevtail.position, T);
            }

            if (isboost)
            {
                boost_fuel -= Time.deltaTime;
            }
        }

        // 3. Food CHK
        // Ate something? Then insert new Element into gap

        if (ate <= 0)
        {
            tail_create(newpose);
        }

        // 4. Collide haed -> tail CHK
        if(die)
        {
            foreach(GameObject o in tail)
            {
                SpawnFood_die(o.transform);
                Destroy(o);
            }

            Destroy(gameObject);
        }

        if(boost_fuel < 0)
        {
            GameObject last_tail = tail[tail.Count - 1];
            tail.RemoveAt(tail.Count - 1);

            SpawnFood_die(last_tail.transform);
            Destroy(last_tail);
            boost_fuel = 0.2f;
        }
	}

    public void tail_create(Vector3 newpose)
    {
        // Load Prefab into the world
        GameObject g = (GameObject)Instantiate(tailPrefab, newpose, Quaternion.identity);

        g.GetComponent<MeshRenderer>().material.color = tailcolor;

        g.name = "tail" + "[" + Head_index + "]";

        // parent 설정
        // g.gameObject.transform.SetParent(rb.transform, true);

        // Keep track of it in our tail list
        tail.Insert(tail.Count, g);

        // Reset the count
        ate += 2;

        // growing worms size
        float tailRatio = 1.0f + (tail.Count / 30f);
        for(int i =0; i < tail.Count; i++)
        {
            tail[i].transform.localScale = new Vector3(Global.tail_size * tailRatio, Global.tail_size * tailRatio, Global.tail_size * tailRatio);
        }
        rb.transform.localScale = new Vector3(Global.head_size * tailRatio, Global.head_size * tailRatio, Global.head_size * tailRatio);
    }

    public void OnTriggerEnter(Collider coll)
    {
        // Trigger Food?
        if (coll.name.StartsWith("FoodPrefab"))
        {
            //dis = Vector3.Distance(coll.transform.position, rb.transform.position);
            Vector3 move_force = rb.transform.position - coll.transform.position;
            coll.GetComponent<Rigidbody>().AddForce(move_force * 10);

            rotateFood fd = coll.gameObject.GetComponent<rotateFood>();

            fd.ate_by_worm();

            ate -= 4;


        }
        if (coll.name.StartsWith("fd"))
        {

            //dis = Vector3.Distance(coll.transform.position, rb.transform.position);
            Vector3 move_force = rb.transform.position - coll.transform.position;
            coll.GetComponent<Rigidbody>().AddForce(move_force * 10);

            rotateFood fd = coll.gameObject.GetComponent<rotateFood>();

            fd.fd_ate_by_worm();

            ate -= 1;

        }

        // When current worm collides against tail of another worm
        if (coll.name.StartsWith("tail") && !coll.name.EndsWith("[" + Head_index + "]"))
        {
            die = true;
        }
    }

    public void SpawnFood_die(Transform tf)
    {
        float x = (float)UnityEngine.Random.Range(-boost_mult, boost_mult);
        float y = (float)UnityEngine.Random.Range(-boost_mult, boost_mult);

        float z = -3f;

        Transform fd = Instantiate(food, new Vector3(tf.position.x, tf.position.y, z),
            Quaternion.identity).transform;

        fd.GetComponent<MeshRenderer>().material.color = Global.player_Color[Head_index];

        fd.Translate(x, y, 0);

        fd.name = "fd";
    }
}
