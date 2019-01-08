package net.frodwith.jaque.test.quickcheck;

import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;

import net.frodwith.jaque.data.Cell;

public final class CellGenerator extends Generator<Cell> {
  public CellGenerator() {
    super(Cell.class);
  }

  @Override
  public Cell generate(SourceOfRandomness random,
                       GenerationStatus status) {
    Generator noun = gen().make(NounGenerator.class);
    Object head = noun.generate(random, status);
    Object tail = noun.generate(random, status);
    return new Cell(head, tail);
  }
}
