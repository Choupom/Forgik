/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof.tree;

import java.util.Map;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.rule.Rule;

public class ProofReportRule implements ProofReport {

	private final Rule rule;
	private final Formula[] assumptions;
	private final ProofReport[] subproofs;
	private final Formula conclusion;

	public ProofReportRule(Rule rule, Formula[] assumptions, ProofReport[] subproofs, Formula conclusion) {
		this.rule = rule;
		this.assumptions = assumptions.clone();
		this.subproofs = subproofs.clone();
		this.conclusion = conclusion;
	}

	public Rule getRule() {
		return this.rule;
	}

	public Formula[] getAssumptions() {
		return this.assumptions.clone();
	}

	public ProofReport[] getSubproofs() {
		return this.subproofs.clone();
	}

	@Override
	public Formula getConclusion() {
		return this.conclusion;
	}

	@Override
	public ProofReportRule apply(Map<Integer, Formula> map) {
		Formula[] assumptions = new Formula[this.assumptions.length];
		for (int i = 0; i < assumptions.length; i++) {
			assumptions[i] = this.assumptions[i].apply(map);
		}

		ProofReport[] subproofs = new ProofReport[this.subproofs.length];
		for (int i = 0; i < subproofs.length; i++) {
			subproofs[i] = this.subproofs[i].apply(map);
		}

		Formula conclusion = this.conclusion.apply(map);

		return new ProofReportRule(this.rule, assumptions, subproofs, conclusion);
	}
}
