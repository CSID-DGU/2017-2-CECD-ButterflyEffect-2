using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public static class Global
{
    public static float ppu = 1.0f;

    //Resolution info
    public static float screen_width = (float)(Screen.width);
    public static float screen_height = (float)(Screen.height);

    //Game Resolution Options
    public static float game_res_width = 640f;
    public static float game_res_height = 640f;

    //Worms Speed
    public static float init_headspeed_mult = game_res_width / 100;
    public static float init_headcurspeed_mult = 1.5f;

    //tail option 
    public static float tail_curspeed = 100f;
    public static float min_distance = 0.01f* game_res_width;




    //Worms Size
    public static float head_size_ratio = 0.05f;
    public static float tail_size_ratio = 0.04f;

    public static float head_size = 32f;
    public static float tail_ratio = 0.8f;

}