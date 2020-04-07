/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.operations;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FreeFormula;
import com.choupom.forgik.parser.FormulaParser;

public class GetFreeFormulaOperationTest {

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

		Set<Integer> expectedSet = new HashSet<Integer>();
		for (int expectedFreeFormula : expectedFreeFormulas) {
			expectedSet.add(Integer.valueOf(expectedFreeFormula));
		}

		Set<Integer> resultSet = new HashSet<>();
		GetFreeFormulasOperation getOperation = new GetFreeFormulasOperation(resultSet);
		formula.runOperation(getOperation);
		Assert.assertEquals(expectedSet, resultSet);

		// check that we can use the operation multiple times
		expectedSet.add(4);
		formula = new FreeFormula(4);
		formula.runOperation(getOperation);
		Assert.assertEquals(expectedSet, resultSet);

		// check that we can use the operation multiple times
		expectedSet.clear();
		expectedSet.add(5);
		resultSet.clear();
		formula = new FreeFormula(5);
		formula.runOperation(getOperation);
		Assert.assertEquals(expectedSet, resultSet);
	}
}
