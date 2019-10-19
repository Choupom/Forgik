/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rulebook;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.choupom.forgik.parser.FormulaParserException;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rule.RuleParser;
import com.choupom.forgik.utils.InputStreamUtils;

public class RulebookParser {

	private static final String DIRECTORY = "/rulebooks/";
	private static final String EXTENSION = ".json";

	private static final String JSON_NAME = "name";
	private static final String JSON_RULES = "rules";

	private RulebookParser() {
		// private constructor
	}

	public static Rulebook parseRulebook(String rulebookId) throws IOException, FormulaParserException {
		String resourceName = DIRECTORY + rulebookId + EXTENSION;

		String jsonString;
		try (InputStream fileInputStream = RulebookParser.class.getResourceAsStream(resourceName)) {
			if (fileInputStream == null) {
				throw new IOException("Rulebook " + rulebookId + " not found");
			}
			jsonString = InputStreamUtils.readInputStream(fileInputStream);
		}

		JSONObject rulebook = new JSONObject(jsonString);
		String name = rulebook.getString(JSON_NAME);
		JSONArray rules = rulebook.getJSONArray(JSON_RULES);

		Rule[] rulesArray = new Rule[rules.length()];
		for (int i = 0; i < rules.length(); i++) {
			String ruleName = rules.getString(i);
			rulesArray[i] = RuleParser.parseRule(ruleName);
		}

		return new Rulebook(name, rulesArray);
	}
}
