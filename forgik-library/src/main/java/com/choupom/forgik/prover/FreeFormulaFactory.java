/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import com.choupom.forgik.formula.FreeFormula;

public class FreeFormulaFactory {

	private int counter;

	public FreeFormulaFactory() {
		this.counter = 1;
	}

	public FreeFormula createFreeFormula() {
		return new FreeFormula(this.counter++);
	}
}
