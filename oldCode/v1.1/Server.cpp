/*
 *   C++ UDP socket server for live image upstreaming
 *   Modified from http://cs.ecs.baylor.edu/~donahoo/practical/CSockets/practical/UDPEchoServer.cpp
 *   Copyright (C) 2015
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#include "PracticalSocket.h" // For UDPSocket and SocketException
#include <iostream>          // For cout and cerr
#include <cstdlib>           // For atoi()
#include <vector>
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

#define BUF_LEN 65540 // Larger than maximum UDP packet size

#include "opencv2/opencv.hpp"
using namespace cv;
#include "config.h"

#define PORT_NUM 9000


int tcpsocket;

std::vector<Rect> getFaces(Mat frame) {

	std::vector<Rect> faces;

	CascadeClassifier cascade;

	if (cascade.load("haarcascade_frontalface_alt.xml")) {
		cascade.detectMultiScale(frame, faces, 1.1, 3, CV_HAAR_FIND_BIGGEST_OBJECT, Size(30, 30), Size(200, 200));
		//printf("%zd face(s) are found.\n", faces.size());
	}

	else {
    cout<<"no file"<<endl;
	}

	return faces;
}

std::queue<Mat> frameQueue;

void* transfer(void*)
{

    cout<<"1"<<endl;

    cout<<"1.5"<<endl;
    while(1)
    {
        if(!frameQueue.empty())
        {
            //face detect
   			Mat frame =frameQueue.front();

   			std::vector<Rect> faces = getFaces(frame);


                    //here ...transfer

                  //rectangle(frame, Point(faces[i].x,faces[i].y),
                    //	Point(faces[i].x+faces[i].width,faces[i].y+faces[i].height),
                        //Scalar(0, 0, 255));
            int input =0;
            if(faces.size()!=0){
                //Rect_<int> r = faces[0].x;

                string st= " ";
                Rect r =  faces.at(0);
                stringstream ss;
                cout<<r.x<<endl;

                ss << r.x <<st << r.y<<st<< (r.x+r.width)<<st<<(r.y+r.height);
                st = ss.str();
                if( send(tcpsocket , st.c_str()  , st.size() , 0) < 0)
                {
                    cout << "Send failed : " << st << endl;
               //     return false;
                }


            }



                    //here ...transfer

                  //rectangle(frame, Point(faces[i].x,faces[i].y),
                    //	Point(faces[i].x+faces[i].width,faces[i].y+faces[i].height),
                        //Scalar(0, 0, 255));
			frameQueue.pop();
          }

    }
}



int main() {
    //unsigned short servPort = atoi(argv[1]); // First arg:  local port
    int FRAME= 0;
    namedWindow("recv", CV_WINDOW_AUTOSIZE);
    try {

        pthread_t serverThread;
        int sockfd=socket(AF_INET,SOCK_STREAM,0);
        struct sockaddr_in serverAddress;
        struct sockaddr_in clientAddress;
        memset(&serverAddress,0,sizeof(serverAddress));
        serverAddress.sin_family=AF_INET;
        serverAddress.sin_addr.s_addr=htonl(INADDR_ANY);
        serverAddress.sin_port=htons(PORT_NUM);
        bind(sockfd,(struct sockaddr *)&serverAddress, sizeof(serverAddress));

        listen(sockfd,5);

        string th_str;

		socklen_t sosize  = sizeof(clientAddress);
		tcpsocket = accept(sockfd,(struct sockaddr*)&clientAddress,&sosize);
		th_str = inet_ntoa(clientAddress.sin_addr);
    cout<<"3"<<endl;
		pthread_create(&serverThread,NULL,&transfer,NULL);



        UDPSocket sock(PORT_NUM);

        char buffer[BUF_LEN]; // Buffer for echo string
        int recvMsgSize; // Size of received message
        string sourceAddress; // Address of datagram source
        unsigned short sourcePort; // Port of datagram source

        clock_t last_cycle = clock();

        while (1) {
            // Block until receive message from a client
            do {
                recvMsgSize = sock.recvFrom(buffer, BUF_LEN, sourceAddress, sourcePort);
            } while (recvMsgSize > sizeof(int));
            int total_pack = ((int * ) buffer)[0];

    //        cout << "expecting length of packs:" << total_pack << endl;
            char * longbuf = new char[PACK_SIZE * total_pack];
            for (int i = 0; i < total_pack; i++) {
                recvMsgSize = sock.recvFrom(buffer, BUF_LEN, sourceAddress, sourcePort);
                if (recvMsgSize != PACK_SIZE) {
                    cerr << "Received unexpected size pack:" << recvMsgSize << endl;
                    continue;
                }
                memcpy( & longbuf[i * PACK_SIZE], buffer, PACK_SIZE);
            }

    //        cout << "Received packet from " << sourceAddress << ":" << sourcePort << endl;

            Mat rawData = Mat(1, PACK_SIZE * total_pack, CV_8UC1, longbuf);
            Mat frame = imdecode(rawData, CV_LOAD_IMAGE_COLOR);
            if (frame.size().width == 0) {
                cerr << "decode failure!" << endl;
                continue;
            }
            if(frameQueue.size()<50 && FRAME%4!=0){
                frameQueue.push(frame);

            }
            FRAME++;

            //imshow("recv", frame);

            free(longbuf);

            //waitKey(1);
            clock_t next_cycle = clock();
            double duration = (next_cycle - last_cycle) / (double) CLOCKS_PER_SEC;
    //        cout << "\teffective FPS:" << (1 / duration) << " \tkbps:" << (PACK_SIZE * total_pack / duration / 1024 * 8) << endl;

     //       cout << next_cycle - last_cycle;
            last_cycle = next_cycle;
        }
    } catch (SocketException & e) {
        cerr << e.what() << endl;
        exit(1);
    }

    return 0;
}
