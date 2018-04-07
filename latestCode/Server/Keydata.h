#ifndef __KEYDATA_INCLUDED__
#define __KEYDATA_INCLUDED__

#include <string>
#include <cstdio>
#include <cstring>
#include <iostream>
#include <sstream>
#include <cstdlib>
#include "json11/json11.hpp"
#include <vector>
#include <iomanip>
using namespace std;
using namespace json11;


/**
  * Pt class
  */
class Pt{
public:
        float x,y;
	int r,g,b;	
	//construct
        Pt(float x, float y) : x(x), y(y){
		r = -1;
		g = -1;
		b = -1;
	}
       
	//method for json : {{"x": x},{"y": y}}
	Json to_json() const{
        std::stringstream ssX,ssY;
        ssX<< std::fixed <<std::setprecision(3)<<x;
        ssY<< std::fixed <<std::setprecision(3)<<y;
        return
         Json::object({
                {"x",ssX.str()},
                {"y",ssY.str()},
		{"r",r},
		{"g",g},
		{"b",b},	});
        }
	void setColor(int r, int g,int b){
		this->r = r;
		this->g = g;
		this->b = b;
	}
};

/**
  * UserKeyPoint class
  */
class UserKeyPoint{
public:
        vector<Pt> keyPoints;
	int rgbRed,rgbGreen,rgbBlue;	
	//construct : init all color value.
        UserKeyPoint(){
		rgbRed = -1;
		rgbGreen = -1;
		rgbBlue = -1;
        }

	//method for json : [{"rgbBlue": rgbBlue, "rgbGreen": rgbGreen, "rgbRed": rgbRed, "skeleton": [...]}]  
        Json to_json() const{
                return Json::object({
                {"skeleton",Json(keyPoints)},
                {"rgbRed",rgbRed},
                {"rgbGreen",rgbGreen},
                {"rgbBlue",rgbBlue},
        });
        }
        void addPoint(Pt pt){
                keyPoints.push_back(pt);
        }
        void setColor(int r, int g, int b){
                rgbRed = r;
                rgbGreen = g;
                rgbBlue = b;
        }
};

#endif
