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

import com.choupom.utils.InputStreamUtils;

public class RuleParser {

	private static final String DIRECTORY = "/rules/";
	private static final String EXTENSION = ".json";

	private static final String JSON_NAME = "name";
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
		JSONArray antecedents = rule.getJSONArray(JSON_ANTECEDENTS);
		String consequent = rule.getString(JSON_CONSEQUENT);

		String[] antecedentsArray = new String[antecedents.length()];
		for (int i = 0; i < antecedents.length(); i++) {
			antecedentsArray[i] = antecedents.getString(i);
		}

		return new Rule(name, antecedentsArray, consequent);
	}
}
