package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.NockObject;
import net.frodwith.jaque.exception.Bail;
import net.frodwith.jaque.exception.Fail;
import net.frodwith.jaque.runtime.NockFunction;
import net.frodwith.jaque.runtime.Equality;

@NodeChild(value="coreNode", type=NockExpressionNode.class)
public abstract class NockObjectLookupNode extends NockLookupNode {
  public abstract NockObject executeLookup(VirtualFrame frame);

  @Specialization(limit = "1",
                  guards = "sameCells(cachedObject.cell, core)",
                  assumptions = "cachedObject.valid")
  protected NockObject doStatic(Cell core,
    @Cached("getObject(core)") NockObject cachedObject) {
    return cachedObject;
  }

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "check(fine, core)",
                  assumptions = "cachedObject.valid",
                  replaces = "doStatic")
  protected NockObject doFine(Cell core,
    @Cached("getObject(core)") NockObject cachedObject,
    @Cached("cachedObject.createFine()") NockObject.Fine fine) {
    return cachedObject.like(core);
  }

  @Specialization(replaces = "doFine")
  protected NockObject doSlow(Cell core) {
    return getObject(core);
  }

  @Fallback
  protected NockObject doAtom(Object atom) {
    throw new Bail("atom not core", this);
  }

  protected NockObject getObject(Cell core) {
    return core.getMeta().getObject(getContextReference().get().dashboard);
  }

  protected static boolean check(NockObject.Fine fine, Cell core) {
    return fine.check(core);
  }

  protected static boolean sameCells(Cell a, Cell b) {
    return Equality.equals(a,b);
  }
}
