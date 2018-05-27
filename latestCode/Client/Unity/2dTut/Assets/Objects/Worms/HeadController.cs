using System.Collections;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;
using System.Threading;


public class HeadController : MonoBehaviour
{

    public GameObject food;
    private Rigidbody rb;
    private Transform curtail;
    private Transform prevtail;
    private float dis;
    private int score = 0;
    private static AndroidJavaObject _admobPlugin;
    private int Head_index = 255;
    private Color32 headcolor = Color.black;
    private Color32 tailcolor = Color.black;
    private MeshRenderer thisMesh;
    bool isboost = false;

    private float WaitForLight = Global.WaitForLightTime;
    private float WaitForRevive = Global.WaitForRevive;

    private GameObject WormLight;

    private float boost_mult = 1.0f;

    public void boost_enable()
    {
        boost_mult = 1.8f;
        isboost = true;
    }
    public void boost_unable() {
        boost_mult = 1.0f;
        isboost = false;
    }

    public void Head_index_set(int id)
    {
        Head_index = id;
    }

    public void set_tail_color(Color32 worm_color)
    {
        headcolor = worm_color;
        tailcolor = (Color)worm_color * 0.90f;

    }

    //머리 이동 속도
    private float headspeed_mult = Global.init_headspeed_mult;
    //머리 회전 속도
    private float headcurspeed_mult = Global.init_headcurspeed_mult;

    //머리 크기
    private Vector3 headSize = new Vector3(Global.head_size, Global.head_size, Global.head_size);
    //꼬리 크기

    private Vector3 tailSize = new Vector3(Global.tail_size, Global.tail_size, Global.tail_size);
    //머리 회전 각도
    private float z_rotate_angle = 120.0f;

    public void Z_rotate_update(float z_angle)
    {
        z_rotate_angle = z_angle;
    }

    // 유니티가 동작하는 액티비티를 저장하는 변수
    public AndroidJavaObject activity;

    List<GameObject> tail = new List<GameObject>();

    //Did worm eat something?
    int ate = 2;
    //Did worm collide with other worms tail
    bool die = false;
    //Coroutine
    Coroutine o;
    // Tail Prefab
    public GameObject tailPrefab;
    AndroidJavaClass jc; AndroidJavaObject jo;

    void Start()
    {
        rb = gameObject.GetComponent<Rigidbody>();
        rb.transform.localScale = headSize;

        tailPrefab.transform.localScale = tailSize;

        for (int i = 0; i < 1; i++)
            tail_create(rb.position);
        o = null;

        thisMesh = gameObject.GetComponent<MeshRenderer>();


        WormLight =GameObject.Find("Light[" + Head_index + "]");

        jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        jo = jc.GetStatic<AndroidJavaObject>("currentActivity");


    }

    float min_distance = Global.min_distance;
    float tail_curspeed = Global.tail_curspeed;
    float boost_fuel = 1.0f;
    float TailSizeIncreaseFactor = Global.TailSizeIncreaseFactor;

    void FixedUpdate()
    {
        MeshRenderer mr;
        //충돌시 발생하는 velocity 제거
        rb.velocity = Vector3.zero;

        Vector3 newpose = rb.position;

        rb.transform.Rotate(0f, 0f, z_rotate_angle * Time.deltaTime * headcurspeed_mult);

        rb.transform.Translate(Mathf.Cos(Mathf.Deg2Rad * rb.transform.rotation.z) * headspeed_mult* boost_mult, Mathf.Sin(Mathf.Deg2Rad * rb.transform.rotation.z) * headspeed_mult * boost_mult, 0.0f);

        // 2.Move Tail to follow head
        if (tail.Count > 0 && tail[0] != null)
        {

            dis = Vector3.Distance(rb.position, tail[0].transform.position) - min_distance;
            float T = Time.deltaTime * (dis * dis / 160) * tail_curspeed;

            if (T > 200.0f)
                T = 200.0f;
            else if (T < 0f)
                T = 0;

            if (thisMesh.enabled == true)
            {
                // float temp = rb.transform.localScale.x * Global.tail_ratio;
                tail[0].transform.position = Vector3.MoveTowards(tail[0].transform.position, newpose, T);
            }

            mr = tail[0].GetComponent<MeshRenderer>();
            if (isboost)
            {
                mr.material.color = (Color)tailcolor * (1 + boost_fuel);
            }
            else
            {
                mr.material.color = (Color)tailcolor * (1f);
            }

            //  tail[0].transform.localScale = new Vector3(temp, temp, temp);

            for (int i = 1; i < tail.Count; i++)
            {
                if(thisMesh.enabled == false)
                {
                    break;
                }

                curtail = tail[i].transform;
                prevtail = tail[i - 1].transform;

                dis = Vector3.Distance(prevtail.position, curtail.position) - min_distance;

                newpose = prevtail.position;
                newpose.z = rb.position.z;

                T = Time.deltaTime * (dis * dis / 160) * tail_curspeed;

                if (T > 200.0f)
                    T = 200.0f;
                else if (T < 0f)
                    T = 0;

                curtail.position = Vector3.MoveTowards(curtail.position, prevtail.position, T);

                mr = curtail.GetComponent<MeshRenderer>();
                if (isboost)
                {
                    mr.material.color = (Color)tailcolor * (1 + boost_fuel);
                }
                else
                {
                    boost_fuel = 1f;
                    mr.material.color = (Color)tailcolor * (1f);
                }
            }
        }

        // 3. Food CHK
        // Ate something? Then insert new Element into gap
        if (ate <= 0)
        {
            tail_create(newpose);
        }

        // 4. Collide head -> tail CHK
        if (die)
        {
            
            if(gameObject.GetComponent<SphereCollider>().enabled == true ) o = StartCoroutine( Destroy_tail(tail, gameObject));

            if(jo!=null)
            jo.Call("updateDie", "" + Head_index);

            die = false;
        
        }
        
        // 5. boost fuel CHK
        if (boost_fuel < 0)
        {
            GameObject last_tail = tail[tail.Count - 1];
            tail.RemoveAt(tail.Count - 1);

            SpawnFood_die(last_tail.transform);

            Destroy(last_tail);

            boost_fuel = 1f;
        }
        
        mr = GetComponent<MeshRenderer>();
        if (isboost)
        {
            boost_fuel -= Time.deltaTime * 0.5f;
            mr.material.color = (Color)headcolor * (1 + boost_fuel);
        }
        else
        {
            boost_fuel = 1f;
            mr.material.color = (Color)headcolor * (1f);
        }

    }

    void tail_create(Vector3 newpose)
    {
        // Load Prefab into the world
        GameObject g = (GameObject)Instantiate(tailPrefab,
                                              newpose,
                                              Quaternion.identity);

        g.GetComponent<MeshRenderer>().material.color = tailcolor;

        g.name = "tail" + "[" + Head_index + "]";

        // parent 설정
        // g.gameObject.transform.SetParent(rb.transform, true);

        // Keep track of it in our tail list
        tail.Insert(tail.Count, g);

        // Reset the flag
        ate += 3;

        //growing worms size.
        float tailRatio = 1.0f + (tail.Count() / 30f);
        for (int i = 0; i < tail.Count(); i++)
        {
            //tail[i].transform.localScale = new Vector3(Global.tail_size * tailRatio, Global.tail_size * tailRatio, Global.tail_size * tailRatio);
            tail[i].transform.localScale = tailSize * TailSizeIncreaseFactor * tailRatio;
        }
        //rb.transform.localScale = new Vector3(Global.head_size * tailRatio, Global.head_size * tailRatio, Global.head_size * tailRatio);
        rb.transform.localScale = headSize * TailSizeIncreaseFactor * tailRatio;

    }

    public IEnumerator Destroy_tail(List<GameObject> tail_list, GameObject head)
    {

        head.GetComponent<SphereCollider>().enabled = false;
        foreach (GameObject tail in tail_list)
        {
            tail.GetComponent<SphereCollider>().enabled = false;
        }

            headspeed_mult = 0f;
        score = 0;
        //jo.Call("updateScore", Head_index + " " + score * 250);
        //head.GetComponent<MeshRenderer>().enabled = true;
        int count = tail_list.Count;
        for (int i = count -1; i >= 0; i--)
        {
            SpawnFood_die(tail_list[i].transform);
            
            Destroy(tail_list[i]);
            tail_list.RemoveAt(i);
            yield return new WaitForSeconds(0.04f);
        }

        head.GetComponent<MeshRenderer>().enabled =false;
        yield return new WaitForSeconds(WaitForLight);

        //x position between left and right border
        int x = (int)(Random.Range(0, 4));
        //y position between top and bottom border
        int y = (int)(Random.Range(0, 4));

        float w_unit = GameObject.Find("Camera").GetComponent<Create_World>().i_width / 12;
        float h_unit = GameObject.Find("Camera").GetComponent<Create_World>().i_height / 12;

        this.transform.position = new Vector3((x * 3 - 5) * (int)w_unit, (y * 3 - 5) * (int)h_unit, rb.position.z);

        WormLight.GetComponent<Transform>().position = new Vector3((x * 3 - 5) * (int)w_unit, (y * 3 - 5) * (int)h_unit, Global.head_size * (-0.5f));
        WormLight.SetActive(true);
        WormLight.GetComponent<Light>().enabled = true;

        yield return new WaitForSeconds(WaitForRevive);

        head.GetComponent<MeshRenderer>().enabled = true;

        headspeed_mult = Global.init_headspeed_mult;
        //Destroy(head);

        head.GetComponent<SphereCollider>().enabled = true;

        ate -= 4;

        WormLight.SetActive(false);


        if (jo != null)
            jo.Call("updateRevive", "" + Head_index);


        yield return null;


    }

    void OnTriggerEnter(Collider coll)
    {
        

        // Trigger Food?
        if (coll.name.StartsWith("FoodPrefab"))
        {
            //dis = Vector3.Distance(coll.transform.position, rb.transform.position);
            Vector3 move_force = rb.transform.position - coll.transform.position;
            coll.GetComponent<Rigidbody>().AddForce(move_force*10);

            rotateFood fd = coll.gameObject.GetComponent<rotateFood>();

            fd.ate_by_worm();

            ate -= 4;

            score++;
            if (jo != null)
                jo.Call("updateScore", Head_index + " " + score * 250);

            return;

        }
        else if (coll.name.StartsWith("fd"))
        {

            //dis = Vector3.Distance(coll.transform.position, rb.transform.position);
            Vector3 move_force = rb.transform.position - coll.transform.position;
            coll.GetComponent<Rigidbody>().AddForce(move_force * 10);

            rotateFood fd = coll.gameObject.GetComponent<rotateFood>();

            fd.fd_ate_by_worm();

            ate -= 1;

            score++;
            if (jo != null)
                jo.Call("updateScore", Head_index + " " + score * 250);

            return;
        }
        if(coll.name.StartsWith("tail") && !coll.name.EndsWith("[" + Head_index + "]"))
        {

            die = true;
            return;
        }
        // Collided with Tail or Border

    }

    public void SpawnFood_die(Transform tf)
    {
        
        float x = (float)UnityEngine.Random.Range(-boost_mult, boost_mult);
        float y = (float)UnityEngine.Random.Range(-boost_mult, boost_mult);

        float z = -3f;


        Transform fd =  Instantiate(food, new Vector3(tf.position.x, tf.position.y, z),
            Quaternion.identity).transform; // default rotation

        fd.GetComponent<Light>().color = tailcolor;
        fd.GetComponent<Light>().intensity = 1.5f;

        fd.Translate(x,y, 0);

        fd.name = "fd";


    }
}