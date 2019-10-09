/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.parser.FormulaParser;
import com.choupom.forgik.utils.InputStreamUtils;

public class RuleParser {

	private static final String DIRECTORY = "/rules/";
	private static final String EXTENSION = ".json";

	private static final String JSON_NAME = "name";
	private static final String JSON_ASSUMPTIONS = "assumptions";
	private static final String JSON_ANTECEDENTS = "antecedents";
	private static final String JSON_CONSEQUENT = "consequent";

	private RuleParser() {
		// private constructor
	}

	public static Rule parseRule(String ruleId) throws IOException {
		String resourceName = DIRECTORY + ruleId + EXTENSION;

		String jsonString;
		try (InputStream fileInputStream = RuleParser.class.getResourceAsStream(resourceName)) {
			if (fileInputStream == null) {
				throw new IOException("Rule " + ruleId + " not found");
			}
			jsonString = InputStreamUtils.readInputStream(fileInputStream);
		}

		JSONObject rule = new JSONObject(jsonString);
		String name = rule.getString(JSON_NAME);
		JSONArray assumptions = rule.optJSONArray(JSON_ASSUMPTIONS);
		JSONArray antecedents = rule.optJSONArray(JSON_ANTECEDENTS);
		String consequent = rule.getString(JSON_CONSEQUENT);

		if (assumptions == null) {
			assumptions = new JSONArray();
		}
		if (antecedents == null) {
			antecedents = new JSONArray();
		}

		Formula[] parsedAssumptions = new Formula[assumptions.length()];
		for (int i = 0; i < assumptions.length(); i++) {
			parsedAssumptions[i] = FormulaParser.parse(assumptions.getString(i));
		}

		Formula[] parsedAntecedents = new Formula[antecedents.length()];
		for (int i = 0; i < antecedents.length(); i++) {
			parsedAntecedents[i] = FormulaParser.parse(antecedents.getString(i));
		}

		Formula parsedConsequent = FormulaParser.parse(consequent);

		return new Rule(name, parsedAssumptions, parsedAntecedents, parsedConsequent);
	}
}
