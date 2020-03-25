/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof.tree;

import java.util.Map;

import com.choupom.forgik.formula.Formula;

public class ProofReportIdentification implements ProofReport {

	private final Formula antecedent;
	private final int antecedentId;

	public ProofReportIdentification(Formula antecedent, int antecedentId) {
		this.antecedent = antecedent;
		this.antecedentId = antecedentId;
	}

	@Override
	public Formula getConclusion() {
		return this.antecedent;
	}

	public int getAntecedentId() {
		return this.antecedentId;
	}

	@Override
	public ProofReportIdentification apply(Map<Integer, Formula> map) {
		Formula antecedent = this.antecedent.apply(map);

		return new ProofReportIdentification(antecedent, this.antecedentId);
	}
}
