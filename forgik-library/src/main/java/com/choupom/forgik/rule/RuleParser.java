/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FreeFormula;
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
			parsedAssumptions[i] = parseFormula(assumptions.getString(i));
		}

		Formula[] parsedAntecedents = new Formula[antecedents.length()];
		for (int i = 0; i < antecedents.length(); i++) {
			parsedAntecedents[i] = parseFormula(antecedents.getString(i));
		}

		Formula parsedConsequent = parseFormula(consequent);

		return new Rule(name, parsedAssumptions, parsedAntecedents, parsedConsequent);
	}

	private static Formula parseFormula(String string) {
		Formula formula = FormulaParser.parse(string);

		Set<Integer> freeFormulas = new HashSet<>();
		formula.getFreeFormulas(freeFormulas);

		Map<Integer, Formula> map = new HashMap<>();
		for (Integer freeFormula : freeFormulas) {
			map.put(freeFormula, new FreeFormula(-freeFormula.intValue() - 1)); // replace id by negative id
		}

		return formula.apply(map);
	}
}
