/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

public class FullRulebook {

	private FullRulebook() {
		// private constructor
	}

	public static Rule[] getRules() {
		RulesBuilder rulebook = new RulesBuilder();

		rulebook.addRule("-X", "(X > Y)", "(X > -Y)"); // negation introduction
		rulebook.addRule("X > Y", "-X"); // negation elimination
		rulebook.addRule("--X", "X"); // double negation introduction
		rulebook.addRule("X", "--X"); // double negation elimination
		rulebook.addRule("X ^ Y", "X", "Y"); // conjunction introduction
		rulebook.addRule("Y", "Y ^ X"); // conjunction elimination (1)
		rulebook.addRule("Y", "X ^ Y"); // conjunction elimination (2)
		rulebook.addRule("X v Y", "X"); // disjunction introduction (1)
		rulebook.addRule("Y v X", "X"); // disjunction introduction (2)
		rulebook.addRule("Z", "X v Y", "X > Z", "Y > Z"); // disjunction elimination
		rulebook.addRule("X v -X"); // law of excluded middle
		rulebook.addRule("-(X ^ -X)"); // law of non-contradiction
		rulebook.addRule("Y", "X", "-X"); // ex falso quodlibet
		rulebook.addRule("Y", "(X > Y)", "X"); // modus ponens
		rulebook.addRule("-X", "(X > Y)", "-Y"); // modus tollens
		rulebook.addRule("-(X v Y)", "-X ^ -Y"); // de morgan
		rulebook.addRule("-(X ^ Y)", "-X v -Y"); // de morgan
		rulebook.addRule("X v Y", "-(-X ^ -Y)"); // de morgan
		rulebook.addRule("X ^ Y", "-(-X v -Y)"); // de morgan
		// TODO: add de morgan equivalent

		return rulebook.build();
	}
}
