//Server
#pragma comment(lib, "ws2_32.lib")
#include <WinSock2.h>
#include <iostream>
#include <cstdlib>
#include <ctime>
#include <string>
using namespace std;

SOCKET Connections;
int ConnectionCounter = 0;
typedef struct Pos {
	int x1, y1;
	int x2, y2;
}Pos;

string getPoint() {
	Pos pos;
	string str = "";
	srand((unsigned)time(NULL));
	pos.x1 = rand() % 50 + 1; str += to_string(pos.x1) + ',';
	pos.y1 = rand() % 50 + 1; str += to_string(pos.y1) + ',';
	pos.x2 = pos.x1 + (rand() % 300 + 1); str += to_string(pos.x2) + ',';
	pos.y2 = pos.y1 + (rand() % 300 + 1); str += to_string(pos.y2);
	return str;
}
void ClientHandlerThread(int index)
{
	int bufferlength;
	while (true)
	{
		string pos = getPoint();
		recv(Connections, (char*)&bufferlength, sizeof(int), NULL); //메세지의 사이즈 알아냄
		char* buffer = new char[bufferlength]; //메세지 저장 버퍼
		recv(Connections, buffer, bufferlength, NULL); //클라이언트로 부터 메시지 받음

		int posLength = pos.size();
		send(Connections, (char*)&posLength, sizeof(int), NULL);
		send(Connections, pos.c_str(), posLength, NULL);
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

	SOCKET sListen = socket(AF_INET, SOCK_STREAM, NULL); //커넥션을 listen할 소켓 생성
	bind(sListen, (SOCKADDR*)&addr, sizeof(addr));//주소를 소켓에 바인드
	listen(sListen, SOMAXCONN); //SOMAXCONN는 맥시멈 큐의 길이(클라이언트 대기 큐)

	SOCKET newConnection; //클라이언트의 커넥션을 유지할 소켓
	newConnection = accept(sListen, (SOCKADDR*)&addr, &addrlen); //새로운 커넥션 accept
	if (newConnection == 0)
	{
		cout << "클라이언트와 연결 실패..." << endl;
	}
	else
	{
		cout << "클라이언트와 연결 성공!!!" << endl;
		string MOTD = "테스트 메시지 입니다.";
		int MOTDLength = MOTD.size();
		send(newConnection, (char*)&MOTDLength, sizeof(int), NULL);
		send(newConnection, MOTD.c_str(), MOTDLength, NULL);
		Connections = newConnection;
		ConnectionCounter += 1;
		CreateThread(NULL, NULL, (LPTHREAD_START_ROUTINE)ClientHandlerThread, (LPVOID)(0), NULL, NULL);
	}
	system("pause");
	return 0;
}

