/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.android;

import com.choupom.forgik.challenge.Challenge;
import com.choupom.forgik.challenge.ChallengeParser;
import com.choupom.forgik.parser.FormulaParserException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class ChallengesHelper {

    private static class ChallengeComparator implements Comparator<Challenge> {
        @Override
        public int compare(Challenge c1, Challenge c2) {
            return c1.getDifficulty() - c2.getDifficulty();
        }
    }

    private static final String[] CHALLENGES = new String[] { "modus_tollens", "hypothetical_syllogism",
			"challenge1", "challenge2", "challenge3", "challenge4", "challenge5", "challenge6",
			"challenge7", "challenge8", "challenge9", "challenge10", "challenge11", "challenge12" };

    private ChallengesHelper() {
        // private constructor
    }

    public static Challenge[] loadSortedChallenges() throws IOException, FormulaParserException {
        Challenge[] challenges = new Challenge[CHALLENGES.length];
        for (int i = 0; i < CHALLENGES.length; i++) {
            challenges[i] = ChallengeParser.parseChallenge(CHALLENGES[i]);
        }

        Arrays.sort(challenges, new ChallengeComparator());
        return challenges;
    }
}
