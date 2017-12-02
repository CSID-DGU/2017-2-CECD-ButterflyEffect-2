#include <iostream>
#include <vector>
#include <stdio.h>
#include <algorithm>
#include <iterator>
#include "opencv2/opencv.hpp"

using namespace std;
using namespace cv;

int main(){
	Mat img = imread("images/background.jpg", 1);
	if(img.empty()){
		cout << "Can't read image." << endl;
		return 0;
	}
	Mat overlay = imread("images/overlay.png", -1);

	if(overlay.empty()){
		cout << "Can't read image." << endl;
		return 0;
	}

	Rect target_roi(0, 0, img.cols/2, img.rows/2);
	cv::resize(overlay, overlay, Size(target_roi.width/2, target_roi.height/2));

	Mat mask;
	if(overlay.channels() == 4){
		vector<Mat> ch;
		split(overlay, ch);
		mask = 255 - ch[3].clone();
		mask.convertTo(mask, CV_32FC1, 1.0 / 255.0);
		ch.erase(ch.begin() + 3);
		merge(ch, overlay);
	}else{
		if(overlay.channels() == 3){
			cvtColor(overlay, overlay, COLOR_BGR2GRAY);
		}
		overlay.convertTo(mask, CV_32FC1, 1.0 / 255.0);
	}

	for(int i = 0; i < overlay.rows; i++){
		for(int j = 0; j < overlay.cols; j++){
			float blending_coeff = mask.at<float>(i ,j);
			Vec3b v1 = img.at<Vec3b>(i + target_roi.y, j + target_roi.x);
			Vec3b v2;
			if(overlay.channels() == 1){
				int v = overlay.at<uchar>(i, j);
				v2 = (v, v, v);
			}else{
				v2 = overlay.at<Vec3b>(i, j);
			}

			Vec3f v1f(v1[0], v1[1], v1[2]);
			Vec3f v2f(v2[0], v2[1], v2[2]);
			
			Vec3f r = v1f * blending_coeff + (1.0 - blending_coeff) * v2f;
			img.at<Vec3b>(i + target_roi.y, j + target_roi.x) = r;

		}
	}

	imshow("mask", img);
	imwrite("result.png", img);
	waitKey();
}
