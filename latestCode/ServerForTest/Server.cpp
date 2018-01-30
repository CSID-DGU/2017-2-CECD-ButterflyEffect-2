#include "PracticalSocket.h" // For UDPSocket and SocketException
#include <iostream>          // For cout and cerr
#include <cstdlib>           // For atoi()
#include <vector>
#include <queue>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sstream>
#include <cstring>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <opencv/cv.h>
#include <pthread.h>
#include "opencv2/opencv.hpp"
#include "config.h"
#define BUF_LEN 65540 // Larger than maximum UDP packet size
using namespace cv;
using namespace std;

int tcpsocket;
queue<Mat> frameQueue;
vector<Rect> getFaces(Mat frame) 
{
	vector<Rect> faces;
	CascadeClassifier cascade;
	if (cascade.load("haarcascade_frontalface_alt.xml")) 
    {
		cascade.detectMultiScale(frame, faces, 1.1, 3, CV_HAAR_FIND_BIGGEST_OBJECT, Size(30, 30), Size(200, 200));
	}
	else 
    {
        cout<<"no file"<<endl;
	}
	return faces;
}

void* transfer(void*)
{
    while(1)
    {   
        if(!frameQueue.empty())
        {
            //face detect
   			Mat frame =frameQueue.front();
   			vector<Rect> faces = getFaces(frame);
            int input =0;
            
            if(faces.size()!=0)
            {
                string st= " ";
                Rect r =  faces.at(0);
                stringstream ss;
                cout << r.x << ", " << r.y << endl;
                ss << r.x <<st << r.y << st << (r.x+r.width) << st << (r.y+r.height) << "\r\n";
                st = ss.str();
                if( send(tcpsocket , st.c_str()  , st.size() , 0) < 0)
                {
                    cout << "Send failed : " << st << endl;
                }
            }
			frameQueue.pop();
          }
    }
}


int main() 
{
    int PORT_NUM;
    int MODE;
    cout << "연결할 포트 번호를 입력하세요>> ";
    cin >> PORT_NUM;
    cout << "MODE를 선택하세요(0: Only UDP, 1: TCP/UDP)>> ";
    cin >> MODE;
    cout << "현재 포트: " << PORT_NUM << ", 모드: ";
    if(MODE)
    {
        cout << "TCP/UDP" << endl;
    }else
    {
        cout << "Only UDP" << endl;
    }
    int FRAME= 0;
    namedWindow("recv", CV_WINDOW_AUTOSIZE);
    try 
    {
        if(MODE)
        {
            pthread_t serverThread;
            string th_str;
            int sockfd=socket(AF_INET,SOCK_STREAM,0);
            struct sockaddr_in serverAddress;
            struct sockaddr_in clientAddress;
            memset(&serverAddress,0,sizeof(serverAddress));
            serverAddress.sin_family=AF_INET;
            serverAddress.sin_addr.s_addr=htonl(INADDR_ANY);
            serverAddress.sin_port=htons(PORT_NUM);
            bind(sockfd,(struct sockaddr *)&serverAddress, sizeof(serverAddress));
            cout << "TCP 소켓이 생성되었습니다" << endl;
            listen(sockfd,5);
            socklen_t sosize  = sizeof(clientAddress);
            tcpsocket = accept(sockfd,(struct sockaddr*)&clientAddress,&sosize);
            th_str = inet_ntoa(clientAddress.sin_addr);
            pthread_create(&serverThread,NULL,&transfer,NULL);
        }
        UDPSocket sock(PORT_NUM);
        cout << "UDP 소켓이 생성되었습니다" << endl;
        char buffer[BUF_LEN]; // Buffer for echo string
        int recvMsgSize; // Size of received message
        string sourceAddress; // Address of datagram source
        unsigned short sourcePort; // Port of datagram source
        clock_t last_cycle = clock();
        while (true) 
        {
            recvMsgSize = sock.recvFrom(buffer, BUF_LEN, sourceAddress, sourcePort);
        	char * longbuf = new char[recvMsgSize];
        	memcpy( & longbuf[0], buffer, recvMsgSize);

            //cout << "주소: " << sourceAddress << ", 포트: " << sourcePort << "로 부터 패킷을 수신하였습니다." << endl; 
            //Mat rawData = Mat(1, PACK_SIZE * total_pack, CV_8UC1, longbuf);
            Mat rawData = Mat(1,recvMsgSize, CV_8UC1, longbuf);
            Mat frame = imdecode(rawData, CV_LOAD_IMAGE_COLOR);
            if (frame.size().width == 0) 
            {
                cerr << "decode failure!" << endl;
                continue;
            }
            if(frameQueue.size()<50 && FRAME%4!=0)
            {
                frameQueue.push(frame);

            }
            FRAME++;
            imshow("recv", frame);
            free(longbuf);
            waitKey(1);
            clock_t next_cycle = clock();
            double duration = (next_cycle - last_cycle) / (double) CLOCKS_PER_SEC;
            //cout << "\teffective FPS:" << (1 / duration) << " \tkbps:" << (PACK_SIZE * total_pack / duration / 1024 * 8) << endl;
            //cout << next_cycle - last_cycle;
            last_cycle = next_cycle;
        }
    } catch (SocketException & e) 
    {
        cerr << e.what() << endl;
        exit(1);
    }

    return 0;
}
