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
import com.choupom.forgik.parser.FormulaParserException;

public class FormulaIdentifierTest {

	@Test
	public void test() throws FormulaParserException {
		testIdentify("$1 v Q", "P v $1", null);
		testIdentify("$1 v Q", "$2 v $2", "Q v Q");
		testIdentify("($1 v $2) v $3", "($2 v $3) v $1", "($1 v $1) v $1");
		testIdentify("$1 ^ $2", "($2 ^ Q) ^ Q", "(Q ^ Q) ^ Q");
		testIdentify("$1 v $1", "P v Q", null);
		testIdentify("$1 v ($1 v $1)", "($1 v $1) v $1", null);
		testIdentify("$1", "$1 v P", null);
		testIdentify("$1 v $2", "$2 v $1", "$1 v $1");
		testIdentify("$1 v P", "$2 v $1", "P v P");
		testIdentify("$1 v $1", "$2 v P", "P v P");
		testIdentify("($1 ^ P) ^ $1", "$2 ^ $2", null);
		testIdentify("($1 v $2) v $3", "($2 v $3) v P", "(P v P) v P");
		testIdentify("$1 v P", "($2 > $3) v $4", "($2 > $3) v P");
		testIdentify("$1 v P", "Q v $1", null);
		testIdentify("P v $1", "$1 v Q", null);
	}

	private static void testIdentify(String string1, String string2, String stringR) throws FormulaParserException {
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
