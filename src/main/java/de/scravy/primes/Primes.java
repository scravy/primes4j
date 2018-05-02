package de.scravy.primes;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.zip.GZIPInputStream;

public final class Primes extends AbstractList<Integer> implements IntFunction<Integer>, RandomAccess {

  /**
   * The number of primes in the interval <code>[ 2 .. Integer.MAX_VALUE ]</code> (including 2 and
   * <code>Integer.MAX_VALUE</code> which itself is a prime number).
   */
  public static final int NUMBER_OF_PRIMES_UPTO_INT_MAX_VALUE = 105097565;

  /**
   * Integer square root of a long.
   * <p>
   * The result is guaranteed to be less than or equal to the actual square root,
   * and the result incremented by one is guaranteed to be greater than the actual
   * square root; i.e. the result is the actual square root rounded down.
   *
   * @param n The integer to take the square root of.
   * @return The square root of n rounded down.
   */
  public static long isqrt(final long n) {
    final long sqrt = (long) Math.sqrt(n);
    final long square = sqrt * sqrt;
    if (n < square) {
      return sqrt - 1;
    }
    return sqrt;
  }

  /**
   * Integer square root of an int.
   * <p>
   * The result is guaranteed to be less than or equal to the actual square root,
   * and the result incremented by one is guaranteed to be greater than the actual
   * square root; i.e. the result is the actual square root rounded down.
   *
   * @param n The integer to take the square root of.
   * @return The square root of n rounded down.
   */
  public static int isqrt(final int n) {
    return (int) Math.sqrt(n);
  }

  /**
   * Thrown if a Primes object can not be constructed, for instance because it
   * was attempted to load a given number of primes form a file but an IOException
   * occurred, or the file was malformed, yada yada yada.
   * <p>
   * Typically does not happen and indicates a setup of programming error,
   * thus it's a {@link RuntimeException}.
   */
  public static class InitializationException extends RuntimeException {
    private InitializationException(final Throwable cause) {
      super(cause);
    }
  }

  /**
   * Loads all the available primes that are shipped with this library.
   *
   * @return An instance of primes that holds all the primes in <code>[ 2 .. Integer.MAX_VALUE ]</code>
   */
  public static Primes load() {
    return load(NUMBER_OF_PRIMES_UPTO_INT_MAX_VALUE);
  }

  /**
   * Loads the specified amount of primes that are shipped with this library.
   *
   * @param howMany How many primes to load from the library resources.
   * @return
   */
  public static Primes load(final int howMany) {
    try {
      final int[] primes = new int[howMany];
      try (final InputStream is = Primes.class.getResourceAsStream("primes.gz");
           final BufferedInputStream bs = new BufferedInputStream(is);
           final GZIPInputStream zs = new GZIPInputStream(bs);
           final DataInputStream ds = new DataInputStream(zs)) {
        for (int i = 0; i < howMany; i += 1) {
          primes[i] = ds.readInt();
        }
      }
      return new Primes(primes);
    } catch (final Throwable exc) {
      throw new InitializationException(exc);
    }
  }

  /**
   * Generates all the primes upto the specified limit of <code>howMany</code>.
   *
   * @param howMany How many primes to generate.
   * @return An instance of primes that holds <code>howMany</code> primes starting from 2.
   * @throws IllegalArgumentException Throws an exception if <code>howMany</code> exceeds
   *                                  {@link Primes#NUMBER_OF_PRIMES_UPTO_INT_MAX_VALUE},
   *                                  as primes beyond this threshold exceed {@link Integer#MAX_VALUE}.
   */
  public static Primes generate(final int howMany) {
    if (howMany > NUMBER_OF_PRIMES_UPTO_INT_MAX_VALUE) {
      throw new IllegalArgumentException("howMany must be less than " + NUMBER_OF_PRIMES_UPTO_INT_MAX_VALUE);
    }
    return new Primes(generate(howMany, (IntConsumer) __ -> {
      // do not do anything as we are only interested in the result array
    }));
  }

  /**
   * Generates all the primes upto {@link Integer#MAX_VALUE} (i.e. all the primes that
   * fit into the <code>int</code> datatype.
   *
   * @return An instance of primes that holds all the primes that fit into <code>int</code>.
   */
  public static Primes generate() {
    return new Primes(generate(NUMBER_OF_PRIMES_UPTO_INT_MAX_VALUE, (IntConsumer) __ -> {
      // do not do anything as we are only interested in the result array
    }));
  }

  /**
   * Generates all the primes up to <code>howMany</code> (specialised for long).
   * <p>
   * This method uses am modified version of the sieve of eratosthenes to calculate the primes,
   * hence it allocates an int-array of howMany primes upfront.
   * <p>
   * The fully populated array is returned in the end, but every time a new prime number
   * is found the <code>handler</code> is invoked with that prime number (which is
   * useful to stream it info a file for example).
   *
   * @param howMany How many prime numbers to calculate.
   * @param handler A handler to invoke every time a prime number is found.
   * @return The array of prime numbers.
   */
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

  /**
   * Generates all the primes up to <code>howMany</code> (specialised for int).
   * <p>
   * This method uses am modified version of the sieve of eratosthenes to calculate the primes,
   * hence it allocates an int-array of howMany primes upfront.
   * <p>
   * The fully populated array is returned in the end, but every time a new prime number
   * is found the <code>handler</code> is invoked with that prime number (which is
   * useful to stream it info a file for example).
   *
   * @param howMany How many prime numbers to calculate.
   * @param handler A handler to invoke every time a prime number is found.
   * @return The array of prime numbers.
   * @throws IllegalArgumentException Throws an exception if <code>howMany</code> exceeds
   *                                  {@link Primes#NUMBER_OF_PRIMES_UPTO_INT_MAX_VALUE},
   *                                  as primes beyond this threshold exceed {@link Integer#MAX_VALUE}.
   */
  public static int[] generate(final int howMany, final IntConsumer handler) {
    if (howMany > NUMBER_OF_PRIMES_UPTO_INT_MAX_VALUE) {
      throw new IllegalArgumentException("howMany must be less than " + NUMBER_OF_PRIMES_UPTO_INT_MAX_VALUE);
    }

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

  private Primes(final int[] primes) {
    this.primes = primes;
  }

  /**
   * Returns the number of primes in this instance.
   *
   * @return The number of primes in this instance.
   */
  @Override
  public int size() {
    return primes.length;
  }

  /**
   * Exposes the underlying array.
   * <p>
   * This array is mutable, do not touch it.
   *
   * @return The underlying array.
   */
  public int[] getUnderlyingArray() {
    return primes;
  }

  /**
   * Returns the nth prime, where the prime with index 0 is 2.
   *
   * @param index The index, starting at zero. The 0th prime is 2.
   * @return The nth prime.
   */
  @Override
  public Integer get(final int index) {
    if (index < 0 || index >= size()) {
      throw new IllegalArgumentException(index + " is out of range for index: [ 0 .. " + (size() - 1) + " ]");
    }
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

  /**
   * Checks whether the given int is prime, by checking whether it is contained
   * in the list of primes in this instance.
   * <p>
   * Note: If you loaded less primes than there are in the range of values for int, then this method might
   * return false for an integer that actually is prime; simply as it was not loaded.
   *
   * @param n The integer to check.
   * @return Whether the integer is prime or not.
   */
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

  /**
   * Enumerates the prime factors of a given integer <code>n</code>.
   *
   * @param n       The integer to compute the prime factors for.
   * @param handler A callback handling each prime factor found.
   */
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
