package net.frodwith.jaque;

import net.frodwith.jaque.Ed25519Exception;

public class Ed25519 {
  final private static String LIB_NAME = "urbed25519";

  static {
    System.loadLibrary(LIB_NAME);
  }

  // Wrappers around the exported functions from the ed25519 library.
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

  // Additional jets which interact with ed25519 in more interesting semantic
  // ways, as they use internal point math functions to do multiplication on
  // curves
  public static native void point_add(
      byte[] output, byte[] aPoint, byte[] bPoint)
      throws Ed25519Exception;
  public static native void scalarmult(
      byte[] output, byte[] a, byte[] aPoint)
      throws Ed25519Exception;
  public static native void scalarmult_base(
      byte[] output, byte[] scalar)
      throws Ed25519Exception;
  public static native void add_scalarmult_scalarmult_base(
      byte[] output, byte[] a, byte[] aPoint, byte[] b)
      throws Ed25519Exception;
  public static native void add_double_scalarmult(
      byte[] output, byte[] a, byte[] aPoint, byte[] b, byte[] bPoint)
      throws Ed25519Exception;
}
