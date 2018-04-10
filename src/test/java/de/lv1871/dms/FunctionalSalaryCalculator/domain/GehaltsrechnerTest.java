package de.lv1871.dms.FunctionalSalaryCalculator.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GehaltsrechnerTest {

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
		Double gehalt = SalaryCalculator
			.create()
			.with(SalaryCalculationRule.ZULAGE_ADDIEREN)
			.with(SalaryCalculationRule.BONUS.curryWith(54.0))
			.with(SalaryCalculationRule.STANDARD_STEUERSATZ_ABZIEHEN)
			.with(SalaryCalculationRule.VORGESETZTEN_SPECIAL.curryWith(SalaryCalculationRule.SUBTRACT).curryWith(23.0))
			.berechne(2300.0);
		// @formatter:on

		assertEquals(new Double(1106.76), gehalt);
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
		Double gehalt = SalaryCalculator
			.create()
			.with(SalaryCalculationRule.ZULAGE_ADDIEREN)
			.with(SalaryCalculationRule.STANDARD_STEUERSATZ_ABZIEHEN)
			.with(SalaryCalculationRule.STEUERSATZ_ABZIEHEN.curryWith(0.01))
			.berechne(2120.0);
		// @formatter:on

		assertEquals(new Double(1011.9), gehalt);
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
		Double gehalt = SalaryCalculator
			.create()
			.with(SalaryCalculationRule.ZULAGE_ADDIEREN)
			.with(SalaryCalculationRule.BONUS.curryWith(102.0))
			.with(SalaryCalculationRule.STANDARD_STEUERSATZ_ABZIEHEN)
			.with(SalaryCalculationRule.STEUERSATZ_ABZIEHEN.curryWith(0.12))
			.with(SalaryCalculationRule.VORGESETZTEN_SPECIAL.curryWith(SalaryCalculationRule.SUM).curryWith(233.0))
			.berechne(1020.0);
		// @formatter:on

		assertEquals(new Double(728.48), gehalt);
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
		SalaryCalculator gehaltsrechnerKonfiguriert = SalaryCalculator
			.create()
			.with(SalaryCalculationRule.ZULAGE_ADDIEREN)
			.with(SalaryCalculationRule.BONUS.curryWith(102.0))
			.with(SalaryCalculationRule.STANDARD_STEUERSATZ_ABZIEHEN)
			.with(SalaryCalculationRule.STEUERSATZ_ABZIEHEN.curryWith(0.12))
			.with(SalaryCalculationRule.VORGESETZTEN_SPECIAL.curryWith(SalaryCalculationRule.SUM).curryWith(233.0));
		// @formatter:on

		Double gehaltNachRegelDrei = gehaltsrechnerKonfiguriert.withLimit(3).berechne(1020.0);
		Double gehaltNachRegelAlle = gehaltsrechnerKonfiguriert.withLimit(null).berechne(1020.0);

		assertEquals(new Double(563.04), gehaltNachRegelDrei);
		assertEquals(new Double(728.48), gehaltNachRegelAlle);
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

		Double gehalt = SalaryCalculator
			.create()
			.with(MULTIPLY.curryWith(3.0))
			.with(SalaryCalculationRule.ZULAGE_ADDIEREN)
			.with(SalaryCalculationRule.STANDARD_STEUERSATZ_ABZIEHEN)
			.with(SalaryCalculationRule.STEUERSATZ_ABZIEHEN.curryWith(0.12))
			.berechne(1020.0);
		// @formatter:on

		assertEquals(new Double(1279.98), gehalt);
	}

}
