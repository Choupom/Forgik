/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.parser;

import org.junit.Assert;
import org.junit.Test;

import com.choupom.forgik.formula.Formula;

public class FormulaParserTest {

	@Test
	public void testInvalidString1() {
		String string = "TEST";
		try {
			FormulaParser.parse(string);
		} catch (FormulaParserException e) {
			return; // expected exception
		}
		Assert.fail();
	}

	@Test
	public void testInvalidString2() {
		String string = "t";
		try {
			FormulaParser.parse(string);
		} catch (FormulaParserException e) {
			return; // expected exception
		}
		Assert.fail();
	}

	@Test
	public void testPredicate() throws FormulaParserException {
		String string = "T";
		Formula formula = FormulaParser.parse(string);
		Assert.assertEquals(string, formula.toString());
	}

	@Test
	public void testPredicates() throws FormulaParserException {
		String string = "-(P > Q) > ((-Q v R) > (P ^ R))";
		Formula formula = FormulaParser.parse(string);
		Assert.assertEquals(string, formula.toString());
	}

	@Test
	public void testFreeFormulas() throws FormulaParserException {
		String string = "--($1 v $2) > ($1 ^ -$3)";
		Formula formula = FormulaParser.parse(string);
		Assert.assertEquals(string, formula.toString());
	}
}
