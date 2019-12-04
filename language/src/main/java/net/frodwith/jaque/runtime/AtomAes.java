package net.frodwith.jaque.runtime;

import java.util.Arrays;
import java.math.BigInteger;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.CompilerDirectives;

import gnu.math.MPN;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.exception.ExitException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES operations on an Atom.
 */
public final class AtomAes {
  @TruffleBoundary
  public static Object aes_cbc(int mode, int keysize, Object key, Object iv, Object msg)
      throws ExitException {
    int len = HoonMath.met((byte)3, msg),
        out = (len % 16 == 0)
            ? len
            : len + 16 - (len % 16);
    byte[] ky = reverse(Atom.forceBytes(key, keysize)),
           iy = reverse(Atom.forceBytes(iv, 16)),
           my = reverse(Atom.forceBytes(msg, out));

    try {
      Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
      SecretKeySpec k = new SecretKeySpec(ky, "AES");
      c.init(mode, k, new IvParameterSpec(iy));

      return Atom.takeBytes(reverse(c.doFinal(my)), out);
    }
    catch (BadPaddingException e) {
      e.printStackTrace();
    }
    catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    catch (InvalidAlgorithmParameterException e) {
      e.printStackTrace();
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    catch (NoSuchPaddingException e) {
      e.printStackTrace();
    }
    throw new ExitException("aes_cbc failure");
  }

  @TruffleBoundary
  public static Object aes_ecb(int mode, int keysize, Object key, Object block)
      throws ExitException {
    byte[] ky = reverse(Atom.forceBytes(key, keysize)),
           by = reverse(Atom.forceBytes(block, 16));

    try {
      Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
      SecretKeySpec k = new SecretKeySpec(ky, "AES");
      c.init(mode, k);

      return Atom.takeBytes(reverse(c.doFinal(by)), 16);
    }
    catch (BadPaddingException e) {
      e.printStackTrace();
    }
    catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    catch (NoSuchPaddingException e) {
      e.printStackTrace();
    }
    throw new ExitException("aes_ecb failure");
  }

  /* IN-PLACE */
  private static byte[] reverse(byte[] a) {
    int i, j;
    byte b;
    for (i = 0, j = a.length - 1; j > i; ++i, --j) {
      b = a[i];
      a[i] = a[j];
      a[j] = b;
    }
    return a;
  }
}
