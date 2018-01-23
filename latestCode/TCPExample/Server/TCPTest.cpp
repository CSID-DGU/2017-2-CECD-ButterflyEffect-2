// TCPTest.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <WS2tcpip.h>
#include <iostream>
#include <string>
#pragma comment (lib, "ws2_32.lib")
using namespace std;

int main() {
	//initialize winsock
	WSADATA WSData;
	WORD ver = MAKEWORD(2, 2);

	int wsok = WSAStartup(ver, &WSData);

	if (wsok != 0) {
		cerr << "can't initialize winsock" << endl;
		return 0;
	}

	//create socket
	SOCKET listening = socket(AF_INET, SOCK_STREAM, 0);
	if (listening == INVALID_SOCKET) {
		cerr << "can't create socket" << endl;
	}
	cout << "socket created!!" << endl;
	//bind socket to ip address and port to a socket

	sockaddr_in hint;
	hint.sin_family = AF_INET;
	hint.sin_port = htons(9500);
	hint.sin_addr.S_un.S_addr = htonl(ADDR_ANY);
	//could also use inet_pton

	bind(listening, (sockaddr *)&hint, sizeof(hint));

	//tell winsock the socket is listening
	listen(listening, SOMAXCONN);
	cout << "Listening..." << endl;
	//wait for connection
	sockaddr_in client;
	int clientSize = sizeof(client);

	SOCKET clientsocket = accept(listening, (sockaddr *)&client, &clientSize);
	cout << "Accept..." << endl;

	char host[NI_MAXHOST]; // client remote name
	char service[NI_MAXSERV]; // service port the client is connected on
	ZeroMemory(host, NI_MAXHOST); // same as memset
	ZeroMemory(service, NI_MAXSERV);

	if (getnameinfo((sockaddr*)&client, sizeof(client), host, NI_MAXHOST, service, NI_MAXSERV, 0) == 0) {
		cout << host << " connected on port " << service << endl;
	}
	else {
		inet_ntop(AF_INET, &client.sin_addr, host, NI_MAXHOST);
		cout << host << " connected on port " << ntohs(client.sin_port) << endl;
	}

	//close listening socket
	closesocket(listening);

	//while loop: accept and echo message back to client
	char buf[4096];
	while (true) {
		ZeroMemory(buf, 4096);
		//wait for client to send data
		int bytesReceived = recv(clientsocket, buf, 4096, 0);
		if (bytesReceived == SOCKET_ERROR) {
			cerr << "Error in recv " << endl;
			break;
		}

		if (bytesReceived == 0) {
			cout << "client disconnected" << endl;
			break;
		}
		buf[bytesReceived] = '\0';
		//buf[bytesReceived - 2] = 'k';
		cout << string(buf,0, bytesReceived);
		char str[23]; 
		//cout << buf << endl;
		//echo message back to client
		send(clientsocket, buf, bytesReceived, 0);

	}

	//close the socket
	closesocket(clientsocket);

	//cleanup winsock
	WSACleanup();
	return 0;
}
