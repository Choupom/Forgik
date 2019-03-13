/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.suggester;

import java.util.Set;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.rule.Rule;

public class SuggestionReverse {

	private final Rule rule;
	private final Formula[] formulas;
	private final String[] leftover;

	public SuggestionReverse(Rule rule, Formula[] formulas, Set<String> leftover) {
		this.rule = rule;
		this.formulas = formulas.clone();
		this.leftover = leftover.toArray(new String[leftover.size()]);
	}

	public Rule getRule() {
		return this.rule;
	}

	public Formula[] getFormulas() {
		return this.formulas;
	}

	public String[] getLeftover() {
		return this.leftover;
	}
}
