package net.frodwith.jaque.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.AxisMap;

public class AxisMapTest {

  @Test
  public void testBasic() {
    Axis beg = Axis.HEAD,
         odd = Axis.TAIL,
         mid = Axis.SAMPLE,
         end = Axis.CONTEXT;

    AxisMap<String> trel = AxisMap.EMPTY;
    trel = trel.insert(beg, "beginning")
      .insert(mid, "middle")
      .insert(end, "end");

    assertEquals("beginning", trel.get(beg));
    assertEquals("middle", trel.get(mid));
    assertEquals("end", trel.get(end));
    assertEquals(null, trel.get(odd));

    trel = trel.insert(odd, "odd")
      .insert(end, "wrong");

    assertEquals("beginning", trel.get(beg));
    assertEquals("middle", trel.get(mid));
    assertEquals("wrong", trel.get(end));
    assertEquals("odd", trel.get(odd));
  }
}
