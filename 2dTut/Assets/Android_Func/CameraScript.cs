using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class CameraScript : MonoBehaviour
{
    private bool camAvailable;
    private WebCamTexture backCam;
    private Texture defaultBackground;

    public RawImage background;
    public AspectRatioFitter Fit;

    private void Start()
    {
        //defaultBackground = background.texture;
        WebCamDevice[] devices = WebCamTexture.devices;

        //We do not have any Camera here.
        if (devices.Length == 0)
        {
            Debug.Log("No camera detected");
            camAvailable = false;
            return;
        }

        for (int i = 0; i < devices.Length; i++)
        {
            if (!devices[i].isFrontFacing)
            {

                backCam = new WebCamTexture(devices[i].name, Screen.width, Screen.width, Screen.height);
            }
        }

        if (backCam == null)
        {
            Debug.Log("Unable to find back camera");
            return;
        }

        /*<-적어도 하나의 Back Camera가 있음을 의미*/


        backCam.Play();
        background.texture = backCam;


        camAvailable = true;
    }

    private void Update()
    {
        if (!camAvailable)
            return;
        float ratio = (float)backCam.width / (float)backCam.height;
        Fit.aspectRatio = ratio;

        //카메라 반전 해결
        float scaleY = backCam.videoVerticallyMirrored ? -1f : 1f;
        background.rectTransform.localScale = new Vector3(1f, scaleY, 1f);

        
        int orient = -backCam.videoRotationAngle;
        background.rectTransform.localEulerAngles = new Vector3(0, 0, orient);
    }
}
