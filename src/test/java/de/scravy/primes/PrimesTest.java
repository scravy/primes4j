package de.scravy.primes;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class PrimesTest {

  {
    final Primes primes = Primes.load(100000);

    describe("getPrimeFactors", () -> {
      it("should compute prime factors for a number below 1000", () -> {
        expect(primes.getPrimeFactors(2 * 2 * 3 * 11)).toEqual(Arrays.asList(2, 2, 3, 11));
      });
      it("should compute prime factors for a number below 100000", () -> {
        expect(primes.getPrimeFactors(2 * 2 * 2 * 17 * 17 * 23)).toEqual(Arrays.asList(2, 2, 2, 17, 17, 23));
      });
    });
  }
}
