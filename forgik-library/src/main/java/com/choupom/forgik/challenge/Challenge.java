/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.challenge;

import com.choupom.forgik.formula.Formulas;

public class Challenge {

	private final String name;
	private final int difficulty;
	private final String rulebook;
	private final Formulas antecedents;
	private final Formulas consequents;

	public Challenge(String name, int difficulty, String rulebook, Formulas antecedents, Formulas consequents) {
		this.name = name;
		this.difficulty = difficulty;
		this.rulebook = rulebook;
		this.antecedents = antecedents;
		this.consequents = consequents;
	}

	public String getName() {
		return this.name;
	}

	public int getDifficulty() {
		return this.difficulty;
	}

	public String getRulebook() {
		return this.rulebook;
	}

	public Formulas getAntecedents() {
		return this.antecedents;
	}

	public Formulas getConsequents() {
		return this.consequents;
	}
}
