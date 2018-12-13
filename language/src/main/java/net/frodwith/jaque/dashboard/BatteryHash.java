package net.frodwith.jaque.dashboard;

import java.util.Arrays;
import java.math.BigInteger;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.HoonSerial;

public final class BatteryHash {
  private byte[] sha;
  private static final HashFunction hf = Hashing.sha256();

  private BatteryHash(byte[] sha) {
    this.sha = sha;
  }

  public static BatteryHash hash(Cell battery) {
    HashCode hc = hf.hashBytes(HoonSerial.jamBytes(battery));
    return new BatteryHash(hc.asBytes());
  }

  public static BatteryHash read(String hexDigits) {
    BigInteger big = new BigInteger(hexDigits, 16);
    byte[] bytes = big.toByteArray();
    if ( bytes.length != 33 || 0 != bytes[0] ) {
      throw new IllegalArgumentException(hexDigits);
    }
    return new BatteryHash(Arrays.copyOf(bytes, 32));
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
    StringBuilder b = new StringBuilder();
    for ( int i = 0; i < 32; ++i ) {
      b.append(String.format("%02x", sha[i]));
    }
    return b.toString();
  }
}
