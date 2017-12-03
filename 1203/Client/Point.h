#ifndef __POINT_INCLUDED__
#define __POINT_INCLUDED__

class Pt{

public:
Pt(){}
Pt(float x,float y){
	this->x = x;
	this->y = y;
}
float x,y;
};

struct UserPoint{
        Pt body;
        Pt rightHand;
};


#endif
