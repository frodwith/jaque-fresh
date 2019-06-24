package net.frodwith.jaque.data;

import java.util.ArrayDeque;
import java.util.function.Function;

/* Immutable Map indexed by Axis with T values.
 * Immutable to aid partial evaluation, indexed by Axis to facilitate
 * storing/retrieving information about things like arms in batteries.
 * algorithms use explicit stacks rather than recursion, again to aid
 * partial evaluation.
*/
public final class AxisMap<T> {
  private final AxisMap<T> left, right;
  private final T value;
  public static final AxisMap EMPTY = new AxisMap(null, null, null);

  private AxisMap(T value, AxisMap<T> left, AxisMap<T> right) {
    this.value = value;
    this.left = left;
    this.right = right;
  }

  public AxisMap<T> insert(Iterable<Boolean> path, T item) {
    ArrayDeque<AxisMap<T>> maps = new ArrayDeque<>();
    ArrayDeque<Boolean> parts = new ArrayDeque<>();
    AxisMap<T> cur = this;
    for ( boolean right : path ) {
      maps.push(cur);
      parts.push(right);
      cur = right ? cur.right : cur.left;
      if ( null == cur ) {
        cur = EMPTY;
      }
    }
    cur = new AxisMap<T>(item, cur.left, cur.right);
    while (!parts.isEmpty()) {
      AxisMap<T> parent = maps.pop();
      cur = parts.pop()
        ? new AxisMap<T>(parent.value, parent.left, cur)
        : new AxisMap<T>(parent.value, cur, parent.right);
    }
    return cur;
  }

  public <U> AxisMap<U> transform(Function<T,U> f) {
    if ( this == EMPTY ) {
      return EMPTY;
    }
    else {
      final class Frame {
        public int state;
        public AxisMap<T> node;
        public AxisMap<U> left;

        public Frame(AxisMap<T> node) {
          this.state = 0;
          this.node = node;
          this.left = null;
        }
      }
      AxisMap<U> ret = null;
      U val;
      Frame top = new Frame(this);
      ArrayDeque<Frame> stack = new ArrayDeque<>();
      stack.push(top);
      while ( true ) {
        switch ( top.state ) {
          case 0:
            ++top.state;
            if ( top.node.left == null ) {
              ret = null;
              // fall through
            }
            else {
              stack.push(top = new Frame(top.node.left));
              break;
            }

          case 1:
            ++top.state;
            top.left = ret;
            if ( top.node.right == null ) {
              ret = null;
              // fall through
            }
            else {
              stack.push(top = new Frame(top.node.right));
              break;
            }

          case 2:
            val = (null == top.node.value) ? null : f.apply(top.node.value);
            ret = new AxisMap<U>(val, top.left, ret);
            stack.pop();
            if ( stack.isEmpty() ) {
              return ret;
            }
            else {
              top = stack.peek();
              break;
            }
        }
      }
    }
  }

  public T get(Iterable<Boolean> path) {
    AxisMap<T> cur = this;
    for ( boolean right : path ) {
      if ( right ) {
        if ( null == cur.right ) {
          return null;
        }
        else {
          cur = cur.right;
        }
      }
      else if ( null == cur.left ) {
        return null;
      }
      else {
        cur = cur.left;
      }
    }
    return cur.value;
  }
}
