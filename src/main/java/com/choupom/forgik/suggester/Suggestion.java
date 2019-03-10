/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.suggester;

import java.util.List;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.rule.Rule;

public class Suggestion {
	public Rule rule;
	public Formula formula;
	public String[] leftover;

	public Suggestion(Rule rule, Formula formula, List<String> leftover) {
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
