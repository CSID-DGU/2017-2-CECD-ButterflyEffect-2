CC = g++
CFLAGS	= -g -Wall
SRCS = DrawObject.cpp
PROG = DrawObject

OPENCV = `pkg-config opencv --cflags --libs`
LIBS = $(OPENCV)

$(PROG) : $(SRCS)
	$(CC) $(CFLAGS) -o $(PROG) $(SRCS) $(LIBS) -lpthread

clean:
	rm -f $(PROG)
