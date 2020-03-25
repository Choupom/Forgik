/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.proof.tree.ProofReport;

public class ProofInfo {

	private final Formula[] antecedents;
	private final Formula[] consequents;
	private final boolean[] completedConsequents;
	private final ProofReport[] consequentProofs;

	public ProofInfo(Formula[] antecedents, Formula[] consequents, boolean[] completedConsequents,
			ProofReport[] consequentProofs) {
		this.antecedents = antecedents.clone();
		this.consequents = consequents.clone();
		this.completedConsequents = completedConsequents.clone();
		this.consequentProofs = consequentProofs.clone();
	}

	public Formula[] getAntecedents() {
		return this.antecedents;
	}

	public Formula[] getConsequents() {
		return this.consequents;
	}

	public boolean[] getCompletedConsequents() {
		return this.completedConsequents;
	}

	public ProofReport[] getConsequentProofs() {
		return this.consequentProofs;
	}
}
