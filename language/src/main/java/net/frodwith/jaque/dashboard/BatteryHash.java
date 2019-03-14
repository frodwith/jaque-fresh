package net.frodwith.jaque.dashboard;

import java.util.Arrays;
import java.math.BigInteger;

import com.google.common.io.BaseEncoding;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.HoonSerial;


public final class BatteryHash {
  private byte[] sha;
  private static final HashFunction hf = Hashing.sha256();
  private static final BaseEncoding hex = BaseEncoding.base16().lowerCase();

  private BatteryHash(byte[] sha) {
    this.sha = sha;
  }

  public static BatteryHash hash(Cell battery) {
    HashCode hc = hf.hashBytes(HoonSerial.jamBytes(battery));
    return new BatteryHash(hc.asBytes());
  }

  public static HashCode read(String hexDigits) {
    return HashCode.fromBytes(hex.decode(hexDigits));
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof BatteryHash) &&
      Arrays.equals(sha, ((BatteryHash) o).sha);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(sha);
  }

  @Override
  public String toString() {
    return hex.encode(sha, 0, 32);
  }
}
