/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.android.challenge;

import com.choupom.forgik.formula.Formula;

public class Challenge {

    private final Formula[] antecedents;
    private final Formula[] consequents;
    private final int difficulty;

    public Challenge(Formula[] antecedents, Formula[] consequents, int difficulty) {
        this.antecedents = antecedents;
        this.consequents = consequents;
        this.difficulty = difficulty;
    }

    public Formula[] getAntecedents() {
        return this.antecedents;
    }

    public Formula[] getConsequents() {
        return this.consequents;
    }

    public int getDifficulty() {
        return this.difficulty;
    }
}
