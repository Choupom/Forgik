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

public class ContainsFreeFormulaOperationTest {

	private static final int NUM_FREE_FORMULAS = 5;

	@Test
	public void test() {
		testContains("$1", new int[] { 1 });
		testContains("$2", new int[] { 2 });
		testContains("$1 > $1", new int[] { 1 });
		testContains("$1 > $2", new int[] { 1, 2 });
		testContains("-$1 ^ ($2 v -$3)", new int[] { 1, 2, 3 });
		testContains("-(A v B)", new int[] {});
	}

	private static void testContains(String formulaString, int[] expectedFreeFormulas) {
		Formula formula = FormulaParser.parse(formulaString);

		// save the operations in memory to check that we can use an operation multiple times
		ContainsFreeFormulaOperation[] containsOperations = new ContainsFreeFormulaOperation[NUM_FREE_FORMULAS];
		for (int i = 0; i < NUM_FREE_FORMULAS; i++) {
			containsOperations[i] = new ContainsFreeFormulaOperation(i);
		}

		// test each free formula
		for (int i = 0; i < NUM_FREE_FORMULAS; i++) {
			boolean expected = contains(expectedFreeFormulas, i);
			boolean result = formula.runOperation(containsOperations[i]);
			Assert.assertEquals(expected, result);
		}
	}

	private static boolean contains(int[] haystack, int needle) {
		for (int i = 0; i < haystack.length; i++) {
			if (haystack[i] == needle) {
				return true;
			}
		}
		return false;
	}
}
