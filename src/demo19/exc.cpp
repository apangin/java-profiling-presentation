/**
 * This example shows how JVM TI agent slows down execution
 * of a Java application. The reason is an Exception Callback
 * that forces deoptimization on every thrown exception.
 * See https://stackoverflow.com/q/64468923/3448419
 *
 * First, cpu profiling reveals that deoptimization takes
 * at least 20% cpu time.
 * Second, profiling `Deoptimization::deoptimize` shows where
 * the deoptimization comes from. Note the reverse Flame Graph.
 * Finally, profiling `java.lang.Throwable.<init>`
 * helps to collect statistics on thrown exceptions
 * without JVM TI overhead.
 * To produce a backtrace view, use `tree,reverse` options.
 */
#include <jvmti.h>
#include <stdio.h>
#include <string.h>

void JNICALL ExceptionCallback(jvmtiEnv* jvmti, JNIEnv* env, jthread thread,
                               jmethodID method, jlocation location, jobject exception,
                               jmethodID catch_method, jlocation catch_location) {
    jclass exception_class = env->GetObjectClass(exception);

    char* class_name = NULL;
    jclass holder = NULL;
    char* holder_name = NULL;
    char* method_name = NULL;

    if (jvmti->GetClassSignature(exception_class, &class_name, NULL) == 0 &&
        jvmti->GetMethodName(method, &method_name, NULL, NULL) == 0 &&
        jvmti->GetMethodDeclaringClass(method, &holder) == 0 &&
        jvmti->GetClassSignature(holder, &holder_name, NULL) == 0)
    {
        class_name[strlen(class_name) - 1] = 0;
        holder_name[strlen(holder_name) - 1] = 0;
        // printf("[exc] %s at %s.%s\n", class_name + 1, holder_name + 1, method_name);
    }

    jvmti->Deallocate((unsigned char*) holder_name);
    jvmti->Deallocate((unsigned char*) method_name);
    jvmti->Deallocate((unsigned char*) class_name);
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM* vm, char* options, void* reserved) {
    jvmtiEnv* jvmti;
    vm->GetEnv((void**) &jvmti, JVMTI_VERSION_1_0);

    jvmtiCapabilities capabilities = {0};
    capabilities.can_generate_exception_events = 1;
    jvmti->AddCapabilities(&capabilities);

    jvmtiEventCallbacks callbacks = {0};
    callbacks.Exception = ExceptionCallback;
    jvmti->SetEventCallbacks(&callbacks, sizeof(callbacks));
    jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_EXCEPTION, NULL);

    printf("Native agent loaded\n");
    return 0;
}
