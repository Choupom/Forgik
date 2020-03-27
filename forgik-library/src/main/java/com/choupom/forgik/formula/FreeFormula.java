/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

public class FreeFormula extends Formula {

	public static final String STRING_PREFIX = "$";

	private final int id;

	public FreeFormula(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public <R, P> R runOperation(FormulaOperation<R, P> operation, P param) {
		return operation.handleFreeFormula(this, param);
	}
}
