#include <opencv2/highgui.hpp>
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

void *moveCircle(void *ptr){
	while(1){
		int key;
		key = getch();
		switch(key){
		case LEFT:
			x -= 5;
			break;
		case RIGHT:
			x += 5;
			break;
		case UP:
			y -= 5;
			break;
		case DOWN:
			y += 5;
			break;
		}
	}
}

int main(){
	pthread_t client_thread;
	pthread_create(&client_thread, NULL, &moveCircle, NULL);
	while(1){
		Mat image(512, 512, CV_8UC3, Scalar(255, 255, 255));
	    circle(image, Point(250+x, 250+y), 30, Scalar(255, 0, 0), -1);
	    imshow("circle", image);
	    if(waitKey(10)==27)
			break;
	}
	return 0;
}
