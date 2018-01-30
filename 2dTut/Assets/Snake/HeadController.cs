using System.Collections;
using System.Collections.Generic;
using UnityEngine;


public class HeadController : MonoBehaviour
{

    public float playerspeedmult;

    private Rigidbody rb;
    private Transform curtail;
    private Transform prevtail;
    private float dis;
    public float curspeed = 100;
    public float min_distance = 1;

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

        Vector3 newpose = rb.position;

        // 1.Add force head into new direction
        //GetAxis = 방향키
        float moveHorizontal = Input.GetAxis("Horizontal");
        float moveVertical = Input.GetAxis("Vertical");

        //Vector 3를 직접 새로 생성하여 2차원 방향 이동 구현
        Vector3 movement = new Vector3(moveHorizontal, moveVertical, 0.0f);

        //print (movement.ToString ("G4"));
        //print ("loop");

        rb.AddForce(movement * playerspeedmult);

        float a = rb.velocity.x;

        //print((movement * playerspeedmult).ToString("G4"));

        // 2.Move Tail to follow head
        if (tail.Count > 0)
        {

            dis = Vector3.Distance(rb.position, tail[0].position);
            float T = Time.deltaTime * dis*dis / min_distance * curspeed;

            if (T > 100.0f)
                T = 100.0f;

           // tail[0].position = Vector3.Slerp(tail[0].position, newpose, T);
           // tail[0].rotation = Quaternion.Slerp(tail[0].rotation, rb.rotation, T);
            tail[0].position = Vector3.MoveTowards(tail[0].position, newpose, T);

            //print("tail[0] :"+ tail[0].position.x + "," + tail[0].position.y + ","+ tail[0].position.z );
            //print("rb :" + rb.position.x + "," + rb.position.y + "," +rb.position.z );


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

                //curtail.position = Vector3.Slerp(curtail.position, newpose, T);
                //curtail.rotation = Quaternion.Slerp(curtail.rotation, prevtail.rotation, T);
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
        // Food?
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