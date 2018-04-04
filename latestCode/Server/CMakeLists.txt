cmake_minimum_required(VERSION 2.8.7)

if (CMAKE_VERSION VERSION_LESS "3")
  project(json11 CXX)
else()
  cmake_policy(SET CMP0048 NEW)
  project(json11 VERSION 1.0.0 LANGUAGES CXX)
endif()
option(JSON11_BUILD_TESTS "Build unit tests" ON)
option(JSON11_ENABLE_DR1467_CANARY "Enable canary test for DR 1467" OFF)

if(CMAKE_VERSION VERSION_LESS "3")
  add_definitions(-std=c++11)
else()
  set(CMAKE_CXX_STANDARD 11)
  set(CMAKE_CXX_STANDARD_REQUIRED ON)
endif()

if(CMAKE_INSTALL_PREFIX_INITIALIZED_TO_DEFAULT)
  set(CMAKE_INSTALL_PREFIX /usr)
endif()

add_library(json11 json11/json11.cpp)
target_include_directories(json11 PUBLIC "${CMAKE_CURRENT_SOURCE_DIR}/json11")
target_compile_options(json11
  PRIVATE -fPIC -fno-rtti -fno-exceptions -Wall)

configure_file("json11/json11.pc.in" "json11/json11.pc" @ONLY)


add_definitions(-std=c++11)
list(APPEND CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/cmake/Modules")
find_package(GFlags)
find_package(Glog)
find_package(OpenCV)
find_package(OpenPose REQUIRED)

include_directories(${OpenPose_INCLUDE_DIRS} ${GFLAGS_INCLUDE_DIR} ${GLOG_INCLUDE_DIR} ${OpenCV_INCLUDE_DIRS})

if (JSON11_BUILD_TESTS)

  # enable test for DR1467, described here: https://llvm.org/bugs/show_bug.cgi?id=23812
  if(JSON11_ENABLE_DR1467_CANARY)
    add_definitions(-D JSON11_ENABLE_DR1467_CANARY=1)
  else()
    add_definitions(-D JSON11_ENABLE_DR1467_CANARY=0)
  endif()


add_executable(Server.bin Server.cpp PracticalSocket.cpp json11/json11.cpp)

target_link_libraries(Server.bin ${OpenPose_LIBS} ${GFLAGS_LIBRARY} ${GLOG_LIBRARY} ${OpenCV_LIBS})
target_link_libraries(Server.bin pthread)
target_link_libraries(Server.bin json11)

endif()

install(TARGETS json11 DESTINATION lib/${CMAKE_LIBRARY_ARCHITECTURE})
install(FILES "${CMAKE_CURRENT_SOURCE_DIR}/json11/json11.hpp" DESTINATION include/${CMAKE_LIBRARY_ARCHITECTURE})
install(FILES "${CMAKE_CURRENT_BINARY_DIR}/json11/json11.pc" DESTINATION lib/${CMAKE_LIBRARY_ARCHITECTURE}/pkgconfig)


