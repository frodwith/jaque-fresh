package net.frodwith.jaque;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.HoonSerial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ArrayIndexOutOfBoundsException;
import java.lang.UnsupportedOperationException;
import java.util.Set;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;

class NounShapeException extends Exception {
  public NounShapeException(String msg) {
    super(msg);
  }

  public NounShapeException(String msg, Throwable cause) {
    super(msg, cause);
  }
}

/**
 * Subordinate Nock interpreter that's connected to a separate daemon control
 * process.
 */
public class Serf
{
  // Mote definitions:
  //
  public final static long C3__PLAY = 2_036_427_888L;
  public final static long C3__BOOT = 1_953_460_066L;
  public final static long C3__WORK = 1_802_661_751L;
  public final static long C3__DONE = 1_701_736_292L;
  public final static long C3__STDR = 1_919_186_035L;
  public final static long C3__SLOG = 1_735_355_507L;
  public final static long C3__HEAR = 1_918_985_576L;
  public final static long C3__EXIT = 1_953_069_157L;
  public final static long C3__HOLE = 1_701_605_224L;
  public final static long C3__CRUD = 1_685_418_595L;
  public final static long C3__LEAF = 1_717_658_988L;
  public final static long C3__ARVO = 1_870_033_505L;
  public final static long C3__WARN = 1_852_989_815L;
  public final static long C3__MASS = 1_936_941_421L;
  public final static long C3__VEGA = 1_634_166_134L;
  public final static long C3__BELT = 1_953_260_898L;
  public final static long C3__TRIM = 1_835_627_124L;
  public final static long C3__SAVE = 1_702_257_011L;

  private final Context truffleContext;
  private final Value nockRuntime;

  private final DataInputStream inputStream;
  private final DataOutputStream outputStream;

  private Value who;
  private boolean isFake = false;
  private long bootSequenceLength = 0;

  public static void main( String[] args )
      throws IOException
  {
    if (args.length != 3) {
      System.err.println("serf must be started with three arguments.");
      System.exit(-1);
    }

    String pierDir = args[0];
    // args[1] (argv[2]) doesn't matter; it's the dead encryption key, and it's
    // good that its unused since it's passed over the command line and that's
    // visible to everyone.
    //
    // args[2] (argv[3]) doesn't matter for now, it's the packed interpreter
    // options thing, most of which will be ignored.

    Serf serf = new Serf();
    serf.run(pierDir);
  }

  public Serf() {
    this.truffleContext = Context
                        .newBuilder("nock")
                        .allowAllAccess(true)
                        .build();
    this.truffleContext.initialize("nock");
    this.nockRuntime = this.truffleContext
                     .getPolyglotBindings()
                     .getMember("nock");

    // Build data readers around System.{in,out} to read higher level binary
    // structures.
    this.inputStream = new DataInputStream(System.in);
    this.outputStream = new DataOutputStream(System.out);
    fixupFileDescriptors();
  }

  public void run(String pierDir) throws IOException {
    // TODO: load the pier directory and do the equivalent of u3m_boot().
    //
    System.err.println("About to send boot");

    sendBoot();
    System.err.println("Send boot");

    // Read until EOF
    try {
      boolean done = false;
      while (!done) {
        Value message = readNoun();

        long tag = getHeadTag(message);
        if (tag == C3__BOOT) {
          onBootMessage(message);
        } else if (tag == C3__WORK) {
          System.err.println("poke work");
        } else if (tag == C3__EXIT) {
          System.err.println("poke exit");
          // TODO: Exit immediately.
        } else if (tag == C3__SAVE) {
          System.err.println("poke save");
          // TODO: I have no idea at all.
        } else {
          // TODO: long -> String
          throw new NounShapeException("Invalid request tag: " + tag);
        }
      }
    } catch (NounShapeException e) {
      System.err.println("(noun shape exception; shutting down)");
    }
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

  private void onBootMessage(Value message)
      throws NounShapeException
  {
    try {
      Value tail = message.getArrayElement(1);
      Value who = tail.getArrayElement(0);
      tail = tail.getArrayElement(1);
      Value fake = tail.getArrayElement(0);
      Value bootSequenceLength = tail.getArrayElement(1);

      // When we boot up, we save these values.
      this.who = who;
      this.isFake = fake.asLong() == 0 ? true : false;

      // The boot sequence length is 1 with solid pills and 5 with brass pills.
      this.bootSequenceLength = bootSequenceLength.asLong();

      System.err.println("poke boot: who=" + this.who + ", isFake=" +
                         this.isFake + ", bootLen=" + this.bootSequenceLength);
    } catch (UnsupportedOperationException e) {
      throw new NounShapeException("Couldn't unpack boot message", e);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new NounShapeException("Couldn't unpack boot message", e);
    }
  }

  /**
   * Sends the initial [%play ~] atom to the king to specify that we're ready
   * for pleas.
   *
   * TODO: This hard codes sending `[%play ~]` instead of sending the state
   * after we've loaded a snapshot. We don't have snapshots yet.
   */
  private void sendBoot() throws IOException {
    writeNoun(nockRuntime.invokeMember("toNoun", C3__PLAY, 0L));
  }

  private void sendDone(long event, long mug, Value effects) throws IOException {
    writeNoun(nockRuntime.invokeMember("toNoun", C3__DONE, event, mug, effects));
  }

  /**
   * Fetches the head tag, assuming that v is a cell where the head is an atom
   * that will fit in a long.
   */
  private long getHeadTag(Value v) throws NounShapeException {
    try {
      Value head = v.getArrayElement(0);
      return head.asLong();
    } catch (UnsupportedOperationException e) {
      throw new NounShapeException("v doesn't appear to be a Cell", e);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new NounShapeException("Couldn't access head", e);
    }
  }

  /**
   * Reads an atom from the input stream and returns it.
   */
  private Value readNoun() throws IOException, EOFException {
    int length = (int)Long.reverseBytes(inputStream.readLong());
    byte[] bytes = new byte[length];
    inputStream.readFully(bytes, 0, length);

    Value atom = nockRuntime.invokeMember("fromBytes", bytes);
    return nockRuntime.invokeMember("cue", atom);
  }

  /**
   * Writes the length of the serialized atom bytes and the bytes themselves.
   */
  private void writeNoun(Value arbitraryNoun) throws IOException {
    Value jammedValue = nockRuntime.invokeMember("jam", arbitraryNoun);
    Value vBytes = nockRuntime.invokeMember("toBytes", jammedValue);
    byte[] bytes = vBytes.as(byte[].class);
    outputStream.writeLong(Long.reverseBytes(bytes.length));
    outputStream.write(bytes, 0, bytes.length);
    outputStream.flush();
  }
}
