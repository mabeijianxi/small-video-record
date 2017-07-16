# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             jx_ffmpeg_jni

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
              src/main/cpp/jx_yuv_encode_h264.cpp
              src/main/cpp/jx_pcm_encode_aac.cpp
              src/main/cpp/jx_media_muxer.cpp
              src/main/cpp/jx_jni_handler.cpp
              src/main/cpp/jx_ffmpeg_jni.cpp
              src/main/cpp/threadsafe_queue.cpp
              src/main/cpp/jx_log.cpp
              src/main/cpp/cmdutils.c
              src/main/cpp/ffmpeg.c
              src/main/cpp/ffmpeg_filter.c
              src/main/cpp/ffmpeg_opt.c
              src/main/cpp/jx_ffmpeg_cmd_run.c
              src/main/cpp/jx_ffmpeg_config.cpp
             )

add_library(
            avcodec
            SHARED
            IMPORTED
            )

add_library(
            avfilter
            SHARED
            IMPORTED
             )


add_library(
            avformat
            SHARED
            IMPORTED
            )


add_library(
            avutil
            SHARED
            IMPORTED
            )

add_library(
            swresample
            SHARED
            IMPORTED
            )

add_library(
            swscale
            SHARED
            IMPORTED
            )


add_library(
            fdk-aac
            SHARED
            IMPORTED
            )

if(${ANDROID_ABI} STREQUAL "armeabi")
set_target_properties(
    avcodec
    PROPERTIES IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavcodec.so
    )

set_target_properties(
        avfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavfilter.so
        )

set_target_properties(
            avformat
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavformat.so
            )

set_target_properties(
            avutil
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavutil.so
            )

set_target_properties(
            swresample
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libswresample.so
             )

set_target_properties(
            swscale
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libswscale.so
             )


set_target_properties(
            fdk-aac
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libfdk-aac.so
             )
endif(${ANDROID_ABI} STREQUAL "armeabi")

if(${ANDROID_ABI} STREQUAL "armeabi-v7a")

set_target_properties(
    avcodec
    PROPERTIES IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libavcodec.so
    )

set_target_properties(
        avfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libavfilter.so
        )

set_target_properties(
            avformat
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libavformat.so
            )

set_target_properties(
            avutil
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libavutil.so
            )

set_target_properties(
            swresample
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libswresample.so
             )

set_target_properties(
            swscale
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libswscale.so
             )


set_target_properties(
            fdk-aac
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libfdk-aac.so
             )

endif(${ANDROID_ABI} STREQUAL "armeabi-v7a")

if(${ANDROID_ABI} STREQUAL "arm64-v8a")
set_target_properties(
    avcodec
    PROPERTIES IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libavcodec.so
    )

set_target_properties(
        avfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libavfilter.so
        )

set_target_properties(
            avformat
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libavformat.so
            )

set_target_properties(
            avutil
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libavutil.so
            )

set_target_properties(
            swresample
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libswresample.so
             )

set_target_properties(
            swscale
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libswscale.so
             )


set_target_properties(
            fdk-aac
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a/libfdk-aac.so
             )
endif(${ANDROID_ABI} STREQUAL "arm64-v8a")

if(${ANDROID_ABI} STREQUAL "x86")
set_target_properties(
    avcodec
    PROPERTIES IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libavcodec.so
    )

set_target_properties(
        avfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libavfilter.so
        )

set_target_properties(
            avformat
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libavformat.so
            )

set_target_properties(
            avutil
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libavutil.so
            )

set_target_properties(
            swresample
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libswresample.so
             )

set_target_properties(
            swscale
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libswscale.so
             )

set_target_properties(
            fdk-aac
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86/libfdk-aac.so
             )
endif(${ANDROID_ABI} STREQUAL "x86")

if(${ANDROID_ABI} STREQUAL "x86_64")
set_target_properties(
    avcodec
    PROPERTIES IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libavcodec.so
    )

set_target_properties(
        avfilter
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libavfilter.so
        )

set_target_properties(
            avformat
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libavformat.so
            )

set_target_properties(
            avutil
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libavutil.so
            )

set_target_properties(
            swresample
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libswresample.so
             )

set_target_properties(
            swscale
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libswscale.so
             )

set_target_properties(
            fdk-aac
            PROPERTIES IMPORTED_LOCATION
            ${CMAKE_SOURCE_DIR}/src/main/jniLibs/x86_64/libfdk-aac.so
             )
endif(${ANDROID_ABI} STREQUAL "x86_64")

include_directories(
    ${CMAKE_SOURCE_DIR}/ffmpeg-3.2.5/

)
# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       jx_ffmpeg_jni
                       fdk-aac
                       avcodec
                       avfilter
                       avformat
                       avutil
                       swresample
                       swscale
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )