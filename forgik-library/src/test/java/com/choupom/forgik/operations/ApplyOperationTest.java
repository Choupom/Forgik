/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.operations;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Predicate;
import com.choupom.forgik.operations.util.MapBuilder;
import com.choupom.forgik.parser.FormulaParser;

public class ApplyOperationTest {

	@Test
	public void test() {
		testApply("$1", new MapBuilder().add(1, "P"), "P");
		testApply("$1 > $1", new MapBuilder().add(1, "P"), "P > P");
		testApply("$1 > $2", new MapBuilder().add(1, "P").add(2, "Q"), "P > Q");
		testApply("$1", new MapBuilder(), "$1");
		testApply("$1", new MapBuilder().add(2, "P"), "$1");
		testApply("$1 > $2", new MapBuilder().add(1, "P"), "P > $2");
		testApply("-$1 ^ ($2 v -$3)", new MapBuilder().add(1, "P").add(2, "Q").add(3, "R"), "-P ^ (Q v -R)");
		testApply("$1 > -$2", new MapBuilder().add(1, "P").add(2, "Q").add(3, "R"), "P > -Q");
		testApply("-(A v B)", new MapBuilder().add(1, "P"), "-(A v B)");
		testApply("$1", new MapBuilder().add(1, "A ^ B"), "A ^ B");
		testApply("$1 > $1", new MapBuilder().add(1, "$2"), "$2 > $2");
		testApply("$1", new MapBuilder().add(1, "$2 > $2"), "$2 > $2");
	}

	private static void testApply(String formulaString, MapBuilder mapBuilder, String expectedString) {
		Formula formula = FormulaParser.parse(formulaString);
		Formula expected = FormulaParser.parse(expectedString);
		Map<Integer, Formula> map = mapBuilder.build();

		ApplyOperation applyOperation = new ApplyOperation(map);
		map.clear(); // make sure the operation caches the map
		Formula result = formula.runOperation(applyOperation);
		Assert.assertEquals(expected, result);

		// check that we can use the operation multiple times
		formula = new Predicate('S');
		result = formula.runOperation(applyOperation);
		Assert.assertEquals(formula, result);
	}
}
