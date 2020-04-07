/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.operations;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.operations.util.MapBuilder;
import com.choupom.forgik.parser.FormulaParser;


public class IdentifyOperationTest {

	@Test
	public void test() {
		testIdentify("$1", "P", true, new MapBuilder().add(1, "P"));
		testIdentify("$1 > $1", "P > P", true, new MapBuilder().add(1, "P"));
		testIdentify("$1 > $2", "P > Q", true, new MapBuilder().add(1, "P").add(2, "Q"));
		testIdentify("$1", "$1", true, new MapBuilder());
		testIdentify("$1 > $2", "P > $2", true, new MapBuilder().add(1, "P"));
		testIdentify("-$1 ^ ($2 v -$3)", "-P ^ (Q v -R)", true, new MapBuilder().add(1, "P").add(2, "Q").add(3, "R"));
		testIdentify("$1 > -$2", "P > -Q", true, new MapBuilder().add(1, "P").add(2, "Q"));
		testIdentify("-(A v B)", "-(A v B)", true, new MapBuilder());
		testIdentify("$1", "A ^ B", true, new MapBuilder().add(1, "A ^ B"));
		testIdentify("$1 > $1", "$2 > $2", true, new MapBuilder().add(1, "$2").add(2, "$1"));
		testIdentify("$1", "$2 > $2", true, new MapBuilder().add(1, "$2 > $2"));

		testIdentify("A v B", "$1 ^ $2", false, null);
		testIdentify("A", "B", false, null);

		testIdentify("$1 > $1", "A > B", true, null);
		testIdentify("$1 v (C ^ $3)", "(A ^ B) v (C ^ $1)", true, null);
		testIdentify("$1 ^ $1", "A ^ -$2", true, null);
		testIdentify("$1 ^ $1", "A ^ $2", true, null);

		testIdentify("$1 > $1", "A > B", false, new MapBuilder().add(1, "A"));
		testIdentify("$1 v (C ^ $2)", "(A ^ B) v (C ^ $1)", false, new MapBuilder().add(1, "A ^ B").add(2, "$1"));
		testIdentify("$1 ^ $1", "A ^ -$2", false, new MapBuilder().add(1, "A"));
		testIdentify("$1 ^ $1", "A ^ $2", false, new MapBuilder().add(1, "A").add(2, "$1"));
	}

	private static void testIdentify(String string1, String string2, boolean assertAllEquals, MapBuilder mapBuilder) {
		Formula formula1 = FormulaParser.parse(string1);
		Formula formula2 = FormulaParser.parse(string2);

		Map<Integer, Formula> resultMap = new HashMap<>();
		IdentifyOperation identifyOperation = new IdentifyOperation(assertAllEquals, resultMap);
		boolean result = formula1.runOperation(identifyOperation, formula2);
		checkResult(result, resultMap, mapBuilder);

		// check that we can use the operation multiple times
		resultMap.clear();
		result = formula2.runOperation(identifyOperation, formula1);
		checkResult(result, resultMap, mapBuilder);
	}

	private static void checkResult(boolean result, Map<Integer, Formula> resultMap, MapBuilder mapBuilder) {
		if (mapBuilder == null) {
			Assert.assertFalse(result);
		} else {
			Assert.assertTrue(result);
			Assert.assertEquals(mapBuilder.build(), resultMap);
		}
	}
}
