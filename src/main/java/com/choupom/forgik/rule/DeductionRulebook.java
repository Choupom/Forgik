/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

public class DeductionRulebook extends Rulebook {

	public static final DeductionRulebook RULEBOOK = new DeductionRulebook();

	public static DeductionRulebook getInstance() {
		return RULEBOOK;
	}

	private DeductionRulebook() {
		addRule2("A", "B", "A ^ B"); // conjunction introduction
		addRule1("A ^ B", "A"); // conjunction elimination (1)
		addRule1("A ^ B", "B"); // conjunction elimination (2)
		addRule1("A", "A v B"); // disjunction introduction (1)
		addRule1("A", "B v A"); // disjunction introduction (2)
		addRule3("A v B", "A > C", "B > C", "C"); // disjunction elimination
		// addRule1("F", "A"); // ex falso quodlibet
		addRule2("-A", "A", "B"); // ex falso quodlibet
		addRule2("(A > B)", "A", "B"); // modus ponens
		// addRule2("-A", "A", "F"); // modus ponens (special case)
		addRule1("--A", "A"); // double negation elimination

		// addRule0("A v -A"); // law of excluded middle
		// addRule0("-A v A"); // law of excluded middle
	}
}
