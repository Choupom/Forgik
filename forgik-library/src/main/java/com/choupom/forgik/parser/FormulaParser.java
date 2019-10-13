/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.parser;

import java.util.ArrayList;
import java.util.List;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FreeFormula;
import com.choupom.forgik.formula.Predicate;

public class FormulaParser {

	public static Formula parse(String string) {
		TokenInfo[] tokens = tokenize(string.toCharArray());
		return createFormula(tokens);
	}

	private static TokenInfo[] tokenize(char[] chars) {
		List<TokenInfo> tokens = new ArrayList<>(chars.length + 1);

		int stringStart = -1;
		for (int i = 0; i < chars.length; i++) {
			Token token = TokenInfo.parseCharacter(chars[i]);
			if (token != null) {
				if (stringStart != -1) {
					String string = new String(chars, stringStart, i - stringStart);
					Formula formula = createFormulaFromString(string);
					tokens.add(new TokenInfo(Token.FORMULA, formula));
					stringStart = -1;
				}
				if (token != Token.WHITESPACE) {
					tokens.add(new TokenInfo(token));
				}
			} else if (stringStart == -1) {
				stringStart = i;
			}
		}

		if (stringStart != -1) {
			String string = new String(chars, stringStart, chars.length - stringStart);
			Formula formula = createFormulaFromString(string);
			tokens.add(new TokenInfo(Token.FORMULA, formula));
		}

		tokens.add(new TokenInfo(Token.EOF));
		return tokens.toArray(new TokenInfo[tokens.size()]);
	}

	private static Formula createFormulaFromString(String string) {
		if (Character.isLowerCase(string.charAt(0))) {
			return new Predicate(string);
		} else {
			return new FreeFormula(string);
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
		}

		return stack.getFinal().getFormula();
	}
}
