package net.frodwith.jaque;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.HoonSerial;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.PrintStream;

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

  private final Context truffleContext;
  private final Value nockRuntime;

  private final DataInputStream inputStream;
  private final DataOutputStream outputStream;

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

    sendWorkerBoot();
    System.err.println("Send boot");

    Value v = readAtom();
    System.err.println(v.toString());
    Value cued = nockRuntime.invokeMember("cue", v);
    System.err.println(cued.toString());

    System.err.flush();

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

  /**
   * Sends the initial [%play ~] atom to the king to specify that we're ready
   * for pleas.
   *
   * TODO: This hard codes sending `[%play ~]` instead of sending the state
   * after we've loaded a snapshot. We don't have snapshots yet.
   */
  private void sendWorkerBoot() throws IOException {
    Value v = nockRuntime.invokeMember("toNoun", C3__PLAY, 0L);
    Value jammed = nockRuntime.invokeMember("jam", v);
    writeAtom(jammed);
  }

  /**
   * Reads an atom from the input stream and returns it.
   */
  private Value readAtom() throws IOException, EOFException {
    int length = (int)Long.reverseBytes(inputStream.readLong());
    System.err.println("Read length " + length);
    byte[] bytes = new byte[length];
    inputStream.readFully(bytes, 0, length);

    return nockRuntime.invokeMember("fromBytes", bytes);
  }

  /**
   * Writes the length of the serialized atom bytes and the bytes themselves.
   */
  private void writeAtom(Value jammedValue) throws IOException {
    Value vBytes = nockRuntime.invokeMember("toBytes", jammedValue);
    byte[] bytes = vBytes.as(byte[].class);
    System.err.println("Writing bytes of length " + bytes.length + " to stream");
    outputStream.writeLong(Long.reverseBytes(bytes.length));
    outputStream.write(bytes, 0, bytes.length);
    outputStream.flush();
  }
}
