package net.frodwith.jaque.data;

import java.util.ArrayDeque;

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

  public AxisMap<T> insert(Axis index, T item) {
    ArrayDeque<AxisMap<T>> maps = new ArrayDeque<>();
    ArrayDeque<Axis.Fragment> frags = new ArrayDeque<>();
    AxisMap<T> cur = this;
    for ( Axis.Fragment f : index ) {
      maps.push(cur);
      frags.push(f);
      cur = (Axis.Fragment.HEAD == f) ? cur.left : cur.right;
      if ( null == cur ) {
        cur = EMPTY;
      }
    }
    cur = new AxisMap<T>(item, cur.left, cur.right);
    while (!frags.isEmpty()) {
      AxisMap<T> parent = maps.pop();
      cur = (Axis.Fragment.HEAD == frags.pop())
        ? new AxisMap<T>(parent.value, cur, parent.right)
        : new AxisMap<T>(parent.value, parent.left, cur);
    }
    return cur;
  }

  public T get(Axis index) {
    AxisMap<T> cur = this;
    for ( Axis.Fragment f : index ) {
      if ( Axis.Fragment.HEAD == f ) {
        if ( null == cur.left ) {
          return null;
        }
        else {
          cur = cur.left;
        }
      }
      else if ( null == cur.right ) {
        return null;
      }
      else {
        cur = cur.right;
      }
    }
    return cur.value;
  }
}
