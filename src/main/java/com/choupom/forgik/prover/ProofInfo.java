/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import com.choupom.forgik.formula.Formula;

public class ProofInfo {

	private final Formula[] antecedents;
	private final Formula[] consequents;
	private final boolean[] completedConsequents;

	public ProofInfo(Formula[] antecedents, Formula[] consequents, boolean[] completedConsequents) {
		this.antecedents = antecedents.clone();
		this.consequents = consequents.clone();
		this.completedConsequents = completedConsequents.clone();
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
}
