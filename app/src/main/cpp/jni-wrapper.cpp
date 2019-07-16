#include <jni.h>
#include <string>
#include <iostream>
#include "in_memory_note_repository.hpp"
#include "foo.hpp"

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_fondesa_androidnativebasedsample_Foo_foo(
        JNIEnv *env,
        jobject /* this */,
        jstring input) {
    jboolean isCopy = JNI_TRUE;
    const char *utfInput = env->GetStringUTFChars(input, &isCopy);
    std::string stdInput = std::string(utfInput);
    std::unique_ptr<Foo> foo = std::make_unique<Foo>(stdInput);
    std::string output = foo->getValue();
    return env->NewStringUTF(output.c_str());
}
JNIEXPORT jlong JNICALL
Java_com_fondesa_androidnativebasedsample_NoteRepository_initialize(
        JNIEnv *env,
        jobject /* this */
) {
    auto repository = std::make_unique<InMemoryNoteRepository>();
    auto repositoryHandle = repository.get();
    return reinterpret_cast<jlong>(repositoryHandle);
}

JNIEXPORT void JNICALL
Java_com_fondesa_androidnativebasedsample_NoteRepository_remove(
        JNIEnv *env,
        jobject /* this */,
        jlong handle,
        jint id
) {
    auto repository = (InMemoryNoteRepository *) handle;
    repository->remove(id);
}
}