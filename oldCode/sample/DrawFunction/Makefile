CC = g++
CFLAGS	= -g -std=c++11 -Wall
SRCS = ImageMove.cpp
PROG = ImageMove

OPENCV = `pkg-config opencv --cflags --libs`
LIBS = $(OPENCV)

$(PROG) : $(SRCS)
	$(CC) $(CFLAGS) -o $(PROG) $(SRCS) $(LIBS) -lpthread

clean:
	rm -f $(PROG)
