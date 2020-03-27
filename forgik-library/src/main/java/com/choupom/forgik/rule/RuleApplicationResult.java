/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

import java.util.Map;
import java.util.Set;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Formulas;

public class RuleApplicationResult {

	private final Formulas assumptions;
	private final Formulas antecedents;
	private final Set<Integer> leftover;
	private final Map<Integer, Formula> consequentMap;

	public RuleApplicationResult(Formulas assumptions, Formulas antecedents, Set<Integer> leftover,
			Map<Integer, Formula> consequentMap) {
		this.assumptions = assumptions;
		this.antecedents = antecedents;
		this.leftover = leftover;
		this.consequentMap = consequentMap;
	}

	public Formulas getAssumptions() {
		return this.assumptions.getCopy();
	}

	public Formulas getAntecedents() {
		return this.antecedents.getCopy();
	}

	public Set<Integer> getLeftover() {
		return this.leftover;
	}

	public Map<Integer, Formula> getConsequentMap() {
		return this.consequentMap;
	}
}
