/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.parser;

import com.choupom.forgik.formula.Formula;

public class TokenInfo {

	private final Token token;
	private final Formula formula;

	public TokenInfo(Token token) {
		this.token = token;
		this.formula = null;
	}

	public TokenInfo(Token token, Formula formula) {
		this.token = token;
		this.formula = formula;
	}

	public Token getToken() {
		return this.token;
	}

	public Formula getFormula() {
		if (this.token != Token.FORMULA) {
			throw new IllegalStateException();
		}
		return this.formula;
	}

	@Override
	public String toString() {
		switch (this.token) {
		case FORMULA:
			return "[" + this.formula.toString() + "]";
		case LEFT_PARENTHESIS:
			return "(";
		case RIGHT_PARENTHESIS:
			return ")";
		case CONJUNCTION:
			return "^";
		case DISJUNCTION:
			return "v";
		case IMPLICATION:
			return ">";
		case NEGATION:
			return "-";
		case WHITESPACE:
			return " ";
		case EOF:
			return "$";
		default:
			return null;
		}
	}

	public static Token parseCharacter(char c) {
		switch (c) {
		case '(':
			return Token.LEFT_PARENTHESIS;
		case ')':
			return Token.RIGHT_PARENTHESIS;
		case '^':
			return Token.CONJUNCTION;
		case 'v':
			return Token.DISJUNCTION;
		case '>':
			return Token.IMPLICATION;
		case '-':
			return Token.NEGATION;
		case ' ':
		case '\t':
		case '\r':
		case '\n':
			return Token.WHITESPACE;
		default:
			return null;
		}
	}
}
