/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UnaryConnective implements Formula {

	public enum Type {

		NEGATION("-");

		private final String symbol;

		private Type(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return this.symbol;
		}
	}

	private final Type type;
	private final Formula operand;

	public UnaryConnective(Type type, Formula operand) {
		this.type = type;
		this.operand = operand;
	}

	public Type getType() {
		return this.type;
	}

	public Formula getOperand() {
		return this.operand;
	}

	@Override
	public String toString() {
		return this.type.getSymbol() + this.operand.toStringNested();
	}

	@Override
	public String toStringNested() {
		return toString();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof UnaryConnective)) {
			return false;
		}

		UnaryConnective connective = (UnaryConnective) object;
		return (this.type == connective.type && this.operand.equals(connective.operand));
	}

	@Override
	public boolean identify(Formula formula, Map<String, List<Formula>> map) {
		if (formula instanceof FreeFormula) {
			FreeFormula freeFormula = (FreeFormula) formula;
			return freeFormula.identify(this, map);
		}

		if (!(formula instanceof UnaryConnective)) {
			return false;
		}

		UnaryConnective connective = (UnaryConnective) formula;
		return (this.type == connective.type && this.operand.identify(connective.operand, map));
	}

	@Override
	public Formula apply(Map<String, Formula> map) {
		Formula newOperand = this.operand.apply(map);
		return new UnaryConnective(this.type, newOperand);
	}

	@Override
	public void getFreeFormulas(Set<String> freeFormulas) {
		this.operand.getFreeFormulas(freeFormulas);
	}
}
