/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.operations;

import org.junit.Assert;
import org.junit.Test;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.parser.FormulaParser;

public class EqualsOperationTest {

	@Test
	public void test() {
		testEquals(new String[] { //
				"A", //
				"B", //
				"$1", //
				"$2", //
				"-A", //
				"--A", //
				"A > B", //
				"A ^ B", //
				"A v B", //
				"-(A ^ B)", //
				"-A v B", //
				"B > A" //
		});
	}

	private static void testEquals(String[] strings) {
		Formula[] formulas = new Formula[strings.length];
		for (int i = 0; i < strings.length; i++) {
			formulas[i] = FormulaParser.parse(strings[i]);
		}

		EqualsOperation equalsOperation = new EqualsOperation();
		for (int i = 0; i < formulas.length; i++) {
			Formula duplicate = FormulaParser.parse(strings[i]);
			Assert.assertTrue(strings[i], formulas[i].runOperation(equalsOperation, duplicate));
			Assert.assertTrue(strings[i], duplicate.runOperation(equalsOperation, formulas[i]));

			for (int j = 0; j < formulas.length; j++) {
				if (i != j) {
					Assert.assertFalse(formulas[i].runOperation(equalsOperation, formulas[j]));
				}
			}
		}
	}
}
