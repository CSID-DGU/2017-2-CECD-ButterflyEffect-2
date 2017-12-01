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
using namespace std;
using namespace cv;

#define PORT_NUM "9000"
//#define ADDR "127.0.0.1"
#define ADDR "13.124.244.68"
#define BUFFER_SIZE 2000
#define PI 3.141592
#define OBJECT_SIZE 30

struct Pt{
    float x, y;
};

struct UserPoint{
	struct Pt body;
	struct Pt rightHand;
};

vector<UserPoint> keyPoints;
pthread_mutex_t count_mutex;
int sock;
int point[4];
int cur_X, cur_Y;
float theta;
int key_Idx = 0;
//사용자의 오른쪽 손과 몸통 좌표의 차를 이용하여 각도를 구한다.
float getDegree(int idx){
	float degree, dx, dy, radian;
	if(!keyPoints[idx].righthand.x && !keyPoints[idx].righthand.y && !keyPoints[idx].body.x && !keyPoints[idx].body.y){
		key_Idx--;
		return theta;
	}else{
		dx = keyPoints[idx].righthand.x - keyPoints[idx].body.x;
		dy = keyPoints[idx].righthand.y - keyPoints[idx].body.y;
		radian = atan(dy/dx);
		degree = (radian*180)/PI;
		return degree;
	}
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
            keyPoints.push_back(userPoint);
        }
		/*
        for(i=0; i<size; i++){
            cout << "keyPoints:" << (i+1) << endl;
            cout << "body:" << keyPoints[i].body.x <<"," << keyPoints[i].body.y<<endl;
            cout << "rightHand:" << keyPoints[i].rightHand.x << "," << keyPoints[i].rightHand.y<<endl;
        }*/

		//theta = getDegree(key_Idx++); 오픈포즈 속도 문제가 해결되면 이 코드를 사용하면 객체가 이동함.
		theta = 30;
    }
}

void init(){
	cur_X = 100;
	cur_Y = 100;
	theta = 0;
}

int main() {
	init();
	int d = 3;
    string servAddress = ADDR; // First arg: server address
    unsigned short servPort = Socket::resolveService(PORT_NUM, "udp");
    try {
		/*tcp 연결*/
		/*thread 생성부분 ... thread에서는 recv하여 전역변수 수정*/
        //tcp
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
        while (1) {
            cap >> frame;
            if(frame.size().width==0)continue;//simple integrity check; skip erroneous data...
            resize(frame, send, Size(FRAME_WIDTH, FRAME_HEIGHT), 0, 0, INTER_LINEAR);
            vector < int > compression_params;
            compression_params.push_back(CV_IMWRITE_JPEG_QUALITY);
            compression_params.push_back(jpegqual);
            imencode(".jpg", send, encoded, compression_params);

			cur_X += d*cos(theta/180*PI);
			cur_Y -= d*sin(theta/180*PI);
			circle(send, Point(cur_X, cur_Y), OBJECT_SIZE, Scalar(255, 0, 0), -1);
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
			//Destructor closes the socket

    }catch (SocketException & e) {
        cerr << e.what() << endl;
        exit(1);
    }
    return 0;
}
