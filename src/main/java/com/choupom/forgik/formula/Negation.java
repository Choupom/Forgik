/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Negation extends Formula {

	private final Formula operand;

	public Negation(Formula operand) {
		this.operand = operand;
	}

	public Formula getOperand() {
		return this.operand;
	}

	@Override
	public String toString() {
		return "-" + this.operand.toStringNested();
	}

	@Override
	protected boolean isStringEmbraced() {
		return false;
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
	public Formula apply(Map<String, Formula> map, Set<String> leftover) {
		Formula newOperand = this.operand.apply(map, leftover);
		return new Negation(newOperand);
	}

	@Override
	public boolean containsFreeVariable(String variableName) {
		return this.operand.containsFreeVariable(variableName);
	}
}
