package net.frodwith.jaque.nodes.jet.ut;

import java.util.Objects;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.SlotNode;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.exception.NockException;

@NodeChildren({
    @NodeChild(value="vet", type=SlotNode.class),
    @NodeChild(value="sut", type=SlotNode.class),
    @NodeChild(value="ref", type=SlotNode.class),
})
public abstract class CropKeyNode extends DecapitationKeyNode {

  private static final class CropKey {
    private final boolean vet;
    private final Object sut, ref;

    public CropKey(boolean vet, Object sut, Object ref) {
      this.vet = vet;
      this.sut = sut;
      this.ref = ref;
    }

    public boolean equals(Object other) {
      if ( !(other instanceof CropKey) ) {
        return false;
      }
      CropKey k = (CropKey) other;
      return vet == k.vet
        && Equality.equals(sut, k.sut)
        && Equality.equals(ref, k.ref);
    }

    public int hashCode() {
      return Objects.hash(vet, sut, ref);
    }
  }

  @Specialization
  protected Object crop(long lvet,  // Pull vet out of what was van.
                        Object sut,
                        Object ref) // u3x_sam
  {
    boolean vet;

    if ( 0L == lvet ) {
      vet = true;
    }
    else if ( 1L == lvet ) {
      vet = false;
    }
    else {
      throw new NockException("bad crop vet", this);
    }

    return new CropKey(vet, sut, ref);
  }

  @Fallback
  protected Object fail(Object vet, Object sut, Object ref) {
    throw new NockException("bad crop key", this);
  }
}
