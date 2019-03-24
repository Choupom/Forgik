/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Implication implements Formula {

	private final Formula operand1;
	private final Formula operand2;

	public Implication(Formula operand1, Formula operand2) {
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	public Formula getOperand1() {
		return this.operand1;
	}

	public Formula getOperand2() {
		return this.operand2;
	}

	@Override
	public String toString() {
		String operatorStr = FormulaSettings.getImplicationString();
		return this.operand1.toStringNested() + " " + operatorStr + " " + this.operand2.toStringNested();
	}

	@Override
	public String toStringNested() {
		return "(" + toString() + ")";
	}

	@Override
	public boolean checkEquals(Formula formula) {
		if (!(formula instanceof Implication)) {
			return false;
		}

		Implication implication = (Implication) formula;
		return (this.operand1.checkEquals(implication.operand1) && this.operand2.checkEquals(implication.operand2));
	}

	@Override
	public boolean identify(Formula formula, Map<String, List<Formula>> map) {
		if (formula instanceof FreeVariable) {
			FreeVariable variable = (FreeVariable) formula;
			return variable.identify(this, map);
		}

		if (!(formula instanceof Implication)) {
			return false;
		}

		Implication implication = (Implication) formula;
		return (this.operand1.identify(implication.operand1, map) && this.operand2.identify(implication.operand2, map));
	}

	@Override
	public Formula apply(Map<String, Formula> map) {
		Formula newOperand1 = this.operand1.apply(map);
		Formula newOperand2 = this.operand2.apply(map);
		return new Implication(newOperand1, newOperand2);
	}

	@Override
	public void getFreeVariables(Set<String> variables) {
		this.operand1.getFreeVariables(variables);
		this.operand2.getFreeVariables(variables);
	}
}
