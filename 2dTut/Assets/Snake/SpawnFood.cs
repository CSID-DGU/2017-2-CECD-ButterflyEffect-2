using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SpawnFood : MonoBehaviour {

    // Food Prefab
    public GameObject food;

    // Borders (out of range spwan avoid)
    public Transform border_Top;
    public Transform border_Bottom;
    public Transform border_Left;
    public Transform border_Right;

    // Use this for initialization
    void Start () {
        // Spawn food every 4 seconds, starting in 3
        InvokeRepeating("Spawn", 3, 0.5f);
    }

    //Spawn one piece of food
    //now it spawn random food using 'INT' position!
    void Spawn()
    {

        //x position between left and right border
        int x = (int)Random.Range(border_Left.position.x,
                                  border_Right.position.x);
        //y position between top and bottom border
        int y = (int)Random.Range(border_Top.position.y,
                                  border_Bottom.position.y);

        int z = -2;


        Instantiate(food, new Vector3(x, y,z),
            Quaternion.identity); // default rotation

    }


    /* Food don't need to be updated per frame
    // Update is called once per frame
    void Update()
    {

    }
    */
}
