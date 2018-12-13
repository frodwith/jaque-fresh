package net.frodwith.jaque.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

public final class InitialRegistrationNode extends RegistrationNode {

  public InitialRegistrationNode(
      ContextReference<NockContext> contextReference) {
    super(contextReference);
  }

  protected Object executeRegister(Object core, Object clue) {
    try {
      Cell cc     = Cell.require(core);
      FastClue fc = FastClue.parse(clue);
      register(cc, fc);
      StaticRegistrationNode stat 
        = new StaticRegistrationNode(cc, fc, contextReference);
      CompilerDirectives.transferToInterpreter();
      replace(stat);
    }
    catch ( ExitException e ) {
    }
    return core;
  }
}
