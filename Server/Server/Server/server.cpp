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
		recv(Connections, (char*)&bufferlength, sizeof(int), NULL); //�޼����� ������ �˾Ƴ�
		char* buffer = new char[bufferlength]; //�޼��� ���� ����
		recv(Connections, buffer, bufferlength, NULL); //Ŭ���̾�Ʈ�� ���� �޽��� ����

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
	if (WSAStartup(DllVersion, &wsaData) != 0) //return ���� 0�� �ƴϸ� ����
	{
		MessageBoxA(NULL, "Winsock startup failed", "Error", MB_OK | MB_ICONERROR);
		exit(1);
	}
	SOCKADDR_IN addr; //������ ������ ���ε��� �ּ�
	int addrlen = sizeof(addr); //�ּ��� ����
	addr.sin_addr.s_addr = inet_addr("127.0.0.1"); //����ȣ��Ʈ�� ��ε� ĳ��Ʈ
	addr.sin_port = htons(1111); //��Ʈ ��ȣ
	addr.sin_family = AF_INET; // IPv4 ����(AF_INET6�� IPv6)

	SOCKET sListen = socket(AF_INET, SOCK_STREAM, NULL); //Ŀ�ؼ��� listen�� ���� ����
	bind(sListen, (SOCKADDR*)&addr, sizeof(addr));//�ּҸ� ���Ͽ� ���ε�
	listen(sListen, SOMAXCONN); //SOMAXCONN�� �ƽø� ť�� ����(Ŭ���̾�Ʈ ��� ť)

	SOCKET newConnection; //Ŭ���̾�Ʈ�� Ŀ�ؼ��� ������ ����
	newConnection = accept(sListen, (SOCKADDR*)&addr, &addrlen); //���ο� Ŀ�ؼ� accept
	if (newConnection == 0)
	{
		cout << "Ŭ���̾�Ʈ�� ���� ����..." << endl;
	}
	else
	{
		cout << "Ŭ���̾�Ʈ�� ���� ����!!!" << endl;
		string MOTD = "�׽�Ʈ �޽��� �Դϴ�.";
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

