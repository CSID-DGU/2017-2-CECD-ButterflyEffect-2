using System.Collections;
using System.Collections.Generic;
using UnityEngine;


public static class Global
{
    public static float ppu = 1.0f;


    //Resolution info
    public static float screen_width = (float)(Screen.width);
    public static float screen_height = (float)(Screen.height);

    //Game (Defalut)Resolution Options
    public static float game_res_width = 640f;
    public static float game_res_height = 640f;

    ////Game (Runtime)Resoultion Options
    public static float game_res_width_run = screen_width / screen_width * game_res_width;
    public static float game_res_height_run = screen_height / screen_width * game_res_height;

    public static float resolution_fix = (game_res_width_run + game_res_height_run) /2;

    //Worms Speed
    public static float init_headspeed_mult = resolution_fix / 300;
    public static float init_headcurspeed_mult = 1.5f;

    //tail option 
    public static float tail_curspeed = 100f;
    public static float min_distance = 0.01f* resolution_fix;




    //Worms Size
    public static float head_size_ratio = 0.2f;
    public static float head_size = resolution_fix * head_size_ratio;

    public static float tail_size_ratio = 0.2f;
    public static float tail_size = resolution_fix * tail_size_ratio;

    //Food Size
    public static float food_size_ratio = 0.02f;
    public static float food_size = food_size_ratio * resolution_fix;

    public static float food_halo_ratio = 0.025f;
    public static float food_halo_size = food_halo_ratio * resolution_fix;

    public static float TailSizeIncreaseFactor = 0.3f;


    //Worms Revive time
    public static float WaitForLightTime = 1f;
    public static float WaitForRevive = 3f;


    public static Color skyblue = new Color(0.1f, 0.1f, 1,1);

    //Wroms color (readonly = final
    public static readonly Color[] player_Color = { Color.green, Color.red, skyblue , Color.yellow , Color.cyan };

}
