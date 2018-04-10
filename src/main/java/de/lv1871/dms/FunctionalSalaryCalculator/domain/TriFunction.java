package de.lv1871.dms.FunctionalSalaryCalculator.domain;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<A, B, C, R> {

	R apply(A a, B b, C c);

	default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c) -> after.apply(apply(a, b, c));
	}

	default Function<A, ExtendedFunction<B, C, R>> curry() {
		return a -> (b, c) -> this.apply(a, b, c);
	}

	default ExtendedFunction<B, C, R> curryWith(A value) {
		return curry().apply(value);
	}
}
