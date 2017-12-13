#include <opencv2/highgui/highgui.hpp>
#include <opencv2/opencv.hpp> 
#include <iostream>
#include <cstdio>
#include <pthread.h>
#include <cstdlib>
#include <time.h>
#include <unistd.h>
#define FRAME_SIZE 1024
#define OBJECT_SIZE 30
using namespace std;
using namespace cv;
int x, y;
float theta;

float getDegree(){
	return rand() % 360 + 1;
}
void *moveCircle(void *ptr){
	while(1){
		theta = getDegree();
	}
}

void init(){
	theta = 45;
	x = 250;
	y = 250;
}

bool isOkay(int x, int y){
	if(x - OBJECT_SIZE < 0 || y - OBJECT_SIZE < 0 || x + OBJECT_SIZE > FRAME_SIZE || y + OBJECT_SIZE > FRAME_SIZE)
		return false;
	else
		return true;
}

int main(){
	int d =3; // how long do worm move for one clock.
	init();
	srand((unsigned int)time(NULL));
	pthread_t client_thread;
	pthread_create(&client_thread, NULL, &moveCircle, NULL);
	int pre_X, pre_Y, new_X, new_Y;
	while(1){
		Mat image(FRAME_SIZE, FRAME_SIZE, CV_8UC3, Scalar(255, 255, 255));
		x += d*cos(theta/180*3.14);
		y -= d*sin(theta/180*3.14);
		if(isOkay(x, y)){
			new_X = x;
			new_Y = y;
			pre_X = new_X;
			pre_Y = new_Y;
		}
		else{
			new_X = pre_X;
			new_Y = pre_Y;
		}
	   	circle(image, Point(new_X, new_Y), OBJECT_SIZE, Scalar(255, 0, 0), -1);
	    imshow("circle", image);
	    if(waitKey(10)==27)
			break;
	}
	return 0;
}
