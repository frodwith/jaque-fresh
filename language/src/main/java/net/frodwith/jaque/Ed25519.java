package net.frodwith.jaque;

import net.frodwith.jaque.Ed25519Exception;

public class Ed25519 {
  final private static String LIB_NAME = "urbed25519";

  static {
    System.loadLibrary(LIB_NAME);
  }

  public static native void ed25519_create_keypair(
      byte[] publicKey, byte[] privateKey, byte[] seed)
      throws Ed25519Exception;
  public static native void ed25519_key_exchange(
      byte[] sharedSecret, byte[] publicKey, byte[] privateKey)
      throws Ed25519Exception;
  public static native void ed25519_sign(
      byte[] signature, byte[] message,
      byte[] publicKey, byte[] privateKey)
      throws Ed25519Exception;
  public static native int ed25519_verify(
      byte[] signature, byte[] message, byte[] publicKey)
      throws Ed25519Exception;
}
