using System.Collections;
using System.Collections.Generic;
using UnityEngine;


public class SFXScript : MonoBehaviour
{
    public AudioSource WaitBackgroundMusic;
    public AudioSource GameBackgroundMusic;
    public AudioSource[] EatFood;
    
    public void PlayEatSound()
    {
        EatFood[UnityEngine.Random.Range((int)0, (int)4)].Play();
    }

    public void PlayWaitBGM()
    {
        GameBackgroundMusic.Stop();
        WaitBackgroundMusic.loop = true;
        WaitBackgroundMusic.Play();
    }

    public void PlayGameBGM()
    {
        WaitBackgroundMusic.Stop();
        GameBackgroundMusic.loop = true;
        GameBackgroundMusic.Play();
        
    }

    public void Stop_PlayWaitBGM()
    {
        WaitBackgroundMusic.Stop();
    }

    public void Stop_PlayGameBGM()
    {
        GameBackgroundMusic.Stop();
    }



}
