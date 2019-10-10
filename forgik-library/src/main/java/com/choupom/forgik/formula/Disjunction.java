/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Disjunction implements Formula {

	private final Formula operand1;
	private final Formula operand2;

	public Disjunction(Formula operand1, Formula operand2) {
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
		String operatorStr = FormulaSettings.getInstance().getDisjunctionString();
		return this.operand1.toStringNested() + " " + operatorStr + " " + this.operand2.toStringNested();
	}

	@Override
	public String toStringNested() {
		return "(" + toString() + ")";
	}

	@Override
	public boolean checkEquals(Formula formula) {
		if (!(formula instanceof Disjunction)) {
			return false;
		}

		Disjunction disjunction = (Disjunction) formula;
		return (this.operand1.checkEquals(disjunction.operand1) && this.operand2.checkEquals(disjunction.operand2));
	}

	@Override
	public boolean identify(Formula formula, Map<String, List<Formula>> map) {
		if (formula instanceof FreeVariable) {
			FreeVariable variable = (FreeVariable) formula;
			return variable.identify(this, map);
		}

		if (!(formula instanceof Disjunction)) {
			return false;
		}

		Disjunction disjunction = (Disjunction) formula;
		return (this.operand1.identify(disjunction.operand1, map) && this.operand2.identify(disjunction.operand2, map));
	}

	@Override
	public Formula apply(Map<String, Formula> map) {
		Formula newOperand1 = this.operand1.apply(map);
		Formula newOperand2 = this.operand2.apply(map);
		return new Disjunction(newOperand1, newOperand2);
	}

	@Override
	public void getFreeVariables(Set<String> variables) {
		this.operand1.getFreeVariables(variables);
		this.operand2.getFreeVariables(variables);
	}
}
