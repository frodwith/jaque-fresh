#include "net_frodwith_jaque_Ed25519.h"
#include <stdio.h>

/**
 * Helper method to get data out of jbyteArray. Returns false on bad input.
 */
int byteArrayToCharArray(JNIEnv* env, jbyteArray a, int assumedLen,
                         unsigned char* dest) {
  int len = (*env)->GetArrayLength(env, a);
  if (len != assumedLen)
    return 0;

  (*env)->GetByteArrayRegion(env, a, 0, len, (jbyte*)dest);
  return 1;
}

//int copyCharArrayTo

/**
 * Throws an exception back to Java.
 */
void throwException(JNIEnv* env, const char* message) {
  jclass exceptionClass;
  const char className[] = "net/frodwith/jaque/Ed25519Exception";

  exceptionClass = (*env)->FindClass(env, className);
  if (exceptionClass == NULL) {
    exceptionClass = (*env)->FindClass(env, "java/lang/Exception");

    if (exceptionClass == NULL) {
      fprintf(stderr,
              "WARNING: FAILED TO THROW EXCEPTION WHILE TRYING TO THROW "
              "EXCEPTION!\r\n");
      return;
    }
  }

  (*env)->ThrowNew(env, exceptionClass, message);
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_create_keypair
 * Signature: ([B[B[B)V
 */
JNIEXPORT void JNICALL
Java_net_frodwith_jaque_Ed25519_ed25519_1create_1keypair(
    JNIEnv* env,
    jclass thiz,
    jbyteArray jPublicKey,
    jbyteArray jPrivateKey,
    jbyteArray jSeed)
{
  // Copy the
  unsigned char publicKey[32];
  unsigned char privateKey[64];
  unsigned char seed[32];

  if (!byteArrayToCharArray(env, jSeed, 32, seed)) {
    throwException(env, "Seed size not equal to 32");
    return;
  }

  /* ed25519_create_keypair(publicKey, privateKey, seed); */

  /* if (! */


  fprintf(stderr, "\red25519_create_keypair\r\n");
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_key_exchange
 * Signature: ([B[B[B)V
 */
JNIEXPORT void JNICALL
Java_net_frodwith_jaque_Ed25519_ed25519_1key_1exchange(
    JNIEnv* env,
    jclass thiz,
    jbyteArray sharedSecret,
    jbyteArray publicKey,
    jbyteArray privateKey)
{
  fprintf(stderr, "\red25519_key_exchange\r\n");
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_sign
 * Signature: ([B[BJ[B[B)V
 */
JNIEXPORT void JNICALL
Java_net_frodwith_jaque_Ed25519_ed25519_1sign(
    JNIEnv* env,
    jclass thiz,
    jbyteArray signature,
    jbyteArray message,
    jlong len,
    jbyteArray publicKey,
    jbyteArray privateKey)
{
  fprintf(stderr, "\red25519_sign\r\n");
}

/*
 * Class:     net_frodwith_jaque_Ed25519
 * Method:    ed25519_verify
 * Signature: ([B[BJ[B)I
 */
JNIEXPORT jint JNICALL
Java_net_frodwith_jaque_Ed25519_ed25519_1verify(
    JNIEnv* env,
    jclass thiz,
    jbyteArray signature,
    jbyteArray message,
    jlong len,
    jbyteArray publicKey)
{
  fprintf(stderr, "\red25519_verify\r\n");
}
