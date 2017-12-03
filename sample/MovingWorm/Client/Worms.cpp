#include "Worms.h"
#include <cmath>

Worms::Worms(int color){
	this->headColor = 0;
	this->color = color;
	this->velocity = 1;
	for(int i=0;i<100;i++){
		Pt body(100,100);	
		bodys.push_back(body);
	}
}

Worms::~Worms(){
}

void Worms::move(){
	
	vector<Pt> temp = bodys;

	bodys[0].x += (velocity) * cos(theta / 180 * 3.14);
	bodys[0].y += (velocity) * sin(theta / 180 * 3.14);
	
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
