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
		addRule2("X", "Y", "X ^ Y"); // conjunction introduction
		addRule1("X ^ Y", "X"); // conjunction elimination (1)
		addRule1("X ^ Y", "Y"); // conjunction elimination (2)
		addRule1("X", "X v Y"); // disjunction introduction (1)
		addRule1("X", "Y v X"); // disjunction introduction (2)
		addRule3("X v Y", "X > Z", "Y > Z", "Z"); // disjunction elimination
		// addRule1("F", "X"); // ex falso quodlibet
		addRule2("X", "-X", "Y"); // ex falso quodlibet
		addRule2("(X > Y)", "X", "Y"); // modus ponens
		// addRule2("-X", "X", "F"); // modus ponens (special case)
		addRule1("--X", "X"); // double negation elimination

		// addRule0("X v -X"); // law of excluded middle
		// addRule0("-X v X"); // law of excluded middle
	}
}
