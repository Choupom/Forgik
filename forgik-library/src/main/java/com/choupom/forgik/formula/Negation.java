/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Negation implements Formula {

	private final Formula operand;

	public Negation(Formula operand) {
		this.operand = operand;
	}

	public Formula getOperand() {
		return this.operand;
	}

	@Override
	public String toString() {
		String operatorStr = FormulaSettings.getInstance().getNegationString();
		return operatorStr + this.operand.toStringNested();
	}

	@Override
	public String toStringNested() {
		return toString();
	}

	@Override
	public boolean checkEquals(Formula formula) {
		if (!(formula instanceof Negation)) {
			return false;
		}

		Negation negation = (Negation) formula;
		return this.operand.checkEquals(negation.operand);
	}

	@Override
	public boolean identify(Formula formula, Map<String, List<Formula>> map) {
		if (formula instanceof FreeVariable) {
			FreeVariable variable = (FreeVariable) formula;
			return variable.identify(this, map);
		}

		if (!(formula instanceof Negation)) {
			return false;
		}

		Negation negation = (Negation) formula;
		return this.operand.identify(negation.operand, map);
	}

	@Override
	public Formula apply(Map<String, Formula> map) {
		Formula newOperand = this.operand.apply(map);
		return new Negation(newOperand);
	}

	@Override
	public void getFreeVariables(Set<String> variables) {
		this.operand.getFreeVariables(variables);
	}
}
