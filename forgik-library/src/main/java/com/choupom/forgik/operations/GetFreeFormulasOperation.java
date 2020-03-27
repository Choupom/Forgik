/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.operations;

import java.util.Set;

import com.choupom.forgik.formula.BinaryConnective;
import com.choupom.forgik.formula.FreeFormula;
import com.choupom.forgik.formula.Predicate;
import com.choupom.forgik.formula.UnaryConnective;
import com.choupom.forgik.formula.VoidFormulaOperation;

public class GetFreeFormulasOperation implements VoidFormulaOperation<Void> {

	private final Set<Integer> freeFormulas;

	public GetFreeFormulasOperation(Set<Integer> freeFormulas) {
		this.freeFormulas = freeFormulas;
	}

	@Override
	public Void handleBinaryConnective(BinaryConnective binaryConnective, Void param) {
		binaryConnective.getOperand1().runOperation(this);
		binaryConnective.getOperand2().runOperation(this);
		return null;
	}

	@Override
	public Void handleUnaryConnective(UnaryConnective unaryConnective, Void param) {
		unaryConnective.getOperand().runOperation(this);
		return null;
	}

	@Override
	public Void handlePredicate(Predicate predicate, Void param) {
		return null;
	}

	@Override
	public Void handleFreeFormula(FreeFormula freeFormula, Void param) {
		this.freeFormulas.add(Integer.valueOf(freeFormula.getId()));
		return null;
	}
}
