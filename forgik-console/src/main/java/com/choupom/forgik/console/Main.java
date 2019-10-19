/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.console;

import java.io.IOException;

import com.choupom.forgik.challenge.Challenge;
import com.choupom.forgik.challenge.ChallengeParser;
import com.choupom.forgik.parser.FormulaParserException;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rulebook.Rulebook;
import com.choupom.forgik.rulebook.RulebookParser;

public class Main {

	private static final String CHALLENGE = "challenge3";

	public static void main(String[] args) throws IOException, FormulaParserException {
		Challenge challenge = ChallengeParser.parseChallenge(CHALLENGE);
		Rulebook rulebook = RulebookParser.parseRulebook(challenge.getRulebook());
		Rule[] rules = rulebook.getRules();
		ConsoleProver.prove(challenge.getAntecedents(), challenge.getConsequents(), rules);
	}
}
