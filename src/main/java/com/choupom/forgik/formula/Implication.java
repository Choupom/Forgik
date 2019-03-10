/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;

public class Implication extends Formula {

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
		return this.operand1.toStringNested() + " > " + this.operand2.toStringNested();
	}

	@Override
	protected boolean isStringEmbraced() {
		return true;
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
	public boolean identify(Formula formula, Map<String, Formula> map) {
		if (!(formula instanceof Implication)) {
			return false;
		}

		Implication implication = (Implication) formula;
		return (this.operand1.identify(implication.operand1, map) && this.operand2.identify(implication.operand2, map));
	}

	@Override
	public Formula apply(Map<String, Formula> map, List<String> leftover) {
		Formula newOperand1 = this.operand1.apply(map, leftover);
		Formula newOperand2 = this.operand2.apply(map, leftover);
		return new Implication(newOperand1, newOperand2);
	}
}
