/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import com.choupom.forgik.operations.EqualsOperation;
import com.choupom.forgik.operations.GetStringOperation;

public abstract class Formula {

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		runOperation(new GetStringOperation(stringBuilder), Boolean.FALSE);
		return stringBuilder.toString();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (!(object instanceof Formula)) {
			return false;
		} else {
			Formula otherFormula = (Formula) object;
			return runOperation(new EqualsOperation(), otherFormula);
		}
	}

	public <R> R runOperation(FormulaOperation<R, Void> operation) {
		return runOperation(operation, null);
	}

	public abstract <R, P> R runOperation(FormulaOperation<R, P> operation, P param);
}
