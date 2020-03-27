/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.proof.tree.ProofReport;

public class ProofInfo {

	private final Formulas antecedents;
	private final Formulas consequents;
	private final boolean[] completedConsequents;
	private final ProofReport[] consequentProofs;

	public ProofInfo(Formulas antecedents, Formulas consequents, boolean[] completedConsequents,
			ProofReport[] consequentProofs) {
		this.antecedents = antecedents.getCopy();
		this.consequents = consequents.getCopy();
		this.completedConsequents = completedConsequents.clone();
		this.consequentProofs = consequentProofs.clone();
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
}
