package net.frodwith.jaque.nodes;

import java.util.ArrayDeque;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.library.NounLibrary;
import net.frodwith.jaque.exception.NockException;
import net.frodwith.jaque.exception.ExitException;

public final class FragmentNode extends Node {
  @Children private final FragmentPartNode[] parts;

  private static abstract class FragmentPartNode extends Node {
    public abstract Object executePart(Object noun) throws ExitException;
  }

  static abstract class HeadNode extends FragmentPartNode {
    @Specialization(limit="3") protected Object 
      doHead(Object noun, @CachedLibrary("noun") NounLibrary nouns) 
        throws ExitException {
      return nouns.head(noun);
    }
  }

  static abstract class TailNode extends FragmentPartNode {
    @Specialization(limit="3") protected Object 
      doTail(Object noun, @CachedLibrary("noun") NounLibrary nouns) 
        throws ExitException {
      return nouns.tail(noun);
    }
  }

  private FragmentNode(FragmentPartNode[] parts) {
    this.parts = parts;
  }

  @ExplodeLoop
  public Object executeFragment(Object o) {
    try {
      for ( FragmentPartNode node : parts ) {
        o = node.executePart(o);
      }
    }
    catch ( ExitException e ) {
      CompilerDirectives.transferToInterpreter();
      throw new NockException("fragment", e, this);
    }

    return o;
  }

  public static FragmentNode fromBits(boolean[] bits) {
    FragmentPartNode[] parts = new FragmentPartNode[bits.length-1];
    for ( int part = 0, int bit = bits.length - 1; bit > 0; --bit, ++part ) {
      parts[part] = bits[bit] 
                  ? FragmentNodeFactory.TailNodeGen.create()
                  : FragmentNodeFactory.HeadNodeGen.create();
    }
    return new FragmentNode(parts);
  }
   
  public static FragmentNode fromAxis(Axis a) {
    ArrayDeque<FragmentPartNode> tmp = new ArrayDeque<>();
    for ( Axis.Fragment f : a ) {
      FragmentPartNode node = ( f == Axis.Fragment.HEAD )
        ? FragmentNodeFactory.HeadNodeGen.create()
        : FragmentNodeFactory.TailNodeGen.create();
      tmp.add(node);
    }
    return new FragmentNode(tmp.toArray(new FragmentPartNode[tmp.size()]));
  }
}
