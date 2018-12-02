package net.frodwith.jaque.runtime;

import java.util.Arrays;

import com.oracle.truffle.api.CompilerDirectives;

import gnu.math.MPN;

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.exception.ExitException;

public final class Atom {
	public static final boolean BIG_ENDIAN = true;
	public static final boolean LITTLE_ENDIAN = false;

	public static int compare(BigAtom a, BigAtom b) {
		return MPN.cmp(a.words, a.words.length, b.words, b.words.length);
	}

	public static int compare(long a, long b) {
		return Long.compareUnsigned(a, b);
	}

	// -1, 0, 1 for less than, equal, or greater than respectively
	public static int compare(Object a, Object b) {
		if ( a instanceof Long ) {
			if ( b instanceof Long ) {
				return compare((long) a, (long) b);
			}
			else {
				return -1;
			}
		}
		else if ( b instanceof Long ) {
			return 1;
		}
		else {
			return compare((BigAtom) a, (BigAtom) b);
		}
	}

	public static Object require(Object o) throws ExitException {
		if ( o instanceof Long || o instanceof BigAtom ) {
			return o;
		}
		else {
			CompilerDirectives.transferToInterpreter();
			throw new ExitException("atom required");
		}
	}

	// IMPORTANT: you may in fact want to FAIL unless you get an int,
	//            this is EXIT unless given an int.
	public static int requireInt(Object o) throws ExitException {
		if ( o instanceof Long ) {
			long atom = (long) o;
			if ( 1 != Long.compareUnsigned(atom, 0xFFFFFFFF) ) {
				return (int) atom;
			}
		}
		CompilerDirectives.transferToInterpreter();
		throw new ExitException("expected int");
	}

	public static int[] words(long l) {
		int low  = (int) l,
				high = (int) (l >>> 32);

		return ( high == 0 )
			? new int[] { low }
		: new int[] { low, high };
	}

	public static int[] words(BigAtom a) {
		return a.words;
	}

	public static int[] words(Object o) {
		return ( o instanceof BigAtom ) 
			? words((BigAtom) o)
			: words((long) o);
	}

	public static Object malt(int[] words) {
		int bad = 0;

		for ( int i = words.length - 1; i >= 0; --i) {
			if ( words[i] == 0 ) {
				++bad;
			}
			else {
				break;
			}
		}

		if ( bad > 0 ) {
			words = Arrays.copyOfRange(words, 0, words.length - bad);
		}

		if ( 0 == words.length ) {
			return 0L;
		}
		else if ( words != null && words.length > 2 ) {
			return new BigAtom(words);
		}
		else if (words.length == 1) {
			return words[0] & 0xffffffffL;
		}
		else {
			return ((words[1] & 0xffffffffL) << 32) | (words[0] & 0xffffffffL);
		}
	}

	public static int[] slaq(byte bloq, int len) {
		int big = ((len << bloq) + 31) >>> 5;
		return new int[big];
	}

	public static void chop(byte met, int fum, int wid, int tou, int[] dst, Object src) {
		int[] buf = words(src);
		int   len = buf.length, i;

		if (met < 5) {
			int san = 1 << met,
					mek = ((1 << san) - 1),
					baf = fum << met,
					bat = tou << met;

			for (i = 0; i < wid; ++i) {
				int waf = baf >>> 5,
						raf = baf & 31,
						wat = bat >>> 5,
						rat = bat & 31,
						hop;

				hop = (waf >= len) ? 0 : buf[waf];
				hop = (hop >>> raf) & mek;
				dst[wat] ^= hop << rat;
				baf += san;
				bat += san;
			}
		}
		else {
			int hut = met - 5,
					san = 1 << hut,
					j;

			for (i = 0; i < wid; ++i) {
				int wuf = (fum + i) << hut,
						wut = (tou + i) << hut;

				for (j = 0; j < san; ++j) {
					dst[wut + j] ^= ((wuf + j) >= len)
						? 0
						: buf[wuf + j];
				}
			}
		}
	}

	public static boolean getNthBit(long atom, int n) {
		if ( n >= (Long.SIZE - 1) ) {
			return false;
		}
		else {
			return ((atom & (1L << n)) != 0);
		}
	}

	public static boolean getNthBit(Object atom, int n) {
		if ( atom instanceof Long ) {
			return getNthBit((long) atom, n);
		}
		else {
			return getNthBit((BigAtom) atom, n);
		}
	}

	public static boolean getNthBit(BigAtom atom, int n) {
		int pix = n >> 5;

		if ( pix >= atom.words.length ) {
			return false;
		}
		else {
			return (1 & (atom.words[pix] >>> (n & 31))) != 0;
		}
	}

	public static Object fromByteArray(byte[] pill) {
		return fromByteArray(pill, LITTLE_ENDIAN);
	}

	/* IN-PLACE */
	private static byte[] reverse(byte[] a) {
		int i, j;
		byte b;
		for (i = 0, j = a.length - 1; j > i; ++i, --j) {
			b = a[i];
			a[i] = a[j];
			a[j] = b;
		}
		return a;
	}

	public static Object fromByteArray(byte[] pill, boolean endian) {
		int len  = pill.length;
		int trim = len % 4;

		if (endian == BIG_ENDIAN) {
			pill = Arrays.copyOf(pill, len);
			reverse(pill);
		}

		if (trim > 0) {
			int    nlen = len + (4-trim);
			byte[] npil = new byte[nlen];
			System.arraycopy(pill, 0, npil, 0, len);
			pill = npil;
			len = nlen;
		}

		int   size  = len / 4;
		int[] words = new int[size];
		int i, b, w;
		for (i = 0, b = 0; i < size; ++i) {
			w =  (pill[b++] << 0)  & 0x000000FF;
			w ^= (pill[b++] << 8)  & 0x0000FF00;
			w ^= (pill[b++] << 16) & 0x00FF0000;
			w ^= (pill[b++] << 24) & 0xFF000000;
			words[i] = w;
		}

		return malt(words);
	}

	public static byte[] toByteArray(Object a) {
		return toByteArray(a, LITTLE_ENDIAN);
	}

  public static byte[] wordsToBytes(int[] wor, int bel, boolean endian) {
    int    w, i, b;
    byte[] buf = new byte[bel];
    for (i = 0, b = 0;;) {
      w = wor[i++];

      buf[b++] = (byte) ((w & 0x000000FF) >>> 0);
      if (b >= bel) break;

      buf[b++] = (byte) ((w & 0x0000FF00) >>> 8);
      if (b >= bel) break;

      buf[b++] = (byte) ((w & 0x00FF0000) >>> 16);
      if (b >= bel) break;

      buf[b++] = (byte) ((w & 0xFF000000) >>> 24);
      if (b >= bel) break;
    }
    if (endian == BIG_ENDIAN) {
      reverse(buf);
    }
    return buf;
  }

  public static byte[] wordsToBytes(int[] wor, int bel) {
    return wordsToBytes(wor, bel, LITTLE_ENDIAN);
  }

  public static byte[] toByteArray(Object atom, boolean endian) {
    if ( (atom instanceof Long) && (0L == (long)atom) ) {
      return new byte[1];
    }
    return wordsToBytes(words(atom), HoonMath.met((byte)3, atom), endian);
  }
}
