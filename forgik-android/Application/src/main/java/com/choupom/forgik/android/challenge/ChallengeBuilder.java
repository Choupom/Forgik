/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.android.challenge;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.parser.FormulaParser;

import java.util.ArrayList;
import java.util.List;

public class ChallengeBuilder {

    private final List<Formula> antecedents;
    private final List<Formula> consequents;
    private final int difficulty;

    public ChallengeBuilder(int difficulty) {
        this.antecedents = new ArrayList<>();
        this.consequents = new ArrayList<>();
        this.difficulty = difficulty;
    }

    public void addAntecedent(String antecedentString) {
        Formula antecedent = FormulaParser.parse(antecedentString);
        this.antecedents.add(antecedent);
    }

    public void addConsequent(String consequentString) {
        Formula consequent = FormulaParser.parse(consequentString);
        this.consequents.add(consequent);
    }

    public Challenge build() {
        Formula[] antecedents = this.antecedents.toArray(new Formula[this.antecedents.size()]);
        Formula[] consequents = this.consequents.toArray(new Formula[this.consequents.size()]);
        return new Challenge(antecedents, consequents, this.difficulty);
    }
}
