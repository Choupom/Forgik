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
	public void testPredicate() {
		String string = "test";
		Formula formula = FormulaParser.parse(string);
		Assert.assertEquals(string, formula.toString());
	}

	@Test
	public void testSimple() {
		String string = "--(p v q) > ($1 ^ -$2)";
		Formula formula = FormulaParser.parse(string);
		Assert.assertEquals(string, formula.toString());
	}

	@Test
	public void testComplex() {
		String string = "-(p > q) > ((q v r) > (p ^ r))";
		Formula formula = FormulaParser.parse(string);
		Assert.assertEquals(string, formula.toString());
	}
}
