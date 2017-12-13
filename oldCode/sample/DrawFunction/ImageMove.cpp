#include <opencv2/opencv.hpp>
#include <iostream>
#include <pthread.h>
#include "getch.h"
#define UP 105
#define DOWN 107
using namespace std;
using namespace cv;

double theta;

Mat ImageRotate(Mat src, double degree, Point2d base);
void DrawImage(Mat src, Mat frame, Point p, double degree, double size);

Mat background;

int main(){
	background = imread("images/background.jpg");
	if(background.empty()){
		cout << "배경 이미지 불러오기 실패." << endl;
		return 0;
	}
	Mat image = imread("images/head.png", -1);
	if(image.empty()){
		cout << "이미지 불러오기 실패." << endl;
		return 0;
	}
	theta = 30;
	circle(background, Point(100, 100), 30, Scalar(255, 0, 0), -1);
	DrawImage(image, background, Point(0, 0), 1.0, 1.5);
	imshow("mask", background);
	//waitKey("result.png", background);
	waitKey();
	return 0;
}

void DrawImage(Mat src, Mat frame, Point p, double degree, double size){
	Mat dst;
	Mat mask;
	if(degree != 0){
		//이미지 각도에 맞게 회전
		//cv::resize(src, src, Size(src.cols/size, src.rows/size));
		imwrite("resize.png",src);
		Point2d mid(src.cols/size, src.rows/size);
		dst = ImageRotate(src, degree, mid);
	}
	
	//프레임에 이미지 붙이기
	Rect target_roi(p.x, p.y, dst.cols/size, dst.rows/size);
	cv::resize(dst, dst, Size(target_roi.width, target_roi.height));

	if(dst.channels() == 4){
		vector<Mat> ch;
		split(dst, ch);
		mask = 255 - ch[3].clone();
		mask.convertTo(mask, CV_32FC1, 1.0/255.0);
		ch.erase(ch.begin()+3);
		merge(ch, dst);
	}else{
		if(dst.channels() == 3){
			cvtColor(dst, dst, COLOR_BGR2GRAY);
		}
		dst.convertTo(mask, CV_32FC1, 1.0/255.0);
	}
	
	for(int i = 0; i < dst.rows; i++){
		for(int j = 0; j < dst.cols; j++){
			float blending_coeff = mask.at<float>(i, j);
			Vec3b v1 = background.at<Vec3b>(i + target_roi.y, j + target_roi.x);
			Vec3b v2;
			if(dst.channels() == 1){
				int v = dst.at<uchar>(i, j);
				v2 = (v, v, v);
			}else{
				v2 = dst.at<Vec3b>(i, j);
			}
			Vec3f v1f(v1[0], v1[1], v1[2]);
			Vec3f v2f(v2[0], v2[1], v2[2]);

			Vec3f r = v1f * blending_coeff + (1.0 - blending_coeff) * v2f;
			background.at<Vec3b>(i + target_roi.y, j + target_roi.x) = r;
		}
	}
	
}

Mat ImageRotate(Mat src, double degree, Point2d base){
	Mat dst = src.clone();
	Mat rot = getRotationMatrix2D(base, degree, 1);
	warpAffine(src, dst, rot, src.size());
	return move(dst);
}
