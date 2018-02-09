using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class rotateFood: MonoBehaviour {

    private Rigidbody food;

	// Use this for initialization
	void Start () {
        food = gameObject.GetComponent<Rigidbody>();
    }
	
	// Update is called once per frame
	void FixedUpdate () {
        food.transform.Rotate(0f, 0f, 1.0f);

    }
}
