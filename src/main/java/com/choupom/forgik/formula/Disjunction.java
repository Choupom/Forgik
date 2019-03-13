/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Disjunction extends Formula {

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
		return this.operand1.toStringNested() + " v " + this.operand2.toStringNested();
	}

	@Override
	protected boolean isStringEmbraced() {
		return true;
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
	public Formula apply(Map<String, Formula> map, Set<String> leftover) {
		Formula newOperand1 = this.operand1.apply(map, leftover);
		Formula newOperand2 = this.operand2.apply(map, leftover);
		return new Disjunction(newOperand1, newOperand2);
	}

	@Override
	public boolean containsFreeVariable(String variableName) {
		return (this.operand1.containsFreeVariable(variableName) || this.operand2.containsFreeVariable(variableName));
	}
}
