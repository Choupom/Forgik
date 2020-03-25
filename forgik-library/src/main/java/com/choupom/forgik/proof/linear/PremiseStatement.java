/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof.linear;

import com.choupom.forgik.formula.Formula;

public class PremiseStatement implements Statement {

	private final Formula premise;

	public PremiseStatement(Formula premise) {
		this.premise = premise;
	}

	@Override
	public Formula getConclusion() {
		return this.premise;
	}

	@Override
	public int getDepth() {
		return 0;
	}
}
