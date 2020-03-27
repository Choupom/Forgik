/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.operations;

import com.choupom.forgik.formula.BinaryConnective;
import com.choupom.forgik.formula.FreeFormula;
import com.choupom.forgik.formula.Predicate;
import com.choupom.forgik.formula.UnaryConnective;
import com.choupom.forgik.formula.VoidFormulaOperation;

public class GetStringOperation implements VoidFormulaOperation<Boolean> {

	private final StringBuilder stringBuilder;

	public GetStringOperation(StringBuilder stringBuilder) {
		this.stringBuilder = stringBuilder;
	}

	@Override
	public Void handleBinaryConnective(BinaryConnective binaryConnective, Boolean nested) {
		if (nested) {
			this.stringBuilder.append('(');
		}
		binaryConnective.getOperand1().runOperation(this, Boolean.TRUE);
		this.stringBuilder.append(' ');
		this.stringBuilder.append(binaryConnective.getType().getSymbol());
		this.stringBuilder.append(' ');
		binaryConnective.getOperand2().runOperation(this, Boolean.TRUE);
		if (nested) {
			this.stringBuilder.append(')');
		}
		return null;
	}

	@Override
	public Void handleUnaryConnective(UnaryConnective unaryConnective, Boolean nested) {
		this.stringBuilder.append(unaryConnective.getType().getSymbol());
		unaryConnective.getOperand().runOperation(this, Boolean.TRUE);
		return null;
	}

	@Override
	public Void handlePredicate(Predicate predicate, Boolean nested) {
		this.stringBuilder.append(predicate.getName());
		return null;
	}

	@Override
	public Void handleFreeFormula(FreeFormula freeFormula, Boolean nested) {
		this.stringBuilder.append(FreeFormula.STRING_PREFIX);
		this.stringBuilder.append(freeFormula.getId());
		return null;
	}
}
