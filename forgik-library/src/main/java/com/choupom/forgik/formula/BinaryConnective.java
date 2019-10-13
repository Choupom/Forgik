/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BinaryConnective implements Formula {

	public enum Type {

		IMPLICATION(">"), CONJUNCTION("^"), DISJUNCTION("v");

		private final String symbol;

		private Type(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return this.symbol;
		}
	}

	private final Type type;
	private final Formula operand1;
	private final Formula operand2;

	public BinaryConnective(Type type, Formula operand1, Formula operand2) {
		this.type = type;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	public Type getType() {
		return this.type;
	}

	public Formula getOperand1() {
		return this.operand1;
	}

	public Formula getOperand2() {
		return this.operand2;
	}

	@Override
	public String toString() {
		return this.operand1.toStringNested() + " " + this.type.getSymbol() + " " + this.operand2.toStringNested();
	}

	@Override
	public String toStringNested() {
		return "(" + toString() + ")";
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof BinaryConnective)) {
			return false;
		}

		BinaryConnective connective = (BinaryConnective) object;
		return (this.type == connective.type && this.operand1.equals(connective.operand1)
				&& this.operand2.equals(connective.operand2));
	}

	@Override
	public boolean identify(Formula formula, Map<String, List<Formula>> map) {
		if (formula instanceof FreeFormula) {
			FreeFormula freeFormula = (FreeFormula) formula;
			return freeFormula.identify(this, map);
		}

		if (!(formula instanceof BinaryConnective)) {
			return false;
		}

		BinaryConnective connective = (BinaryConnective) formula;
		return (this.type == connective.type && this.operand1.identify(connective.operand1, map)
				&& this.operand2.identify(connective.operand2, map));
	}

	@Override
	public Formula apply(Map<String, Formula> map) {
		Formula newOperand1 = this.operand1.apply(map);
		Formula newOperand2 = this.operand2.apply(map);
		return new BinaryConnective(this.type, newOperand1, newOperand2);
	}

	@Override
	public void getFreeFormulas(Set<String> freeFormulas) {
		this.operand1.getFreeFormulas(freeFormulas);
		this.operand2.getFreeFormulas(freeFormulas);
	}
}
