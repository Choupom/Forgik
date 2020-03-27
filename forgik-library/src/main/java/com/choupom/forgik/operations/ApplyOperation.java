/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.operations;

import java.util.HashMap;
import java.util.Map;

import com.choupom.forgik.formula.BinaryConnective;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FormulaOperation;
import com.choupom.forgik.formula.FreeFormula;
import com.choupom.forgik.formula.Predicate;
import com.choupom.forgik.formula.UnaryConnective;

public class ApplyOperation implements FormulaOperation<Formula, Void> {

	private final Map<Integer, Formula> map;

	public ApplyOperation(Map<Integer, Formula> map) {
		this.map = new HashMap<>(map);
	}

	@Override
	public Formula handleBinaryConnective(BinaryConnective binaryConnective, Void param) {
		Formula newOperand1 = binaryConnective.getOperand1().runOperation(this);
		Formula newOperand2 = binaryConnective.getOperand2().runOperation(this);
		return new BinaryConnective(binaryConnective.getType(), newOperand1, newOperand2);
	}

	@Override
	public Formula handleUnaryConnective(UnaryConnective unaryConnective, Void param) {
		Formula newOperand = unaryConnective.getOperand().runOperation(this);
		return new UnaryConnective(unaryConnective.getType(), newOperand);
	}

	@Override
	public Formula handlePredicate(Predicate predicate, Void param) {
		return predicate;
	}

	@Override
	public Formula handleFreeFormula(FreeFormula freeFormula, Void param) {
		Formula formula = this.map.get(Integer.valueOf(freeFormula.getId()));
		if (formula != null) {
			return formula;
		} else {
			return freeFormula;
		}
	}
}
