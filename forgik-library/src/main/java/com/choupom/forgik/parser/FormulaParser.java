/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.parser;

import java.util.ArrayList;
import java.util.List;

import com.choupom.forgik.formula.BoundVariable;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FreeVariable;

public class FormulaParser {

	public static Formula parse(String string) {
		TokenInfo[] tokens = tokenize(string);
		return createFormula(tokens);
	}

	private static TokenInfo[] tokenize(String string) {
		List<TokenInfo> tokens = new ArrayList<>(string.length() + 1);
		char[] chars = string.toCharArray();

		int variableStart = -1;
		for (int i = 0; i < chars.length; i++) {
			Token token = TokenInfo.parseCharacter(chars[i]);
			if (token != null) {
				if (variableStart != -1) {
					String variableName = new String(chars, variableStart, i - variableStart);
					Formula variable = createVariable(variableName);
					tokens.add(new TokenInfo(Token.FORMULA, variable));
					variableStart = -1;
				}
				if (token != Token.WHITESPACE) {
					tokens.add(new TokenInfo(token));
				}
			} else if (variableStart == -1) {
				variableStart = i;
			}
		}

		if (variableStart != -1) {
			String variableName = new String(chars, variableStart, chars.length - variableStart);
			Formula variable = createVariable(variableName);
			tokens.add(new TokenInfo(Token.FORMULA, variable));
		}

		tokens.add(new TokenInfo(Token.EOF));
		return tokens.toArray(new TokenInfo[tokens.size()]);
	}

	private static Formula createVariable(String variableName) {
		if (Character.isLowerCase(variableName.charAt(0))) {
			return new BoundVariable(variableName);
		} else {
			return new FreeVariable(variableName);
		}
	}

	private static Formula createFormula(TokenInfo[] tokens) {
		if (tokens.length == 0) {
			return null;
		}

		Stack stack = new Stack(tokens.length);
		int state = 0;

		for (TokenInfo tokenInfo : tokens) {
			Token token = tokenInfo.getToken();
			// System.out.println("Token " + token + " at state " + state);

			if (state == 0) {
				if (token == Token.FORMULA) {
					state = 1;
				} else if (token == Token.NEGATION || token == Token.LEFT_PARENTHESIS) {
					state = 0;
				} else {
					System.out.println("Invalid token " + token + " at state " + state);
					throw new IllegalStateException();
				}
				stack.shift(tokenInfo);
			} else if (state == 1) {
				if (token == Token.CONJUNCTION || token == Token.DISJUNCTION || token == Token.IMPLICATION) {
					state = 0;
				} else if (token == Token.RIGHT_PARENTHESIS) {
					state = 1;
				} else if (token == Token.EOF) {
					state = 2;
				} else {
					System.out.println("Invalid token " + token + " at state " + state);
					throw new IllegalStateException();
				}
				stack.reduce();
				stack.shift(tokenInfo);
			} else if (state == 2) {
				System.out.println("Invalid token " + token + " at state " + state);
				throw new IllegalStateException();
			}

			// stack.print();
		}

		return stack.getFinal().getFormula();
	}
}
