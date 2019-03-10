/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

public class FullRulebook extends Rulebook {

	private static final FullRulebook RULEBOOK = new FullRulebook();

	public static FullRulebook getInstance() {
		return RULEBOOK;
	}

	private FullRulebook() {
		addRule2("(A > B)", "(A > -B)", "-A"); // negation introduction
		// addRule1("-A", "A > B"); // negation elimination
		addRule1("A", "--A"); // double negation introduction
		addRule1("--A", "A"); // double negation elimination
		addRule2("A", "B", "A ^ B"); // conjunction introduction
		addRule1("A ^ B", "A"); // conjunction elimination (1)
		addRule1("A ^ B", "B"); // conjunction elimination (2)
		// addRule1("A", "A v B"); // disjunction introduction (1)
		// addRule1("A", "B v A"); // disjunction introduction (2)
		addRule3("A v B", "A > C", "B > C", "C"); // disjunction elimination
		// addRule0("A v -A"); // law of excluded middle
		// addRule0("-(A ^ -A)"); // law of non-contradiction
		// addRule2("A", "-A", "B"); // ex falso quodlibet
		addRule2("(A > B)", "A", "B"); // modus ponens
		addRule2("(A > B)", "-B", "-A"); // modus tollens
		addRule1("-(A v B)", "-A ^ -B"); // de morgan
		addRule1("-(A ^ B)", "-A v -B"); // de morgan
		addRule1("A v B", "-(-A ^ -B)"); // de morgan
		addRule1("A ^ B", "-(-A v -B)"); // de morgan
		// TODO: add de morgan equivalent
	}
}
