/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.challenge;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.parser.FormulaParser;
import com.choupom.forgik.utils.InputStreamUtils;

public class ChallengeParser {

	private static final String DIRECTORY = "/challenges/";
	private static final String EXTENSION = ".json";

	private static final String JSON_NAME = "name";
	private static final String JSON_DIFFICULTY = "difficulty";
	private static final String JSON_RULEBOOK = "rulebook";
	private static final String JSON_ANTECEDENTS = "antecedents";
	private static final String JSON_CONSEQUENTS = "consequents";

	private ChallengeParser() {
		// private constructor
	}

	public static Challenge parseChallenge(String challengeId) throws IOException {
		String resourceName = DIRECTORY + challengeId + EXTENSION;

		String jsonString;
		try (InputStream fileInputStream = ChallengeParser.class.getResourceAsStream(resourceName)) {
			if (fileInputStream == null) {
				throw new IOException("Challenge " + challengeId + " not found");
			}
			jsonString = InputStreamUtils.readInputStream(fileInputStream);
		}

		JSONObject challenge = new JSONObject(jsonString);
		String name = challenge.getString(JSON_NAME);
		int difficulty = challenge.getInt(JSON_DIFFICULTY);
		String rulebook = challenge.getString(JSON_RULEBOOK);
		JSONArray antecedents = challenge.getJSONArray(JSON_ANTECEDENTS);
		JSONArray consequents = challenge.getJSONArray(JSON_CONSEQUENTS);

		Formula[] antecedentsArray = new Formula[antecedents.length()];
		for (int i = 0; i < antecedents.length(); i++) {
			String antecedent = antecedents.getString(i);
			antecedentsArray[i] = FormulaParser.parse(antecedent);
		}

		Formula[] consequentsArray = new Formula[consequents.length()];
		for (int i = 0; i < consequents.length(); i++) {
			String consequent = consequents.getString(i);
			consequentsArray[i] = FormulaParser.parse(consequent);
		}

		return new Challenge(name, difficulty, rulebook, Formulas.wrap(antecedentsArray),
				Formulas.wrap(consequentsArray));
	}
}
