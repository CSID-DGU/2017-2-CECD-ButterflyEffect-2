#include <opencv2/highgui/highgui.hpp>
#include <opencv2/opencv.hpp> 
#include <iostream>
#include <cstdio>
#include <pthread.h>
#include "getch.h"
#define LEFT 106
#define RIGHT 108
#define UP 105
#define DOWN 107
using namespace std;
using namespace cv;
int x, y;
float theta ;
void *moveCircle(void *ptr){
	while(1){
		int key;
		key = getch();
		cout<<"KEY: "<<key<<endl;
		switch(key)
		{

		case UP:
			theta -= 50;
			break;
		case DOWN:
			theta += 50;
			break;
		}
	}
}

int main(){
	int d =3; // how long do worm move for one clock.
	x=250;
	y=250;
	theta = 45;
	pthread_t client_thread;
	pthread_create(&client_thread, NULL, &moveCircle, NULL);
	while(1){

		Mat image(1024, 1024, CV_8UC3, Scalar(255, 255, 255));
		x += d*cos(theta/180*3.14);
		y -= d*sin(theta/180*3.14);
		//cout<< "X:"<<d*cos(theta/180*3.14)<<"/Y:"<<d*sin(theta/180*3.14)<<endl;
	    circle(image, Point(x, y), 30, Scalar(255, 0, 0), -1);
	    imshow("circle", image);
	    if(waitKey(10)==27)
			break;
	}
	return 0;
}
