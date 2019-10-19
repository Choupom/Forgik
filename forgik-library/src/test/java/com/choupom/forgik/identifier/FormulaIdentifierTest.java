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
		testIdentify("$1 v q", "p v $1", null);
		testIdentify("$1 v q", "$2 v $2", "q v q");
		testIdentify("($1 v $2) v $3", "($2 v $3) v $1", "($1 v $1) v $1");
		testIdentify("$1 ^ $2", "($2 ^ q) ^ q", "(q ^ q) ^ q");
		testIdentify("$1 v $1", "p v q", null);
		testIdentify("$1 v ($1 v $1)", "($1 v $1) v $1", null);
		testIdentify("$1", "$1 v p", null);
		testIdentify("$1 v $2", "$2 v $1", "$1 v $1");
		testIdentify("$1 v p", "$2 v $1", "p v p");
		testIdentify("$1 v $1", "$2 v p", "p v p");
		testIdentify("($1 ^ p) ^ $1", "$2 ^ $2", null);
		testIdentify("($1 v $2) v $3", "($2 v $3) v p", "(p v p) v p");
		testIdentify("$1 v p", "($2 > $3) v $4", "($2 > $3) v p");
		testIdentify("$1 v p", "q v $1", null);
		testIdentify("p v $1", "$1 v q", null);
	}

	private static void testIdentify(String string1, String string2, String stringR) {
		Formula formula1 = FormulaParser.parse(string1);
		Formula formula2 = FormulaParser.parse(string2);
		Identification result = FormulaIdentifier.identify(formula1, formula2);
		if (stringR == null) {
			Assert.assertNull(result);
		} else {
			Assert.assertEquals(stringR, result.getFormula().toString());
			Formula mapped1 = formula1.apply(result.getMap());
			Formula mapped2 = formula2.apply(result.getMap());
			Assert.assertTrue(mapped1.equals(mapped2));
			Assert.assertTrue(mapped2.equals(mapped1));
		}
	}
}
