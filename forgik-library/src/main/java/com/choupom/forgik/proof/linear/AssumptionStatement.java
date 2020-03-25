/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof.linear;

import com.choupom.forgik.formula.Formula;

public class AssumptionStatement implements Statement {

	private final Formula assumption;
	private final int depth;

	public AssumptionStatement(Formula assumption, int depth) {
		this.assumption = assumption;
		this.depth = depth;
	}

	@Override
	public Formula getConclusion() {
		return this.assumption;
	}

	@Override
	public int getDepth() {
		return this.depth;
	}
}
