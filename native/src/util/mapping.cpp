#include <vector>
#include <note.hpp>
#include "draft.hpp"
#include "mapping.hpp"
#include "jclass_util.hpp"

namespace Jni {

template<>
jobject mapFromNative<Draft>(JNIEnv *env, Draft obj, jclass cls, jmethodID constructor) {
    auto draft = std::move(obj);
    jstring noteTitle = env->NewStringUTF(draft.getTitle().c_str());
    jstring noteDescription = env->NewStringUTF(draft.getDescription().c_str());

    return env->NewObject(cls,
                          constructor,
                          noteTitle,
                          noteDescription);
}

template<>
jobject mapFromNative<Note>(JNIEnv *env, Note obj, jclass cls, jmethodID constructor) {
    auto note = std::move(obj);
    jint noteId = note.getId();
    jstring noteTitle = env->NewStringUTF(note.getTitle().c_str());
    jstring noteDescription = env->NewStringUTF(note.getDescription().c_str());

    return env->NewObject(cls,
                          constructor,
                          noteId,
                          noteTitle,
                          noteDescription);
}

template<>
Draft mapToNative(JNIEnv *env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);

    auto title = Jni::findField<StringField>(env, obj, cls, "title");
    auto description = Jni::findField<StringField>(env, obj, cls, "description");

    return Draft(title.utfValue, description.utfValue);
}

template<>
Note mapToNative(JNIEnv *env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);

    auto id = Jni::findField<int>(env, obj, cls, "id");
    auto title = Jni::findField<StringField>(env, obj, cls, "title");
    auto description = Jni::findField<StringField>(env, obj, cls, "description");

    return Note(id, title.utfValue, description.utfValue);
}
}