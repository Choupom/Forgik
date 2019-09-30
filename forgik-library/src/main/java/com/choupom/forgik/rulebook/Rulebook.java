/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rulebook;

import com.choupom.forgik.rule.Rule;

public class Rulebook {

	private final String name;
	private final Rule[] rules;

	public Rulebook(String name, Rule[] rules) {
		this.name = name;
		this.rules = rules.clone();
	}

	public String getName() {
		return this.name;
	}

	public Rule[] getRules() {
		return this.rules.clone();
	}
}
