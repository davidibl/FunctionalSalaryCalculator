package de.lv1871.dms.FunctionalSalaryCalculator.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SalaryCalculatorTest {

	// @formatter:off
	/**
	 * A salary calculator has the following rules:
	 *
	 * 1. A 54% tax should be subtracted 
	 * 2. A gross salary of 102 should be added
	 * 3. A regional tax can be subtracted conditionally
	 * 4. An individual premium can get added
	 * 5. Finally every supervisor can add or subtract something on the final result
	 *
	 * The rules have to be applied in the following order 2 -> 4 -> 1 -> 3 -> 5
	 *
	 * The rules should be resuable in any way
	 * The calculator should implement a builder pattern
	 *
	 * The calculation method should expect an optional parameter 'limit'.
	 * This parameter tells us how many rules should be recognized when caclulating the result.
	 */
	// @formatter:on

	// @formatter:off
	/**
	 * rules:
	 *
	 * base salary: 2300
	 * premium: 54
	 * supervisor special: -23
	 */
	// @formatter:on
	@Test
	public void testGehaltsberechnungA() {
		// @formatter:off
		Double salary = SalaryCalculator
			.create()
			.with(SalaryCalculationRule.ADD_DEFAULT_PREMIUM)
			.with(SalaryCalculationRule.PREMIUM.curryWith(54.0))
			.with(SalaryCalculationRule.SUBTRACT_DEFAULT_TAX)
			.with(SalaryCalculationRule.SUPERVISOR_SPECIAL.curryWith(SalaryCalculationRule.SUBTRACT).curryWith(23.0))
			.calculate(2300.0);
		// @formatter:on

		assertEquals(new Double(1106.76), salary);
	}

	// @formatter:off
	/**
	 * rules
	 *
	 * base salary: 2120
	 * regional tax: 1%
	 * supervisor special: 0
	 */
	// @formatter:on
	@Test
	public void testGehaltsberechnungB() {
		// @formatter:off
		Double salary = SalaryCalculator
			.create()
			.with(SalaryCalculationRule.ADD_DEFAULT_PREMIUM)
			.with(SalaryCalculationRule.SUBTRACT_DEFAULT_TAX)
			.with(SalaryCalculationRule.SUBTRACT_TAX.curryWith(0.01))
			.calculate(2120.0);
		// @formatter:on

		assertEquals(new Double(1011.9), salary);
	}

	// @formatter:off
	/**
	 * rules
	 *
	 * base salary: 1020
	 * regional tax: 12%
	 * supervisor special: +233
	 * premium: 102
	 */
	// @formatter:on
	@Test
	public void testGehaltsberechnungC() {
		// @formatter:off
		Double salary = SalaryCalculator
			.create()
			.with(SalaryCalculationRule.ADD_DEFAULT_PREMIUM)
			.with(SalaryCalculationRule.PREMIUM.curryWith(102.0))
			.with(SalaryCalculationRule.SUBTRACT_DEFAULT_TAX)
			.with(SalaryCalculationRule.SUBTRACT_TAX.curryWith(0.12))
			.with(SalaryCalculationRule.SUPERVISOR_SPECIAL.curryWith(SalaryCalculationRule.SUM).curryWith(233.0))
			.calculate(1020.0);
		// @formatter:on

		assertEquals(new Double(728.48), salary);
	}

	// @formatter:off
	/**
	 * rules
	 *
	 * base salary: 1020
	 * regional tax: 12%
	 * supervisor special: +233
	 * premium: 102
	 */
	// @formatter:on
	@Test
	public void testGehaltsberechnungCMitAssertionNachErsterDritterUndLetzterRegel() {
		// @formatter:off
		SalaryCalculator salaryCalculatorConfigured = SalaryCalculator
			.create()
			.with(SalaryCalculationRule.ADD_DEFAULT_PREMIUM)
			.with(SalaryCalculationRule.PREMIUM.curryWith(102.0))
			.with(SalaryCalculationRule.SUBTRACT_DEFAULT_TAX)
			.with(SalaryCalculationRule.SUBTRACT_TAX.curryWith(0.12))
			.with(SalaryCalculationRule.SUPERVISOR_SPECIAL.curryWith(SalaryCalculationRule.SUM).curryWith(233.0));
		// @formatter:on

		Double salaryAfterThreeRules = salaryCalculatorConfigured.withLimit(3).calculate(1020.0);
		Double salaryAfterAllRules = salaryCalculatorConfigured.withLimit(null).calculate(1020.0);

		assertEquals(new Double(563.04), salaryAfterThreeRules);
		assertEquals(new Double(728.48), salaryAfterAllRules);
	}

	// @formatter:off
	/**
	 * rules
	 *
	 * base salary: 1020
	 * regional tax: 12%
	 * supervisor special: +-0
	 * and a special special. The base salary gets multiplied with three
	 */
	// @formatter:on
	@Test
	public void testGehaltsberechnungDSonderlocke() {
		// @formatter:off
		ExtendedFunction<Double, Double, Double> MULTIPLY = (a, b) -> a * b;

		Double salary = SalaryCalculator
			.create()
			.with(MULTIPLY.curryWith(3.0))
			.with(SalaryCalculationRule.ADD_DEFAULT_PREMIUM)
			.with(SalaryCalculationRule.SUBTRACT_DEFAULT_TAX)
			.with(SalaryCalculationRule.SUBTRACT_TAX.curryWith(0.12))
			.calculate(1020.0);
		// @formatter:on

		assertEquals(new Double(1279.98), salary);
	}

}
