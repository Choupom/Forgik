/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.operations;

import com.choupom.forgik.formula.BinaryConnective;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FormulaOperation;
import com.choupom.forgik.formula.FreeFormula;
import com.choupom.forgik.formula.Predicate;
import com.choupom.forgik.formula.UnaryConnective;

public class EqualsOperation implements FormulaOperation<Boolean, Formula> {

	@Override
	public Boolean handleBinaryConnective(BinaryConnective binaryConnective, Formula other) {
		if (!(other instanceof BinaryConnective)) {
			return Boolean.FALSE;
		}

		BinaryConnective otherConnective = (BinaryConnective) other;
		return (binaryConnective.getType() == otherConnective.getType()
				&& binaryConnective.getOperand1().runOperation(this, otherConnective.getOperand1())
				&& binaryConnective.getOperand2().runOperation(this, otherConnective.getOperand2()));
	}

	@Override
	public Boolean handleUnaryConnective(UnaryConnective unaryConnective, Formula other) {
		if (!(other instanceof UnaryConnective)) {
			return Boolean.FALSE;
		}

		UnaryConnective otherConnective = (UnaryConnective) other;
		return (unaryConnective.getType() == otherConnective.getType()
				&& unaryConnective.getOperand().runOperation(this, otherConnective.getOperand()));
	}

	@Override
	public Boolean handlePredicate(Predicate predicate, Formula other) {
		if (!(other instanceof Predicate)) {
			return Boolean.FALSE;
		}

		Predicate otherPredicate = (Predicate) other;
		return (predicate.getName() == otherPredicate.getName());
	}

	@Override
	public Boolean handleFreeFormula(FreeFormula freeFormula, Formula other) {
		if (!(other instanceof FreeFormula)) {
			return Boolean.FALSE;
		}

		FreeFormula otherFreeFormula = (FreeFormula) other;
		return (otherFreeFormula.getId() == freeFormula.getId());
	}
}
