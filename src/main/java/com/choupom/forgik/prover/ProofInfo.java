/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import com.choupom.forgik.formula.Formula;

public class ProofInfo {

	public Formula[] antecedents;
	public Formula[] consequents;
	public boolean[] completedConsequents;

	public ProofInfo(Formula[] antecedents, Formula[] consequents, boolean[] completedConsequents) {
		this.antecedents = antecedents.clone();
		this.consequents = consequents.clone();
		this.completedConsequents = completedConsequents.clone();
	}
}
