//Client
#pragma comment(lib, "ws2_32.lib")
#include <WinSock2.h>
#include <iostream>
#include <string>
#include <regex>
#include <opencv2/opencv.hpp>
using namespace std;
using namespace cv;
SOCKET Connection;

typedef struct Pos {
	int x1, y1;
	int x2, y2;
}Pos;

Pos pos;
void draw_Rectangle() {
	Mat drawing;
	drawing = Mat::zeros(500, 500, CV_8UC3);
	rectangle(drawing, Point(pos.x1, pos.y1), Point(pos.x2, pos.y2), Scalar(200, 0, 0), 2, 3);
	imshow("image", drawing);
	waitKey(30);
}
void ClientThread()
{
	string str;
	regex rex_Pos("(.*),(.*),(.*),(.*)");
	smatch match_Pos;
	int bufferlength;
	while (true)
	{
		recv(Connection, (char*)&bufferlength, sizeof(int), NULL);
		char* buffer = new char[bufferlength + 1];
		buffer[bufferlength] = '\0'; // 문자열의 끝을 알려주기 위해 마지막 문자 null값
		recv(Connection, buffer, bufferlength, NULL);
		str = buffer;
		if (regex_search(str, match_Pos, rex_Pos)) {
			for (int i = 1; i < match_Pos.size(); i++) {
				cout << match_Pos[i] << endl;
				if (i == 1)
					pos.x1 = stoi(match_Pos[i]);
				else if (i == 2)
					pos.y1 = stoi(match_Pos[i]);
				else if (i == 3)
					pos.x2 = stoi(match_Pos[i]);
				else
					pos.y2 = stoi(match_Pos[i]);
			}
			draw_Rectangle();
		}
		delete[] buffer;
	}
}

int main()
{
	WSAData wsaData;
	WORD DllVersion = MAKEWORD(2, 1);
	if (WSAStartup(DllVersion, &wsaData) != 0) //return 값이 0이 아니면 에러
	{
		MessageBoxA(NULL, "Winsock startup failed", "Error", MB_OK | MB_ICONERROR);
		exit(1);
	}
	SOCKADDR_IN addr; //리스닝 소켓을 바인드할 주소
	int addrlen = sizeof(addr); //주소의 길이
	addr.sin_addr.s_addr = inet_addr("127.0.0.1"); //로컬호스트로 브로드 캐스트
	addr.sin_port = htons(1111); //포트 번호
	addr.sin_family = AF_INET; // IPv4 소켓(AF_INET6는 IPv6)

	Connection = socket(AF_INET, SOCK_STREAM, NULL); // 커넥션 소켓 설정
	if (connect(Connection, (SOCKADDR*)&addr, sizeof(addr)) != 0)
	{
		MessageBoxA(NULL, "연결 실패", "Error", MB_OK | MB_ICONERROR);
		return 0;
	}
	cout << "연결되었습니다!!" << endl;
	CreateThread(NULL, NULL, (LPTHREAD_START_ROUTINE)ClientThread, NULL, NULL, NULL);
	string buffer;
	while (true)
	{
		getline(cin, buffer);
		int bufferlength = buffer.size();
		send(Connection, (char*)&bufferlength, sizeof(int), NULL);
		send(Connection, buffer.c_str(), bufferlength, NULL);
		Sleep(10);
	}
	system("pause");
	return 0;
}