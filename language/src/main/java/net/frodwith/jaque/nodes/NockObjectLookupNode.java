package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.exception.Bail;
import net.frodwith.jaque.exception.Fail;
import net.frodwith.jaque.runtime.NockFunction;

public abstract class NockObjectLookupNode extends NockLookupNode {
  public abstract NockObject executeLookup();

  @Specialization(limit = "1",
                  guards = "sameCells(cachedCore, core)")
  protected NockFunction doStatic(Cell core,
    @Cached("cachedCore") Cell cachedCore,
    @Cached("locate(core)") LocationResult result,
    @Cached("createObject(cachedCore, result)") NockObject object) {
    return object;
  }

  @Specialization(limit = "INLINE_CACHE_SIZE",
                  guards = "fine(result, core)",
                  replaces = "doStatic")
  protected NockObject doFast(Cell core,
    @Cached("locate(core)") LocationResult result,
    return createObject(core, result);
  }

  @Specialization(replaces = "doFast")
  protected NockFunction doSlow(Cell core) {
    return createObject(core, locate(core));
  }

  protected static NockObject createObject(Cell core, LocationResult result) {
    return new NockObject(core, result.location);
  }

  protected static boolean fine(LocationResult result, Cell core) {
    return result.check(core);
  }

  @TruffleBoundary
  protected LocationResult locate(Cell core) {
    return getContextReference().get().locateCore(core);
  }
  
  @Fallback
  protected NockFunction doAtom(Object atom) {
    throw new Bail("atom not core", this);
  }
}
