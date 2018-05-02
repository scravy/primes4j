# primes4j

This package has a list of all the prime numbers up to `Integer.MAX_VALUE` (2147483647)
which is itself a prime number. This list contains 105097565 entries. The list consists
of a sequence of integers each stored as 4 bytes, thus it is 420390260 bytes in memory.

On top it offers a tiny API to load this list (complete or partially) and for example
find the prime factors for a number or check whether a given `int` is prime or not.

This project is mostly useful for playing around with number theory related problems
which can be computed within the realm of `int` values.

## Maven Coordinates

```
<dependency>
  <groupId>de.scravy</groupId>
  <artifactId>primes4j</artifactId>
  <version>2</version>
</dependency>
```

## Usage Example (1)

```
import de.scravy.primes.Primes;

public class Example1 {
  public static void main(final String[] args) {
    final Primes primes = Primes.load(10000); // only loads 10000 primes which is faster
    System.out.println(primes.getPrimeFactors(2868)); // prints [2, 2, 3, 239]
  }
}

```

## Usage Example (2)

```
import de.scravy.primes.Primes;

public class Example {
  public static void main(final String[] args) {
    final Primes primes = Primes.load(); // takes a while as it loads all primes into memory
    System.out.println(primes.isPrime(Integer.MAX_VALUE)); // prints true
  }
}
```
