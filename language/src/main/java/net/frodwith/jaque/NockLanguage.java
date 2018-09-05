package net.frodwith.jaque;

import com.oracle.truffle.api.TruffleLanguage;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.runtime.NockFunction;

@TruffleLanguage.Registration(id = NockLanguage.ID, 
                              name = "nock",
                              mimeType = NockLanguage.MIME_TYPE)
public final class NockLanguage extends TruffleLanguage<NockContext> {
  public static final String ID = "nock";
  public static final String MIME_TYPE = "application/x-nock";

  @Override
  public boolean isObjectOfLanguage(Object o) {
    return o instanceof NockFunction
      || o instanceof Cell
      || o instanceof BigAtom
      || o instanceof Long;
  }
}
