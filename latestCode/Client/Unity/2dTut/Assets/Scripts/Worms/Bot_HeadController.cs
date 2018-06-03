using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

public class Bot_HeadController : HeadController {

    public int quadrant = 1;
    public int food_quadrant = 1;

    GameObject GameCamera;
    GameObject[] list;

    public  HeadController HC;

    float degree = -1f;
    float distance;

    //AI Parameter
    float BotRadian;
    float BotX;
    float BotY;
    
    // Update is called once per frame
    public float preDegree;
    bool initialize = false;


    public double GetPosition(Vector3 curPos)
    {
        double theta = 0d;
        
        if (list[0] != null)
        {
            Vector3 v3 = list[0].transform.position;
            theta = (float)((Mathf.Atan2((v3.y - curPos.y), (v3.x - curPos.x)) / Math.PI) * 180f);
            // Get distance
            distance = Mathf.Sqrt(Mathf.Pow((v3.x - curPos.x), 2) + Mathf.Pow((v3.y-curPos.y),2));
            return theta;
        }
        else
        {   
            return -1f;
        }
    }

    // Use this for initialization
    private void Start () {
        GameCamera = GameObject.Find("Camera");
        list = GameCamera.GetComponent<SpawnFood>().FoodprefabArray;
        rb = gameObject.GetComponent<Rigidbody>();
        rb.transform.localScale = new Vector3(Global.head_size, Global.head_size, Global.head_size);
        tailPrefab.transform.localScale = new Vector3(Global.tail_size, Global.tail_size, Global.tail_size);

        WormLight = GameObject.Find("BotLight[" + Head_index + "]");
        Debug.Log("Head_index : " + Head_index + ", is light exist : " + (WormLight != null));

        thisMesh = gameObject.GetComponent<MeshRenderer>();

        for (int i = 0; i < 1; i++)
            TailCreate(rb.position);
        
        
	}

    private void FixedUpdate () {

        if (ImReviving == true)
            return;

        MeshRenderer mr;
        Vector3 newpose = rb.position;
        float speed = headcurspeed_mult * 1.2f;
        degree = (float)GetPosition(newpose);

        //Debug.Log("degree is " + degree);
        Vector3 move;
        
        if (degree == -1f)
        {
            move = new Vector3(0, 0, 0f);
            initialize = false;
        }
        else
        {
            if(initialize == false)
            {
                initialize = true;
                if (degree >= 0)
                    preDegree = 1f;
                else
                    preDegree = -1f;
            }
            if (preDegree >= 0)
            {
                if (distance != 0)
                {
                    preDegree = preDegree + (3 * (Math.Abs(degree - preDegree) / distance));
                    if ( preDegree > degree )
                        preDegree = degree;
                }
                
            }
            else if (preDegree < 0)
            {
                if (distance != 0)
                {
                    preDegree = preDegree - (3 * (Math.Abs(degree - preDegree) / distance));
                    if ( preDegree < degree )
                        preDegree = degree;
                }
                
            }

            //Debug.Log("preDegree is " + preDegree);
            BotRadian = preDegree * Mathf.Deg2Rad;
            BotX = Mathf.Cos(BotRadian);
            BotY = Mathf.Sin(BotRadian);
            move = new Vector3(BotX, BotY, 0f) * speed;
            
        }
        
        newpose += move;
        rb.transform.rotation = new Quaternion(0, 0, preDegree, distance/speed);
        rb.transform.position = newpose;


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


            //  tail[0].transform.localScale = new Vector3(temp, temp, temp);

            for (int i = 1; i < tail.Count; i++)
            {
                if (thisMesh.enabled == false)
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

            }
        }

        // 3. Food CHK
        // Ate something? Then insert new Element into gap
        if (ate <= 0)
        {
            TailCreate(newpose);
        }

        // 4. Collide head -> tail CHK
        if (die)
        {

            if (gameObject.GetComponent<SphereCollider>().enabled == true) o = StartCoroutine(Destroy_tail(tail, gameObject));

            if (jo != null)
                jo.Call("updateDie", "" + Head_index);

            die = false;

        }
    }
    

    private void OnTriggerEnter(Collider coll)
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

    private IEnumerator Destroy_tail(List<GameObject> tail_list, GameObject head)
    {
        ImReviving = true;
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
        for (int i = count - 1; i >= 0; i--)
        {
            SpawnFood_die(tail_list[i].transform);

            Destroy(tail_list[i]);
            tail_list.RemoveAt(i);
            yield return new WaitForSeconds(0.04f);
        }

        head.GetComponent<MeshRenderer>().enabled = false;
        yield return new WaitForSeconds(WaitForLight);

        //x position between left and right border
        int x = (int)(UnityEngine.Random.Range(0, 4));
        //y position between top and bottom border
        int y = (int)(UnityEngine.Random.Range(0, 4));

        float w_unit = GameObject.Find("Camera").GetComponent<Create_World>().i_width / 12;
        float h_unit = GameObject.Find("Camera").GetComponent<Create_World>().i_height / 12;

        this.transform.position = new Vector3((x * 3 - 5) * (int)w_unit, (y * 3 - 5) * (int)h_unit, rb.position.z);

        WormLight.GetComponent<Transform>().position = new Vector3((x * 3 - 5) * (int)w_unit, (y * 3 - 5) * (int)h_unit, Global.head_size * (-0.5f));
        WormLight.GetComponent<Light>().intensity = 4f;
        WormLight.SetActive(true);
        WormLight.GetComponent<Light>().enabled = true;

        
        yield return new WaitForSeconds(WaitForRevive);

        head.GetComponent<MeshRenderer>().enabled = true;

        headspeed_mult = Global.init_headspeed_mult;
        //Destroy(head);

        head.GetComponent<SphereCollider>().enabled = true;

        ate -= 4;

        WormLight.SetActive(false);


        ImReviving = false;


        yield return null;


    }



}
