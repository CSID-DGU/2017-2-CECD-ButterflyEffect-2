#include "Worms.h"
#include "config.h"
#include <cmath>

Worms::Worms(int color){
	this->headColor = 0;
	this->color = color;
	this->velocity = 5;
	for(int i=0;i<2;i++){
		Pt body(START_X,START_Y);	
		bodys.push_back(body);
	}
}

Worms::~Worms(){
}

void Worms::move(){
	
	vector<Pt> temp = bodys;

	if(0 + WORMS_HEAD <bodys[0].x && bodys[0].x<FRAME_WIDTH - WORMS_HEAD)
		bodys[0].x += -((velocity) * cos(theta / 180 * 3.14));
	else
	{
		bodys[0].x = START_X;
		bodys[0].y = START_Y;
	}

	if(0 + WORMS_HEAD < bodys[0].y && bodys[0].y<FRAME_HEIGHT - WORMS_HEAD)
		bodys[0].y += (velocity) * sin(theta / 180 * 3.14);
	
	else{
		bodys[0].x = START_X;
		bodys[0].y = START_Y;
	}
	for(int i=1;i<bodys.size();i++){
		bodys[i].x = temp[i-1].x;
		bodys[i].y = temp[i-1].y;
	}
}

void Worms::setTheta(float theta){
	this->theta = theta;
}

vector<Pt> Worms::getBody(){
	return this->bodys;
}

int Worms::getColor(){
	return this->color;
}

int Worms::getHeadColor(){
	return this->headColor;
}
float Worms::getTheta(){
	return this->theta;
}
void Worms::increaseBody(){
	for(int i=0;i<10;i++){
		Pt body(bodys[0].x,bodys[0].y);	
		bodys.push_back(body);
	}
}
