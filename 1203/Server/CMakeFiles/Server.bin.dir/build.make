# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.5

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/hansb/openpose/examples/user_codesendPos

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/hansb/openpose/examples/user_codesendPos

# Include any dependencies generated for this target.
include CMakeFiles/Server.bin.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/Server.bin.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/Server.bin.dir/flags.make

CMakeFiles/Server.bin.dir/Server.cpp.o: CMakeFiles/Server.bin.dir/flags.make
CMakeFiles/Server.bin.dir/Server.cpp.o: Server.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/hansb/openpose/examples/user_codesendPos/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/Server.bin.dir/Server.cpp.o"
	/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/Server.bin.dir/Server.cpp.o -c /home/hansb/openpose/examples/user_codesendPos/Server.cpp

CMakeFiles/Server.bin.dir/Server.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/Server.bin.dir/Server.cpp.i"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/hansb/openpose/examples/user_codesendPos/Server.cpp > CMakeFiles/Server.bin.dir/Server.cpp.i

CMakeFiles/Server.bin.dir/Server.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/Server.bin.dir/Server.cpp.s"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/hansb/openpose/examples/user_codesendPos/Server.cpp -o CMakeFiles/Server.bin.dir/Server.cpp.s

CMakeFiles/Server.bin.dir/Server.cpp.o.requires:

.PHONY : CMakeFiles/Server.bin.dir/Server.cpp.o.requires

CMakeFiles/Server.bin.dir/Server.cpp.o.provides: CMakeFiles/Server.bin.dir/Server.cpp.o.requires
	$(MAKE) -f CMakeFiles/Server.bin.dir/build.make CMakeFiles/Server.bin.dir/Server.cpp.o.provides.build
.PHONY : CMakeFiles/Server.bin.dir/Server.cpp.o.provides

CMakeFiles/Server.bin.dir/Server.cpp.o.provides.build: CMakeFiles/Server.bin.dir/Server.cpp.o


CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o: CMakeFiles/Server.bin.dir/flags.make
CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o: PracticalSocket.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/hansb/openpose/examples/user_codesendPos/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o"
	/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o -c /home/hansb/openpose/examples/user_codesendPos/PracticalSocket.cpp

CMakeFiles/Server.bin.dir/PracticalSocket.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/Server.bin.dir/PracticalSocket.cpp.i"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/hansb/openpose/examples/user_codesendPos/PracticalSocket.cpp > CMakeFiles/Server.bin.dir/PracticalSocket.cpp.i

CMakeFiles/Server.bin.dir/PracticalSocket.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/Server.bin.dir/PracticalSocket.cpp.s"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/hansb/openpose/examples/user_codesendPos/PracticalSocket.cpp -o CMakeFiles/Server.bin.dir/PracticalSocket.cpp.s

CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o.requires:

.PHONY : CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o.requires

CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o.provides: CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o.requires
	$(MAKE) -f CMakeFiles/Server.bin.dir/build.make CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o.provides.build
.PHONY : CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o.provides

CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o.provides.build: CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o


# Object files for target Server.bin
Server_bin_OBJECTS = \
"CMakeFiles/Server.bin.dir/Server.cpp.o" \
"CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o"

# External object files for target Server.bin
Server_bin_EXTERNAL_OBJECTS =

Server.bin: CMakeFiles/Server.bin.dir/Server.cpp.o
Server.bin: CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o
Server.bin: CMakeFiles/Server.bin.dir/build.make
Server.bin: /usr/local/lib/libopenpose.so.1.1.0
Server.bin: /usr/lib/x86_64-linux-gnu/libgflags.so
Server.bin: /usr/lib/x86_64-linux-gnu/libglog.so
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_videostab.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_ts.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_superres.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_stitching.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_ocl.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_gpu.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_contrib.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_photo.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_legacy.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_video.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_objdetect.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_ml.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_calib3d.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_features2d.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_highgui.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_imgproc.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_flann.so.2.4.9
Server.bin: /usr/lib/x86_64-linux-gnu/libopencv_core.so.2.4.9
Server.bin: /usr/local/cuda/lib64/libcudart_static.a
Server.bin: /usr/lib/x86_64-linux-gnu/librt.so
Server.bin: /usr/lib/x86_64-linux-gnu/libboost_filesystem.so
Server.bin: /usr/lib/x86_64-linux-gnu/libboost_system.so
Server.bin: CMakeFiles/Server.bin.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/hansb/openpose/examples/user_codesendPos/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Linking CXX executable Server.bin"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/Server.bin.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/Server.bin.dir/build: Server.bin

.PHONY : CMakeFiles/Server.bin.dir/build

CMakeFiles/Server.bin.dir/requires: CMakeFiles/Server.bin.dir/Server.cpp.o.requires
CMakeFiles/Server.bin.dir/requires: CMakeFiles/Server.bin.dir/PracticalSocket.cpp.o.requires

.PHONY : CMakeFiles/Server.bin.dir/requires

CMakeFiles/Server.bin.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/Server.bin.dir/cmake_clean.cmake
.PHONY : CMakeFiles/Server.bin.dir/clean

CMakeFiles/Server.bin.dir/depend:
	cd /home/hansb/openpose/examples/user_codesendPos && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/hansb/openpose/examples/user_codesendPos /home/hansb/openpose/examples/user_codesendPos /home/hansb/openpose/examples/user_codesendPos /home/hansb/openpose/examples/user_codesendPos /home/hansb/openpose/examples/user_codesendPos/CMakeFiles/Server.bin.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/Server.bin.dir/depend

