package de.lv1871.dms.FunctionalSalaryCalculator.domain;

import java.util.function.Function;

public class SalaryCalculationRule {

	public static ExtendedFunction<Double, Double, Double> SUM = (a, b) -> a + b;
	public static ExtendedFunction<Double, Double, Double> SUBTRACT = (a, b) -> b - a;
	public static ExtendedFunction<Double, Double, Double> MULTITPLY = (a, b) -> a * b;
	public static ExtendedFunction<Double, Double, Double> SUBTRACT_TAX = (a, b) -> SUBTRACT
			.apply(MULTITPLY.apply(b, a), b);
	public static Function<Double, Double> SUBTRACT_DEFAULT_TAX = SUBTRACT_TAX.curryWith(0.54);
	public static Function<Double, Double> ADD_DEFAULT_PREMIUM = SUM.curryWith(102.0);
	public static ExtendedFunction<Double, Double, Double> PREMIUM = SUM;
	public static TriFunction<ExtendedFunction<Double, Double, Double>, Double, Double, Double> SUPERVISOR_SPECIAL = (
			func, a, b) -> func.apply(a, b);

}
