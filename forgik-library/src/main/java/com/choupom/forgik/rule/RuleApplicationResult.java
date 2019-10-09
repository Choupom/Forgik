/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

import java.util.Map;
import java.util.Set;

import com.choupom.forgik.formula.Formula;

public class RuleApplicationResult {

	private final Formula[] assumptions;
	private final Formula[] antecedents;
	private final Set<String> leftover;
	private final Map<String, Formula> consequentMap;

	public RuleApplicationResult(Formula[] assumptions, Formula[] antecedents, Set<String> leftover,
			Map<String, Formula> consequentMap) {
		this.assumptions = assumptions;
		this.antecedents = antecedents;
		this.leftover = leftover;
		this.consequentMap = consequentMap;
	}

	public Formula[] getAssumptions() {
		return this.assumptions;
	}

	public Formula[] getAntecedents() {
		return this.antecedents;
	}

	public Set<String> getLeftover() {
		return this.leftover;
	}

	public Map<String, Formula> getConsequentMap() {
		return this.consequentMap;
	}
}
