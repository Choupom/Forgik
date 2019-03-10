/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.suggester;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.rule.Rule;

public class SuggestionReverse {
	public Rule rule;
	public Formula[] formulas;

	public SuggestionReverse(Rule rule, Formula[] formulas) {
		this.rule = rule;
		this.formulas = formulas.clone();
	}

	public Rule getRule() {
		return this.rule;
	}

	public Formula[] getFormulas() {
		return this.formulas;
	}
}
