/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

public class UnaryConnective extends Formula {

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
	public <R, P> R runOperation(FormulaOperation<R, P> operation, P param) {
		return operation.handleUnaryConnective(this, param);
	}
}
