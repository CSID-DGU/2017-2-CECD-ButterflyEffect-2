using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class rotateFood: MonoBehaviour {

    private Rigidbody food;
    private Light foodlight;
    // Use this for initialization
    void Start () {
        food = gameObject.GetComponent<Rigidbody>();
        foodlight = gameObject.GetComponent<Light>();
        
    }

    int flag = 1;
    float modifier = 1;

	// Update is called once per frame
	void FixedUpdate () {
        if (flag == 1)
        {
            foodlight.range = Global.food_halo_size * modifier;
            modifier *= 1.01f;
            if (modifier > 1.1)
                flag = 0;
        }
        else
        {
            foodlight.range = Global.food_halo_size * modifier;
            modifier *= 0.99f;
            if (modifier < 0.9)
                flag = 1;
        }
        food.transform.Rotate(0.0f, 0.0f, 1.0f);
        food.velocity *= 1.2f;
    }

    public void ate_by_worm()
    {
        Invoke("disabled", 0.2f);
        return;
    }

    public void fd_ate_by_worm()
    {
        Invoke("fd_disabled", 0.2f);
        return;
    }

    private void disabled()
    {
        food.velocity = Vector3.zero;
        gameObject.SetActive(false);
        return;
    }
    private void fd_disabled()
    {
        gameObject.SetActive(false);
        Destroy(gameObject);
        return;
    }
}