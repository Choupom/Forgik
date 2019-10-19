/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.challenge;

import com.choupom.forgik.formula.Formula;

public class Challenge {

	private final String name;
	private final int difficulty;
	private final String rulebook;
	private final Formula[] antecedents;
	private final Formula[] consequents;

	public Challenge(String name, int difficulty, String rulebook, Formula[] antecedents, Formula[] consequents) {
		this.name = name;
		this.difficulty = difficulty;
		this.rulebook = rulebook;
		this.antecedents = antecedents.clone();
		this.consequents = consequents.clone();
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

	public Formula[] getAntecedents() {
		return this.antecedents.clone();
	}

	public Formula[] getConsequents() {
		return this.consequents.clone();
	}
}
