/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.suggester;

import java.util.Set;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.rule.Rule;

public class Suggestion {

	private final Rule rule;
	private final Formula formula;
	private final String[] leftover;

	public Suggestion(Rule rule, Formula formula, Set<String> leftover) {
		this.rule = rule;
		this.formula = formula;
		this.leftover = leftover.toArray(new String[leftover.size()]);
	}

	public Rule getRule() {
		return this.rule;
	}

	public Formula getFormula() {
		return this.formula;
	}

	public String[] getLeftover() {
		return this.leftover;
	}
}
