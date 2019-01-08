package net.frodwith.jaque.test.quickcheck;

import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;

import net.frodwith.jaque.data.Cell;

public class NounGenerator extends Generator<Object> {
  private static GenerationStatus.Key<Integer> depth
    = new GenerationStatus.Key("depth", Integer.class);

  public NounGenerator() {
    super(Object.class);
  }

  @Override
  public Object generate(SourceOfRandomness random,
                         GenerationStatus status) {
    int c = status.valueOf(depth).orElse(0);
    if ( c > 10 || random.nextBoolean() ) {
      return gen().make(AtomGenerator.class).generate(random, status);
    }
    else {
      status.setValue(depth, c+1);
      Cell r = gen().make(CellGenerator.class).generate(random, status);
      status.setValue(depth, c);
      return r;
    }
  }
}
