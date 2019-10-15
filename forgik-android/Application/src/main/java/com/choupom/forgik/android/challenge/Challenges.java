/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.android.challenge;

import java.util.ArrayList;
import java.util.List;

public class Challenges {

    private static final Challenge[] CHALLENGES = createChallenges();

    private Challenges() {
        // private constructor
    }

    public static int getNumChallenges() {
        return CHALLENGES.length;
    }

    public static Challenge getChallenge(int index) {
        return CHALLENGES[index];
    }

    private static Challenge[] createChallenges() {
        List<Challenge> challenges = new ArrayList<>();
        challenges.add(getChallenge0());
        challenges.add(getChallenge1());
        challenges.add(getChallenge2());
        challenges.add(getChallenge3());
        challenges.add(getChallenge4());
        challenges.add(getChallenge5());
        challenges.add(getChallenge6());
        challenges.add(getChallenge7());
        challenges.add(getChallenge8());
        challenges.add(getChallenge9());
        challenges.add(getChallenge10());
        challenges.add(getChallenge11());
        return challenges.toArray(new Challenge[challenges.size()]);
    }

    private static Challenge getChallenge0() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addAntecedent("p > q");
        builder.addAntecedent("q > r");
        builder.addConsequent("p > r");
        return builder.build();
    }

    private static Challenge getChallenge1() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addAntecedent("p");
        builder.addConsequent("(p v p) ^ p");
        return builder.build();
    }

    private static Challenge getChallenge2() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addAntecedent("p");
        builder.addConsequent("--p");
        return builder.build();
    }

    private static Challenge getChallenge3() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addAntecedent("-(p > q)");
        builder.addConsequent("p");
        builder.addConsequent("-q");
        return builder.build();
    }

    private static Challenge getChallenge4() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addAntecedent("p > q");
        builder.addConsequent("-(p ^ -q)");
        return builder.build();
    }

    private static Challenge getChallenge5() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addAntecedent("p v (p ^ p)");
        builder.addConsequent("p");
        return builder.build();
    }

    private static Challenge getChallenge6() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addAntecedent("p v q");
        builder.addAntecedent("-p");
        builder.addConsequent("q");
        return builder.build();
    }

    private static Challenge getChallenge7() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addAntecedent("p > q");
        builder.addAntecedent("-q");
        builder.addConsequent("-p");
        return builder.build();
    }

    private static Challenge getChallenge8() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addConsequent("p v -p");
        return builder.build();
    }

    private static Challenge getChallenge9() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addConsequent("-(p ^ -p)");
        return builder.build();
    }

    private static Challenge getChallenge10() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addAntecedent("-p");
        builder.addConsequent("p > q");
        return builder.build();
    }

    private static Challenge getChallenge11() {
        ChallengeBuilder builder = new ChallengeBuilder(0);
        builder.addAntecedent("p v q");
        builder.addAntecedent("-p v r");
        builder.addConsequent("q v r");
        return builder.build();
    }
}
