/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.identifier;

import org.junit.Assert;
import org.junit.Test;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.parser.FormulaParser;

public class FormulaIdentifierTest {

	@Test
	public void test() {
		testIdentify("A v q", "p v A", null);
		testIdentify("A v q", "B v B", "q v q");
		testIdentify("(A v B) v C", "(B v C) v A", "(A v A) v A");
		testIdentify("A ^ B", "(B ^ q) ^ q", "(q ^ q) ^ q");
		testIdentify("A v A", "p v q", null);
		testIdentify("A v (A v A)", "(A v A) v A", null);
		testIdentify("A", "A v p", null);
		testIdentify("A v B", "B v A", "A v A");
		testIdentify("A v p", "B v A", "p v p");
		testIdentify("A v A", "B v p", "p v p");
		testIdentify("(A ^ p) ^ A", "B ^ B", null);
		testIdentify("(A v B) v C", "(B v C) v p", "(p v p) v p");
		testIdentify("A v p", "(B > C) v D", "(B > C) v p");
		testIdentify("A v p", "q v A", null);
		testIdentify("p v A", "A v q", null);
	}

	private static void testIdentify(String string1, String string2, String stringR) {
		Formula formula1 = FormulaParser.parse(string1);
		Formula formula2 = FormulaParser.parse(string2);
		Identification result = FormulaIdentifier.identify(formula1, formula2);
		if (stringR == null) {
			Assert.assertNull(result);
		} else {
			Assert.assertEquals(stringR, result.getFormula().toString());
			Formula mapped1 = formula1.apply(result.getMap(), null);
			Formula mapped2 = formula2.apply(result.getMap(), null);
			Assert.assertTrue(mapped1.checkEquals(mapped2));
			Assert.assertTrue(mapped2.checkEquals(mapped1));
		}
	}
}
