package net.frodwith.jaque.nodes.op;

import java.io.StringWriter;
import java.io.IOException;

import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.CompilerDirectives;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.printer.MappedNounPrinter;

public final class InitialRegisterOpNode extends RegisterOpNode {

  private final static TruffleLogger LOG =
    TruffleLogger.getLogger(NockLanguage.ID, InitialRegisterOpNode.class);

  public InitialRegisterOpNode(Dashboard dashboard) {
    super(dashboard);
  }

  @Override
  public void executeRegister(Object core, Object clue) {
    try {
      Cell cc     = Cell.require(core);
      FastClue fc = FastClue.parse(clue);
      register(cc, fc);
      StaticRegisterOpNode stat = new StaticRegisterOpNode(cc, fc, dashboard);
      CompilerDirectives.transferToInterpreter();
      replace(stat);
    }
    catch ( ExitException exit ) {
      StringWriter out = new StringWriter();
      try {
        MappedNounPrinter.print(out, clue);
        LOG.warning("Invalid clue: " + out.toString());
      }
      catch ( ExitException | IOException e ) {
        LOG.severe("noun print error");
      }
    }
  }
}
