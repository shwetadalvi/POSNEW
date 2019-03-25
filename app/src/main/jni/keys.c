#include <jni.h>


JNIEXPORT jstring JNICALL
Java_com_abremiratesintl_KOT_MainActivity_getNativeKey(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "YWJyYWRtaW4xMjM=");
}
JNIEXPORT jstring JNICALL
Java_com_abremiratesintl_KOT_MainActivity_getAdminKey(JNIEnv *env, jobject instance){
        return (*env)->  NewStringUTF(env, "MjIzNw==");
}
JNIEXPORT jstring JNICALL
Java_com_abremiratesintl_KOT_MainActivity_getUserKey(JNIEnv *env, jobject instance){
    return (*env)->  NewStringUTF(env, "MjIzNw==");
}
