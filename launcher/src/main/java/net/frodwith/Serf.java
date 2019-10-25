package net.frodwith.jaque;

import net.frodwith.jaque.NockLanguage;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;

/**
 * Hello world!
 *
 */
public class Serf
{
  public static void main( String[] args )
  {
    Context context = Context.newBuilder()
                      .allowPolyglotAccess(PolyglotAccess.ALL)
                      .build();

    context.initialize("nock");

    System.out.println( "Hello mime type, " + NockLanguage.MIME_TYPE );
  }
}
