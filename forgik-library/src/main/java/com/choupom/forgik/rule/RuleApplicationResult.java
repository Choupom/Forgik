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
	private final Set<Integer> leftover;
	private final Map<Integer, Formula> consequentMap;

	public RuleApplicationResult(Formula[] assumptions, Formula[] antecedents, Set<Integer> leftover,
			Map<Integer, Formula> consequentMap) {
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

	public Set<Integer> getLeftover() {
		return this.leftover;
	}

	public Map<Integer, Formula> getConsequentMap() {
		return this.consequentMap;
	}
}
