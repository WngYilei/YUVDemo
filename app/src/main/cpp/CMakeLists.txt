
cmake_minimum_required(VERSION 3.18.1)


project("yuvdemo")

file(GLOB allCPP *.cpp)
add_library(
        yuvdemo
        SHARED
        ${allCPP}
)
#引入头文件
include_directories(${CMAKE_SOURCE_DIR}/include)

##引入库文件
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/${CMAKE_ANDROID_ARCH_ABI}")


find_library(
        log-lib
        log)

target_link_libraries(
        yuvdemo
        yuv
        jnigraphics
        ${log-lib})