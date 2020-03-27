/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

public class Predicate extends Formula {

	private final char name;

	public Predicate(char name) {
		this.name = name;
	}

	public char getName() {
		return this.name;
	}

	@Override
	public <R, P> R runOperation(FormulaOperation<R, P> operation, P param) {
		return operation.handlePredicate(this, param);
	}
}
