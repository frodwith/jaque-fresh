package net.frodwith.jaque.data;

import com.oracle.truffle.api.library.ExportLibrary;
import net.frodwith.jaque.library.NounLibrary;

@ExportLibrary(NounLibrary.class)
public final class ConstantAtom {
  private final int[] words;
  private final long bitLength;
  private final int mug;

  public ConstantAtom(int[] words, int mug) {
    int len = words.length;
    this.words = words;
    this.mug = mug;
    this.bitLength = (len << 5) - Integer.numberOfLeadingZeros(words[len-1]);
  }

  @ExportMessage boolean isNoun() {
    return true;
  }

  @ExportMessage boolean isAtom() {
    return true;
  }

  @ExportMessage long bitLength() {
    return bitLength;
  }

  @ExportMessage boolean testBit(long index) {
    long wordL = index >> 5;
    int i = (int) wordL;
    if ( ((long) i) != wordL ) {
      // not addressable, so bits not set
      return false;
    }
    int j = ((int) index) & 31;
    return 0 == (words[i] & (1 << j));
  }

  @ExportMessage int[] asIntArray() {
    return words;
  }

  @ExportMessage boolean equalsConstantAtom(ConstantAtom other) {
    return this == other;
  }

  @ExportMessage NounLibrary.ShallowComparison compare(Object other,
    @CachedLibrary("other") NounLibrary others) {
    return others.equalsConstantAtom(other, this)
      ? NounLibrary.ShallowComparison.EQUAL
      : NounLibrary.ShallowComparison.NOT_EQUAL;
  }
}

