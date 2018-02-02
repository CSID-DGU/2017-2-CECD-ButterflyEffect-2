using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class AndroidManager : MonoBehaviour {

    private static AndroidManager _instance;
    public string androidLog = "No Log";
    

    //#if UNITY_ANDROID && !UNITY_EDITOR

    // 유니티가 동작하는 액티비티를 저장하는 변수
    public AndroidJavaObject activity;

    void Awake()
    {
        // 현재 실행 중인 유니티 액티비티를 가져와서 변수에 저장
        //AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        //activity = jc.GetStatic<AndroidJavaObject>("currentActivity");
    }

    //#endif

    void AndroidLog(string newAndroidLog)
    {
        androidLog = newAndroidLog;
    }

    public static AndroidManager Instance
    {
        get
        {
            if(_instance == null)
            {
                _instance = FindObjectOfType(typeof(AndroidManager)) as AndroidManager;
                if(_instance == null)
                {
                    _instance = new GameObject("AndroidManager").AddComponent<AndroidManager>();
                }
            }
            return _instance;
        }
    }

    //// Use this for initialization
    //void Start()
    //{

    //}

    //// Update is called once per frame
    //void Update()
    //{

    //}
}
