package net.frodwith.jaque.test.quickcheck;

import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;

import net.frodwith.jaque.runtime.Atom;

public final class AtomGenerator extends Generator<Object> {
  public AtomGenerator() {
    super(Object.class);
  }

  @Override
  public Object generate(SourceOfRandomness random,
                         GenerationStatus status) {
    if ( random.nextBoolean() ) {
      // byte sized atoms
      return (long) random.nextByte(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
    else if ( random.nextBoolean() ) {
      // direct atoms
      return random.nextLong();
    }
    else {
      // 9 bytes of randomness (USUALLY a bigatom)
      return Atom.fromByteArray(random.nextBytes(9));
    }
  }
}
