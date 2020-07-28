/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof.tree;

import java.util.Map;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.operations.ApplyOperation;
import com.choupom.forgik.rule.Rule;

public class ProofReportRule implements ProofReport {

	private final Rule rule;
	private final Formulas assumptions;
	private final ProofReport[] subproofs;
	private final Formula conclusion;

	public ProofReportRule(Rule rule, Formulas assumptions, ProofReport[] subproofs, Formula conclusion) {
		this.rule = rule;
		this.assumptions = assumptions;
		this.subproofs = subproofs.clone();
		this.conclusion = conclusion;
	}

	public Rule getRule() {
		return this.rule;
	}

	public Formulas getAssumptions() {
		return this.assumptions;
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
		ApplyOperation applyOperation = new ApplyOperation(map);
		Formulas assumptions = this.assumptions.runOperation(applyOperation);
		Formula conclusion = this.conclusion.runOperation(applyOperation);

		ProofReport[] subproofs = new ProofReport[this.subproofs.length];
		for (int i = 0; i < subproofs.length; i++) {
			subproofs[i] = this.subproofs[i].apply(map);
		}

		return new ProofReportRule(this.rule, assumptions, subproofs, conclusion);
	}
}
