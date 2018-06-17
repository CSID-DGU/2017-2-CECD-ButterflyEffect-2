using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class foodinfo : MonoBehaviour {

    public Mesh[] MeshType;
    public int score;
    private FoodType food_type = FoodType.none;

    public FoodType type
    {
        get
        {
            return food_type;
        }

        set
        {
            food_type = value;
            this.GetComponent<MeshFilter>().mesh = MeshType[(uint)food_type];
            score = 130 + (int)food_type*4;
            //this.GetComponent<Rigidbody>().local
        }
    }



}
