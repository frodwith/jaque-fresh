package net.frodwith.jaque.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public final class NockMain {
  public static void main(String[] args) throws IOException {
    Source source = Source.newBuilder("nock", new InputStreamReader(System.in), "<stdin>").build();
    Context context;
    
    try {

      context = Context.newBuilder("nock").in(System.in).out(System.out).build();

    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      System.exit(1);
      return;
    }

    try {
      Value simple = context.eval(source);
  
      if (!simple.isNull()) {
        // Cell subFormula = new Cell(0L, 2L);
        // Cell subSubject = new Cell(42L, 0L);
        // Cell subject    = new Cell(subFormula, subSubject);
        System.out.println(simple.execute(0L));
      }

    } catch (PolyglotException ex) {
      if (ex.isInternalError()) {
          // for internal errors we print the full stack trace
          ex.printStackTrace();
      } else {
          System.err.println(ex.getMessage());
      }
      System.exit(1);
    } finally {
        context.close();
    }
    System.exit(0);
  }
}
