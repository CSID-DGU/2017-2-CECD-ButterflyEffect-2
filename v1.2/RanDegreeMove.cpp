#include <iostream>
#include <cstdio>
#include <cstdlib>
#include <time.h>
#include <cmath>
#include "getch.h"
#define PI 3.14159265
using namespace std;

typedef struct Pt{
	float x, y;
}Pt;

float getRandom(){
	float random = rand() % 1000 + 1;
	return random/3.0f;
}

float getDegree(Pt first, Pt second){
	float degree, dx, dy , radian;
	dx = first.x - second.x;
	dy = first.y - second.y;
	radian = atan2(dy, dx);
	degree = (radian*180)/PI;
	return degree;
}

string getDirection(float degree){
	if(0.0 <= degree <= 90.0)
		return "LEFT_UP";
	else if(90.0 < degree <= 180.0)
		return "RIGHT_UP";
	else if(-90.0 <= degree < 0.0)
		return "LEFT_DOWN";
	else if(-180.0 <= degree < -90.0)
		return "RIGHT_DOWN";
}

void *moveCircle(void *ptr){
	Pt body, hand;
	body.x = 155.0; body.y = 155.0;
	while(1){
		hand.x = getRandom(); hand.y = getRandom();
		float degree = getDegree(body, hand);
		cout << "hand x = " << hand.x << ", hand y = "<< hand.y << endl;
		cout << "degree is " << degree << endl;
	}
}

int main(){
	cout.setf(ios::fixed);
	cout.precision(2);
	srand(unsigned(time(NULL)));
	return 0;
}
