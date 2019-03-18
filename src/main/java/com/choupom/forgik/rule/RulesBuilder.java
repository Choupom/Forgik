/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

import java.util.ArrayList;
import java.util.List;

public class RulesBuilder {

	private final List<Rule> rules;

	public RulesBuilder() {
		this.rules = new ArrayList<>();
	}

	public Rule[] build() {
		return this.rules.toArray(new Rule[this.rules.size()]);
	}

	public void addRule(String consequentStr, String... antecedentsStr) {
		this.rules.add(new Rule(consequentStr, antecedentsStr));
	}
}
