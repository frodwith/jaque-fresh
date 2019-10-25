package net.frodwith.jaque;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.HoonSerial;

import java.io.InputStream;
import java.io.OutputStream;

import java.util.Set;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;

/**
 * Subordinate Nock interpreter that's connected to a separate daemon control
 * process.
 */
public class Serf
{
  public static long C3__PLAY = 2_036_427_888L;

  public static void main( String[] args )
  {
    if (args.length != 4) {
      System.out.println("serf must be started with four arguments.");
      System.exit(-1);
    }

    fixupFileDescriptors();

    String pierDir = args[0];
    // args[1] (argv[2]) doesn't matter; it's the dead encryption key, and it's
    // good that its unused since it's passed over the command line and that's
    // visible to everyone.
    //
    // args[2] (argv[3]) doesn't matter for now, it's the packed interpreter
    // options thing, most of which will be ignored.

    // OK, so actually we can't access any of the internals of a truffle
    // language from here and must purely use the Context api. Great.

    Context context = Context
                      .newBuilder("nock")
                      .allowAllAccess(true)
                      .build();
    context.initialize("nock");

    Value nock = context
                 .getPolyglotBindings()
                 .getMember("nock");

    Value v = nock.invokeMember("toNoun", C3__PLAY, 0L);
    System.out.println(v.toString());

    Value jammed = nock.invokeMember("jam", v);
    System.out.println(jammed.toString());

    // TODO: During boot, we send a play event back to the king.
  }

  /**
   * In the C urbit-worker, we do a bunch of stuff with dup() and file
   * descriptors to ensure that unrelated pieces of the program don't
   * read/write from stdin/stdout. As a aserf, stdin is a binary stream of
   * serialized events and stdout is a binary stream of . To make sure uses
   * of System.out don't interfere with this, we set
   */
  private static void fixupFileDescriptors() {
    // In JDK11, we could do the following. But graal everything is JDK8.
    //
    // System.setIn(InputStream.nullInputStream());
    // System.setOut(OutputStream.nullOutputStream());

    // TODO: I'm punting on this for now. We (mostly) worked for a long time
    // before we fixed up the filedescriptors.
  }

  //  private static void workerSend(

  // private static Dashboard makeDashboard(boolean fast, boolean hash) {

  // }

  // private static Value poke(Value gate, Value ovo) {

  // }
}
