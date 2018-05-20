using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class foodinfo : MonoBehaviour {

    public Mesh[] MeshType;

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
            Debug.Log(food_type);
            this.GetComponent<MeshFilter>().mesh = MeshType[(uint)food_type];
        }
    }



}
