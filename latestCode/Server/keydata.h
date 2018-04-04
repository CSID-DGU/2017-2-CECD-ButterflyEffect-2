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
	
	//construct
        Pt(float x, float y) : x(x), y(y){}
       
	//method for json : {{"x": x},{"y": y}}
	Json to_json() const{
        std::stringstream ssX,ssY;
        ssX<< std::fixed <<std::setprecision(3)<<x;
        ssY<< std::fixed <<std::setprecision(3)<<y;
        return
         Json::object({
                {"x",ssX.str()},
                {"y",ssY.str()},});
        }
};

/**
  * UserKeyPoint class
  */
class UserKeyPoint{
public:
        int rgbRed;
        int rgbGreen;
        int rgbBlue;
        vector<Pt> keyPoints;

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
