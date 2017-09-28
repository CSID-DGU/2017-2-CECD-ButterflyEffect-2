#include "opencv2/core/core.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/opencv.hpp"

using namespace cv;
using namespace std;
typedef struct Pos {
	int x1, y1; //좌측 상단
	int x2, y2; //우측 하단
}Pos;

Pos p;
Pos getPoint() {	
	return p;
}

void setPoint(int x1, int y1, int x2, int y2) {
	p.x1 = x1;
	p.y1 = y1;
	p.x2 = x2;
	p.y2 = y2;
}

int main() {
	Mat drawing;
	Pos pos;
	drawing = Mat::zeros(500, 500, CV_8UC3);
	setPoint(200, 50, 300, 150);
	pos = getPoint();
	rectangle(drawing, Point(pos.x1, pos.y1), Point(pos.x2, pos.y2), Scalar(200, 0, 0), 2, 3);
	setPoint(250, 100, 350, 200);
	pos = getPoint();
	rectangle(drawing, Point(pos.x1, pos.y1), Point(pos.x2, pos.y2), Scalar(0, 0, 200), 2, 3);
	imshow("image", drawing);
	waitKey(0);

	return 0;
}
