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
		addRule2("(X > Y)", "(X > -Y)", "-X"); // negation introduction
		// addRule1("-X", "X > Y"); // negation elimination
		addRule1("X", "--X"); // double negation introduction
		addRule1("--X", "X"); // double negation elimination
		addRule2("X", "Y", "X ^ Y"); // conjunction introduction
		addRule1("X ^ Y", "X"); // conjunction elimination (1)
		addRule1("X ^ Y", "Y"); // conjunction elimination (2)
		// addRule1("X", "X v Y"); // disjunction introduction (1)
		// addRule1("X", "Y v X"); // disjunction introduction (2)
		addRule3("X v Y", "X > Z", "Y > Z", "Z"); // disjunction elimination
		// addRule0("X v -X"); // law of excluded middle
		// addRule0("-(X ^ -X)"); // law of non-contradiction
		// addRule2("X", "-X", "Y"); // ex falso quodlibet
		addRule2("(X > Y)", "X", "Y"); // modus ponens
		addRule2("(X > Y)", "-Y", "-X"); // modus tollens
		addRule1("-(X v Y)", "-X ^ -Y"); // de morgan
		addRule1("-(X ^ Y)", "-X v -Y"); // de morgan
		addRule1("X v Y", "-(-X ^ -Y)"); // de morgan
		addRule1("X ^ Y", "-(-X v -Y)"); // de morgan
		// TODO: add de morgan equivalent
	}
}
