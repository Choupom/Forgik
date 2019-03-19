/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.parser;

import com.choupom.forgik.formula.Conjunction;
import com.choupom.forgik.formula.Disjunction;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Implication;
import com.choupom.forgik.formula.Negation;

class Stack {

	private final TokenInfo[] array;
	private int size;

	public Stack(int maxSize) {
		this.array = new TokenInfo[maxSize];
		this.size = 0;
	}

	public TokenInfo getFinal() {
		if (this.size != 2 && this.array[1].getToken() != Token.EOF) {
			throw new IllegalStateException();
		}
		return this.array[0];
	}
	public void shift(TokenInfo token) {
		this.array[this.size++] = token;
	}

	public void reduce() {
		while (reduceOnce()) {
			// keep reducing
		}
	}

	private boolean reduceOnce() {
		if (this.size >= 2) {
			TokenInfo prevToken1 = this.array[this.size - 1];
			TokenInfo prevToken2 = this.array[this.size - 2];

			int reduceSize = getReduceSize(prevToken1, prevToken2);

			Formula newFormula = null;

			if (reduceSize == 2) {
				newFormula = reduceUnary(prevToken2.getToken(), prevToken1.getFormula());
			} else if (reduceSize == 3) {
				TokenInfo prevToken3 = this.array[this.size - 3];
				if (prevToken1.getToken() == Token.RIGHT_PARENTHESIS) {
					if (prevToken3.getToken() != Token.LEFT_PARENTHESIS) {
						throw new IllegalStateException();
					}
					newFormula = prevToken2.getFormula();
				} else {
					newFormula = reduceBinary(prevToken2.getToken(), prevToken3.getFormula(), prevToken1.getFormula());
				}
			}

			if (newFormula != null) {
				TokenInfo newToken = new TokenInfo(Token.FORMULA, newFormula);
				this.size -= reduceSize;
				shift(newToken);
				return true;
			}
		}

		return false;
	}

	private int getReduceSize(TokenInfo prevToken1, TokenInfo prevToken2) {
		if (prevToken1.getToken() == Token.RIGHT_PARENTHESIS) {
			return 3;
		}

		switch (prevToken2.getToken()) {
		case NEGATION:
			return 2;
		case CONJUNCTION:
		case DISJUNCTION:
		case IMPLICATION:
			return 3;
		default:
			return 0;
		}
	}

	private Formula reduceUnary(Token operator, Formula f) {
		switch (operator) {
		case NEGATION:
			return new Negation(f);
		default:
			return null;
		}
	}

	private Formula reduceBinary(Token operator, Formula f1, Formula f2) {
		switch (operator) {
		case CONJUNCTION:
			return new Conjunction(f1, f2);
		case DISJUNCTION:
			return new Disjunction(f1, f2);
		case IMPLICATION:
			return new Implication(f1, f2);
		default:
			return null;
		}
	}

	public void print() {
		System.out.print("Stack content: ");
		for (int i = 0; i < this.size; i++) {
			System.out.print(this.array[i] + " ");
		}
		System.out.println();
	}
}
