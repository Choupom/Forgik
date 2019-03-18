/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Conjunction extends Formula {

	private final Formula operand1;
	private final Formula operand2;

	public Conjunction(Formula operand1, Formula operand2) {
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
		return this.operand1.toStringNested() + " ^ " + this.operand2.toStringNested();
	}

	@Override
	protected boolean isStringEmbraced() {
		return true;
	}

	@Override
	public boolean checkEquals(Formula formula) {
		if (!(formula instanceof Conjunction)) {
			return false;
		}

		Conjunction conjunction = (Conjunction) formula;
		return (this.operand1.checkEquals(conjunction.operand1) && this.operand2.checkEquals(conjunction.operand2));
	}

	@Override
	public boolean identify(Formula formula, Map<String, List<Formula>> map) {
		if (formula instanceof FreeVariable) {
			FreeVariable variable = (FreeVariable) formula;
			return variable.identify(this, map);
		}

		if (!(formula instanceof Conjunction)) {
			return false;
		}

		Conjunction conjunction = (Conjunction) formula;
		return (this.operand1.identify(conjunction.operand1, map) && this.operand2.identify(conjunction.operand2, map));
	}

	@Override
	public Formula apply(Map<String, Formula> map) {
		Formula newOperand1 = this.operand1.apply(map);
		Formula newOperand2 = this.operand2.apply(map);
		return new Conjunction(newOperand1, newOperand2);
	}

	@Override
	public void getFreeVariables(Set<String> variables) {
		this.operand1.getFreeVariables(variables);
		this.operand2.getFreeVariables(variables);
	}
}
