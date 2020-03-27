/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

public class BinaryConnective extends Formula {

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
	public <R, P> R runOperation(FormulaOperation<R, P> operation, P param) {
		return operation.handleBinaryConnective(this, param);
	}
}
