package de.scravy.primes;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.zip.GZIPInputStream;

public final class Primes extends AbstractList<Integer> implements IntFunction<Integer>, RandomAccess {

  private static long isqrt(final long n) {
    long sqrt = (long) Math.sqrt(n);
    long square = sqrt * sqrt;
    if (n < square) {
      return sqrt - 1;
    }
    return sqrt;
  }

  private static int isqrt(final int n) {
    return (int) Math.sqrt(n);
  }

  public static class InitializationException extends RuntimeException {
    private InitializationException(final Throwable cause) {
      super(cause);
    }
  }

  public static Primes load() {
    return new Primes();
  }

  public static Primes load(final int howMany) {
    return new Primes(howMany);
  }

  public static long[] generate(final int howMany, final LongConsumer handler) {

    final long[] ps = new long[howMany];

    ps[0] = 2L;
    int px = 1;
    handler.accept(ps[0]);
    long r = 1L;

    loop:
    while (px < ps.length) {
      r += 2L;
      final long s = isqrt(r);
      for (int i = 0; i < px && ps[i] <= s; i += 1) {
        final long m = r % ps[i];
        if (m == 0) {
          continue loop;
        }
      }
      ps[px] = r;
      px += 1;
      handler.accept(r);
    }
    return ps;
  }

  public static int[] generate(final int howMany, final IntConsumer handler) {

    final int[] ps = new int[howMany];

    ps[0] = 2;
    int px = 1;
    handler.accept(ps[0]);
    int r = 1;

    loop:
    while (px < ps.length) {
      r += 2;
      final int s = isqrt(r);
      for (int i = 0; i < px && ps[i] <= s; i += 1) {
        final int m = r % ps[i];
        if (m == 0) {
          continue loop;
        }
      }
      ps[px] = r;
      px += 1;
      handler.accept(r);
    }
    return ps;
  }

  private final int[] primes;

  private Primes() {
    this(105097565);
  }

  private Primes(final int howMany) {
    try {
      primes = new int[howMany];
      try (final InputStream is = getClass().getResourceAsStream("primes.gz");
           final GZIPInputStream zs = new GZIPInputStream(is);
           final DataInputStream ds = new DataInputStream(zs)) {
        for (int i = 0; i < howMany; i += 1) {
          primes[i] = ds.readInt();
        }
      }
    } catch (final Throwable exc) {
      throw new InitializationException(exc);
    }
  }

  @Override
  public int size() {
    return primes.length;
  }

  public int[] getUnderlyingArray() {
    return primes;
  }

  @Override
  public Integer get(final int index) {
    return primes[index];
  }

  @Override
  public Integer apply(final int value) {
    return get(value);
  }

  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<Integer>() {
      int i = 0;

      @Override
      public boolean hasNext() {
        return i < primes.length;
      }

      @Override
      public Integer next() {
        return primes[i++];
      }
    };
  }

  public boolean isPrime(final int n) {
    return Arrays.binarySearch(primes, n) >= 0;
  }

  @Override
  public boolean contains(final Object n) {
    if (!(n instanceof Integer)) {
      return false;
    }
    return Arrays.binarySearch(primes, (Integer) n) >= 0;
  }

  public void getPrimeFactors(final int n, final IntConsumer handler) {
    int r = n;
    int i = 0;
    while (r > 1 && i < primes.length) {
      if (r % primes[i] == 0) {
        handler.accept(primes[i]);
        r = r / primes[i];
      } else {
        i += 1;
      }
    }
  }

  public List<Integer> getPrimeFactors(final int n) {
    final ArrayList<Integer> factors = new ArrayList<>();
    getPrimeFactors(n, factors::add);
    return factors;
  }

  public NavigableSet<Integer> getPrimeFactorsSet(final int n) {
    final TreeSet<Integer> factors = new TreeSet<>();
    getPrimeFactors(n, factors::add);
    return factors;
  }

}
