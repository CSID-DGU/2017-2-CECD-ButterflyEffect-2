#include "opencv2/opencv.hpp"
#include "config.h"
#include "PracticalSocket.h"      // For UDPSocket and SocketException
#include <iostream>               // For cout and cerr
#include <cstdlib>                // For atoi()
#include <cstring>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <ctime>
#include <unistd.h>
#include <pthread.h>
#include <sstream>
#include <vector>
#include "Point.h"
#include "Worms.h"
using namespace std;
using namespace cv;

#define PORT_NUM "9000"
//#define ADDR "127.0.0.1"
#define ADDR "13.124.165.41"
#define BUFFER_SIZE 2000
#define PI 3.141592
#define OBJECT_SIZE 30
#define FOOD_SIZE 15

vector<UserPoint> keyPoints;
vector<Pt> foods;
pthread_mutex_t count_mutex;
int sock;
int point[4];
int cur_X, cur_Y;
Worms worms;
float theta = 1;
int key_Idx = 0;

void makeFood(){ 
            Pt food(rand()%FRAME_WIDTH, rand()%FRAME_HEIGHT);
            foods.push_back(food);
}
//이미지 회전 함수
Mat ImageRotate(Mat src, double degree, Point2d base){
    Mat dst = src.clone();
    Mat rot = getRotationMatrix2D(base, degree, 1);
    warpAffine(src, dst, rot, src.size());
    return move(dst);
}

//이미지 그리기 함수
void DrawImage(Mat src, Mat frame, Point p, double degree, double size){
    Mat dst;
    Mat mask;
    //이미지 각도에 맞게 회전
    Point2d mid(src.cols/2, src.rows/2);
    dst = ImageRotate(src, degree, mid);

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
            Vec3b v1 = frame.at<Vec3b>(i + target_roi.y, j + target_roi.x);
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
            frame.at<Vec3b>(i + target_roi.y, j + target_roi.x) = r;
        }
    }
}

//drawing worms on the frame
void drawing(Mat frame, Mat image, Mat body){
    vector<Pt> bodies = worms.getBody();

    //drawing worms
    for(int i = bodies.size() - 1;i >= 0; i--){
        if(i == 0){ 
            //circle(frame, Point(bodies[i].x, bodies[i].y), OBJECT_SIZE, Scalar(255, 255, 255), -1);
            DrawImage(image, frame, Point(bodies[i].x-(image.cols/4), bodies[i].y-(image.rows/4)), (double)worms.getTheta(), 1.5);
        }
        else if(i%2 == 0){
			DrawImage(body, frame, Point(bodies[i].x - (body.cols/4), bodies[i].y - (body.rows/4)), (double)worms.getTheta(), 1.5);
        }
    }       

    //drawing foods
    for(int i=0;i<foods.size();i++){
        circle(frame, Point(foods[i].x,foods[i].y),FOOD_SIZE,Scalar(rand()%255,rand()%255,rand()%255),-1);
    }

	//drawing body and hand
    circle(frame, Point(keyPoints[0].body.x, keyPoints[0].body.y), 30, Scalar(0, 0, 255), -1);
    circle(frame, Point(keyPoints[0].body.x, keyPoints[0].body.y), 25, Scalar(255, 255, 255), -1);
    circle(frame, Point(keyPoints[0].rightHand.x, keyPoints[0].rightHand.y), 30, Scalar(0, 0, 255), -1);
    circle(frame, Point(keyPoints[0].rightHand.x, keyPoints[0].rightHand.y), 25, Scalar(255, 255, 255), -1);
}

bool isCrashed(Pt p1, Pt p2){
    int dx = p1.x - p2.x;
    int dy = p1.y - p2.y;
    int C = 10 ; //나중에 config 에서 선언
    if(dx*dx+dy*dy < C*C){
        return true;
    }
    else{
        return false;
    }
}

void crashHandler(){
    //먹이 충돌 처리
    Pt wormsHead = worms.getBody().at(0);
    for(int i=0;i<foods.size();i++){
        if(isCrashed(foods[i],wormsHead)){
            //만약 먹이를 먹으면 해당 지렁이의 몸을 늘어나게 하고,
            //먹이를 제거하기, 
            //** 추후에 먹이를 새로 만들어주는 것도 만들어보기
            worms.increaseBody();
            foods.erase(foods.begin()+i);
            if(foods.size()==0)
                makeFood();
            
        }
    }
}

float getDirectDegree(Pt first, Pt second){
    float degree, dx, dy , radian;
    dx = first.x - second.x;
    dy = first.y - second.y;

    cout << "first:(" << first.x << "," << first.y << ")" << endl;
    cout << "second:(" << second.x << "," << second.y << ")" << endl;
    radian = atan2(dy, dx);
    degree = (radian*180)/PI;

    return degree;
}

void *DataHandler(void *ptr){
    while(true){
        char server_reply[BUFFER_SIZE];
        memset(server_reply, '\0', sizeof(server_reply));
        int byte_Num;
        if(byte_Num = recv(sock, server_reply, BUFFER_SIZE, 0) < 0){
            cout << "receive error!!" << endl;
        }
        //parsing
        cout <<"data :" << server_reply << endl;
        string str(server_reply);
        string token;
        stringstream stream(str);
        int i = 0;
        stream >> token;
        int size = std::stoi(token);
        cout<<"size:"<<keyPoints.size()<<endl;
        for(i=0; i < size; i++){
            UserPoint userPoint;
            stream >> token;
            userPoint.body.x = std::stoi(token);
            stream >> token;
            userPoint.body.y = std::stoi(token);
            stream >> token;
            userPoint.rightHand.x = std::stoi(token);
            stream >> token;
            userPoint.rightHand.y = std::stoi(token);
            keyPoints[i] = userPoint;
        }
    }
}

void init(){
    cur_X = 100;
    cur_Y = 100;
    theta = 0;
    UserPoint initPoint;
    initPoint.body.x = 0; initPoint.body.y = 0;
    initPoint.rightHand.x = 0; initPoint.rightHand.y = 0;
    keyPoints.push_back(initPoint);
    srand(time(NULL));
    for(int i=0;i<10;i++)
        makeFood();
    
}

int main() {
    init();
    int d = 1;
    string servAddress = ADDR; // First arg: server address
    unsigned short servPort = Socket::resolveService(PORT_NUM, "udp");
    try {
        /*tcp 연결*/
        sockaddr_in server;
        pthread_t client_thread;

        //소켓 생성
        sock = socket(AF_INET, SOCK_STREAM, 0);
        if(sock == -1){
            cout << "소켓 생성에 실패하였습니다." << endl;
            exit(1);
        }
        cout << "TCP소켓을 생성하였습니다." << endl;

        //소켓 준비
        server.sin_family = AF_INET;
        server.sin_addr.s_addr = inet_addr(ADDR);
        server.sin_port = htons(stoi(PORT_NUM));

        //커넥트
        if(connect(sock, (struct sockaddr*)&server, sizeof(server)) < 0){
            cout<< "커넥트 에러" << endl;
            exit(1);
        }
        cout << "연결되었습니다." << endl;
        pthread_create(&client_thread, NULL, &DataHandler, NULL);

        //udp
        UDPSocket sock;
        int jpegqual =  ENCODE_QUALITY; // Compression Parameter

        Mat frame, send;
        vector < uchar > encoded;
        VideoCapture cap(0); // Grab the camera
        namedWindow("send", CV_WINDOW_AUTOSIZE);
        if (!cap.isOpened()) {
            cerr << "OpenCV Failed to open camera";
            exit(1);
        }
        clock_t last_cycle = clock();

        Mat image = imread("images/head.png", -1);
        Mat body = imread("images/body.png", -1);
        if(image.empty()){
            cout << "이미지 불러오기 실패" << endl;
            return 0;
        }
        while (1) {
            cap >> frame;
            if(frame.size().width==0)continue;//simple integrity check; skip erroneous data...
            resize(frame, send, Size(FRAME_WIDTH, FRAME_HEIGHT), 0, 0, INTER_LINEAR);
            vector < int > compression_params;
            compression_params.push_back(CV_IMWRITE_JPEG_QUALITY);
            compression_params.push_back(jpegqual);
            flip(send, send, 1);
            imencode(".jpg", send, encoded, compression_params);

            //drawing worms
			float userDegree = getDirectDegree(keyPoints[0].rightHand, keyPoints[0].body);
			
			if(15 <= userDegree && userDegree <= 90){
				worms.setTheta(worms.getTheta() + THETA_C);
			}else if(-90 <= userDegree && userDegree < -15){
				worms.setTheta(worms.getTheta() - THETA_C);
			}
			string myText = to_string(worms.getTheta());
			//Text Location
			cv::Point myPoint;
			myPoint.x = 800;
			myPoint.y = 100;		 
			// Font Face
			int myFontFace = 5;
			// Font Scale
			double myFontScale = 2;
			cout << "UserDegree >> " << worms.getTheta() << endl;
            //worms.setTheta(getDirectDegree(keyPoints[0].rightHand,keyPoints[0].body));
            worms.move();
            drawing(send, image, body);
			cv::putText( send, myText, myPoint, myFontFace, myFontScale, Scalar(0,255,0) );
            crashHandler();

            imshow("send", send);
           
            int total_pack = 1 + (encoded.size() - 1) / PACK_SIZE;

            int ibuf[1];
            ibuf[0] = total_pack;
            sock.sendTo(ibuf, sizeof(int), servAddress, servPort);

            for (int i = 0; i < total_pack; i++)
                sock.sendTo( & encoded[i * PACK_SIZE], PACK_SIZE, servAddress, servPort);

            waitKey(FRAME_INTERVAL);

            clock_t next_cycle = clock();
            double duration = (next_cycle - last_cycle) / (double) CLOCKS_PER_SEC;
            //cout << "\teffective FPS:" << (1 / duration) << " \tkbps:" << (PACK_SIZE * total_pack / duration / 1024 * 8) << endl;

            last_cycle = next_cycle;
        }
    }catch (SocketException & e) {
        cerr << e.what() << endl;
        exit(1);
    }
    return 0;
}
