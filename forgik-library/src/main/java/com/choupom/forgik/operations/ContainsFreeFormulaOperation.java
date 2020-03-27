/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.operations;

import com.choupom.forgik.formula.BinaryConnective;
import com.choupom.forgik.formula.FormulaOperation;
import com.choupom.forgik.formula.FreeFormula;
import com.choupom.forgik.formula.Predicate;
import com.choupom.forgik.formula.UnaryConnective;

public class ContainsFreeFormulaOperation implements FormulaOperation<Boolean, Void> {

	private final int freeFormula;

	public ContainsFreeFormulaOperation(int freeFormula) {
		this.freeFormula = freeFormula;
	}

	@Override
	public Boolean handleBinaryConnective(BinaryConnective binaryConnective, Void param) {
		return (binaryConnective.getOperand1().runOperation(this) || binaryConnective.getOperand2().runOperation(this));
	}

	@Override
	public Boolean handleUnaryConnective(UnaryConnective unaryConnective, Void param) {
		return unaryConnective.getOperand().runOperation(this);
	}

	@Override
	public Boolean handlePredicate(Predicate predicate, Void param) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean handleFreeFormula(FreeFormula freeFormula, Void param) {
		return (freeFormula.getId() == this.freeFormula);
	}
}
