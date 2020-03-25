/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof.linear;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.rule.Rule;

public class RuleStatement implements Statement {

	private final Rule rule;
	private final int[] antecedentStatements;
	private final Formula consequent;
	private final int depth;

	public RuleStatement(Rule rule, int[] antecedentStatements, Formula consequent, int depth) {
		this.rule = rule;
		this.antecedentStatements = antecedentStatements.clone();
		this.consequent = consequent;
		this.depth = depth;
	}

	public Rule getRule() {
		return this.rule;
	}

	public int[] getAntecedentStatements() {
		return this.antecedentStatements.clone();
	}

	@Override
	public Formula getConclusion() {
		return this.consequent;
	}

	@Override
	public int getDepth() {
		return this.depth;
	}
}
