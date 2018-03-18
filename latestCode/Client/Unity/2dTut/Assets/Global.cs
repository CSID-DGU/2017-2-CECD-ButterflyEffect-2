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
    public static float init_headspeed_mult = game_res_width / 480;
    public static float init_headcurspeed_mult = 1.5f;

    //tail option 
    public static float tail_curspeed = 100f;
    public static float min_distance = 0.01f* game_res_width;




    //Worms Size
    public static float head_size_ratio = 0.05f;
    public static float head_size = game_res_width * head_size_ratio;

    public static float tail_size_ratio = 0.04f;
    public static float tail_size = game_res_width * tail_size_ratio;

    //Food Size
    public static float food_size_ratio = 0.03f;
    public static float food_size = food_size_ratio * game_res_width;

    public static Color skyblue = new Color(0.3f, 0.3f, 1,1);

    //Wroms color (readonly = final
    public static readonly Color[] player_Color = { Color.green, Color.red, skyblue , Color.yellow , Color.cyan };

   

}