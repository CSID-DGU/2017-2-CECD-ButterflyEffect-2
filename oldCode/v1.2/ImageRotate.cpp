#include <opencv2/opencv.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/core.hpp>
#include <utility>
using namespace cv;
using namespace std;
Mat ImageRotateInner(const Mat src, double degree, Point2d base){
	Mat dst = src.clone();
	Mat rot = getRotationMatrix2D(base, degree, 0.3);
	warpAffine(src, dst, rot, src.size());	
	return move(dst);
}

int main() {
	double angle = 60;
	Mat background = imread("images/background.jpg");
	Mat worm = imread("images/worm.png", -1);

	Point2d mid(worm.cols / 2.0, worm.rows / 2.0);
	Point pt(100, 100);	

	Mat rotate = ImageRotateInner(worm, angle, mid);
	
	imshow("worm", rotate);
	waitKey();
	destroyAllWindows();
	return 0;
}
