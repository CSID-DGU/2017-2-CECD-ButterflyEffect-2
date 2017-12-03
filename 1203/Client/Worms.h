
#ifndef __WORMS_INCLUDED__
#define __WORMS_INCLUDED__

#include <iostream>
#include <string>
#include <vector>
#include "Point.h"

using namespace std;

class Worms{
public:
	Worms(int color=255);
	~Worms();
	void move();
	void setTheta(float theta);
	int getColor();
	int getHeadColor();
	vector<Pt> getBody();
private:
	float theta; //지렁이 현재 각도
	int headColor;
	int color; //지렁이 색깔
	int velocity; //지렁이 속도
	vector<Pt> bodys;

};


#endif
