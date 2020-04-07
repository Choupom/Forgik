/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.operations;

import org.junit.Assert;
import org.junit.Test;

import com.choupom.forgik.formula.BinaryConnective;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.UnaryConnective;
import com.choupom.forgik.formula.UnaryConnective.Type;
import com.choupom.forgik.parser.FormulaParser;

public class GetStringOperationTest {

	private static final String PREFIX = "-{";
	private static final String SEPARATOR = "} = {";
	private static final String SUFFIX = "}";

	@Test
	public void test() {
		testGetString("A");
		testGetString("-B");
		testGetString("--C");
		testGetString("$1");
		testGetString("-$2");
		testGetString("A ^ $1");
		testGetString("$2 v B");
		testGetString("-(A > B)");
		testGetString("-(-A ^ B) > ($1 v -$2)");
		testGetString("--(--$1 > --$2)");
	}

	private static void testGetString(String formulaString) {
		Formula formula = FormulaParser.parse(formulaString);
		Formula negation = new UnaryConnective(Type.NEGATION, formula);

		StringBuilder expectedBuilder = new StringBuilder();
		expectedBuilder.append(PREFIX);
		expectedBuilder.append(formulaString);
		expectedBuilder.append(SEPARATOR);
		expectedBuilder.append(Type.NEGATION.getSymbol());
		if (formula instanceof BinaryConnective) {
			expectedBuilder.append('(');
		}
		expectedBuilder.append(formulaString);
		if (formula instanceof BinaryConnective) {
			expectedBuilder.append(')');
		}
		expectedBuilder.append(SUFFIX);

		StringBuilder resultBuilder = new StringBuilder();
		GetStringOperation stringOperation = new GetStringOperation(resultBuilder);
		resultBuilder.append(PREFIX);
		formula.runOperation(stringOperation, Boolean.FALSE);
		resultBuilder.append(SEPARATOR);
		negation.runOperation(stringOperation, Boolean.FALSE);
		resultBuilder.append(SUFFIX);
		Assert.assertEquals(expectedBuilder.toString(), resultBuilder.toString());
	}
}
