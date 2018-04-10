package de.lv1871.dms.FunctionalSalaryCalculator.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SalaryCalculator {

	private List<Function<Double, Double>> rules = new ArrayList<>();
	private Integer limit;

	public static SalaryCalculator create() {
		return new SalaryCalculator();
	}

	public SalaryCalculator with(Function<Double, Double> regel) {
		this.rules.add(regel);
		return this;
	}

	public SalaryCalculator withLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	public Double berechne(Double basisgehalt) {
		// @formatter:off
		return ROUND_DEFAULT.apply(rules
			.stream()
			.limit(this.getLimit())
			.reduce(Function.identity(), Function::andThen)
			.apply(basisgehalt));
		// @formatter:on
	}

	private static ExtendedFunction<Integer, Double, Double> ROUND = (Integer places, Double value) -> {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	};

	private static Function<Double, Double> ROUND_DEFAULT = ROUND.curryWith(2);

	private int getLimit() {
		return (limit == null) ? rules.size() : limit;
	}

}
