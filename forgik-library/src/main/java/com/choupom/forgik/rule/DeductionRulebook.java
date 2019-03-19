/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

public class DeductionRulebook {

	public static Rule[] getRules() {
		RulesBuilder rulebook = new RulesBuilder();

		rulebook.addRule("X ^ Y", "X", "Y"); // conjunction introduction
		rulebook.addRule("X", "X ^ Y"); // conjunction elimination (1)
		rulebook.addRule("Y", "X ^ Y"); // conjunction elimination (2)
		rulebook.addRule("X v Y", "X"); // disjunction introduction (1)
		rulebook.addRule("Y v X", "X"); // disjunction introduction (2)
		rulebook.addRule("Z", "X v Y", "X > Z", "Y > Z"); // disjunction elimination
		rulebook.addRule("Y", "X", "-X"); // ex falso quodlibet
		rulebook.addRule("Y", "(X > Y)", "X"); // modus ponens
		rulebook.addRule("X", "--X"); // double negation elimination

		// NOTE:
		// these two rules:
		// * rulebook.addRule("X", "F"); // ex falso quodlibet
		// * rulebook.addRule("F", "-X", "X"); // modus ponens (special case)
		// have been replaced by this rule:
		// * rulebook.addRule("Y", "X", "-X"); // ex falso quodlibet
		// by doing so we never need to use F
		// and the user does not have to know that -X is X > F

		return rulebook.build();
	}
}
