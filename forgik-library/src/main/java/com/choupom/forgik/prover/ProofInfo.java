/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.proof.tree.ProofReport;
import com.choupom.forgik.rule.Rule;

public class ProofInfo {

	private final int[] path;
	private final Formulas antecedents;
	private final Formulas consequents;
	private final boolean[] completedConsequents;
	private final ProofReport[] consequentProofs;
	private final Proof parentProof;
	private final int parentConsequentId;
	private final Rule parentConsequentRule;

	public ProofInfo(int[] path, Formulas antecedents, Formulas consequents, boolean[] completedConsequents,
			ProofReport[] consequentProofs, Proof parentProof, int parentConsequentId, Rule parentConsequentRule) {
		this.path = path.clone();
		this.antecedents = antecedents;
		this.consequents = consequents;
		this.completedConsequents = completedConsequents.clone();
		this.consequentProofs = consequentProofs.clone();
		this.parentProof = parentProof;
		this.parentConsequentId = parentConsequentId;
		this.parentConsequentRule = parentConsequentRule;
	}

	public int[] getPath() {
		return this.path;
	}

	public Formulas getAntecedents() {
		return this.antecedents;
	}

	public Formulas getConsequents() {
		return this.consequents;
	}

	public boolean[] getCompletedConsequents() {
		return this.completedConsequents;
	}

	public ProofReport[] getConsequentProofs() {
		return this.consequentProofs;
	}

	public ProofInfo getParentProof() {
		return (this.parentProof == null ? null : this.parentProof.getInfo());
	}

	public int getParentConsequentId() {
		return this.parentConsequentId;
	}

	public Rule getParentConsequentRule() {
		return this.parentConsequentRule;
	}
}
