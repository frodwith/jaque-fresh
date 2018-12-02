package net.frodwith.jaque.dashboard;

import java.util.Arrays;

import com.google.common.hash.Hashing;
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
    return new BatteryHash(hf.hashBytes(HoonSerial.jamBytes(battery)));
  }

  @Override
  public boolean equals(Object o) {
    return (o instanceof BatteryHash) &&
      Arrays.equals(sha, ((BatteryHash) o).sha);
  }

  @Override
  public boolean hashCode() {
    return Arrays.hashCode(sha);
  }

  @Override
  public boolean toString() {
    return Base64.getEncoder().encode(sha);
  }
}
