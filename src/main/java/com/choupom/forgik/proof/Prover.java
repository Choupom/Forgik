/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof;

import java.util.HashMap;
import java.util.Map;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FreeVariable;
import com.choupom.forgik.formula.Implication;
import com.choupom.forgik.formula.Negation;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.suggester.SuggestionReverse;

public class Prover {

	private static class Proof {

		public Formula[] antecedents;
		public Formula[] consequents;
		public boolean[] completedConsequents;
		public Map<String, Formula> map;

		public Proof parent;
		public int parentConsequentId;

		public Proof(Formula[] antecedents, Formula[] consequents, Proof parent, int parentConsequentId) {
			this.antecedents = antecedents.clone();
			this.consequents = consequents.clone();
			this.completedConsequents = new boolean[consequents.length];
			this.map = new HashMap<>();

			this.parent = parent;
			this.parentConsequentId = parentConsequentId;
		}
	}

	private static int UniqueVariableCounter = 0;

	private Proof proof;

	public Prover(Formula[] antecedents, Formula consequents[]) {
		this.proof = new Proof(antecedents, consequents, null, -1);
	}

	public ProofInfo getProofInfo() {
		if (this.proof == null) {
			return null;
		}
		return new ProofInfo(this.proof.antecedents, this.proof.consequents, this.proof.completedConsequents);
	}

	public void cancelProof() {
		this.proof = this.proof.parent;
	}

	public void completeConsequent(int consequentId, Identification identification) {
		completeConsequent(consequentId, identification.getMap());
	}

	public void proveImplication(int consequentId) {
		Formula consequent = this.proof.consequents[consequentId];

		Implication implication;
		if (consequent instanceof Implication) {
			implication = (Implication) consequent;
		} else if (consequent instanceof FreeVariable) {
			Formula freeVariable1 = createUniqueVariable();
			Formula freeVariable2 = createUniqueVariable();
			implication = new Implication(freeVariable1, freeVariable2);

			Map<String, Formula> map = new HashMap<>();
			map.put(((FreeVariable) consequent).getName(), implication);
			updateProofInfo(this.proof, map);
		} else {
			System.out.println("The consequent is not an implication");
			return;
		}

		Formula[] subproofAntecedents = new Formula[this.proof.antecedents.length + 1];
		for (int i = 0; i < this.proof.antecedents.length; i++) {
			subproofAntecedents[i] = this.proof.antecedents[i];
		}
		subproofAntecedents[this.proof.antecedents.length] = implication.getOperand1();

		Formula[] subproofConsequents = new Formula[1];
		subproofConsequents[0] = implication.getOperand2();

		this.proof = new Proof(subproofAntecedents, subproofConsequents, this.proof, consequentId);
	}

	public void proveByContradiction(int consequentId) {
		Formula consequent = this.proof.consequents[consequentId];

		if (!(consequent instanceof Negation)) {
			System.out.println("The consequent is not a negation");
			return;
		}

		Negation negation = (Negation) consequent;

		Formula[] subproofAntecedents = new Formula[this.proof.antecedents.length + 1];
		for (int i = 0; i < this.proof.antecedents.length; i++) {
			subproofAntecedents[i] = this.proof.antecedents[i];
		}
		subproofAntecedents[this.proof.antecedents.length] = negation.getOperand();

		Formula[] subproofConsequents = new Formula[2];
		subproofConsequents[0] = createUniqueVariable();
		subproofConsequents[1] = new Negation(subproofConsequents[0]);

		this.proof = new Proof(subproofAntecedents, subproofConsequents, this.proof, consequentId);
	}

	public void proveByRule(int consequentId, SuggestionReverse suggestion) {
		Map<String, Formula> map = new HashMap<>();
		for (String name : suggestion.getLeftover()) {
			map.put(name, createUniqueVariable());
		}
		Formula[] suggestionFormulas = suggestion.getFormulas();
		Formula[] proofAntecedents = new Formula[suggestionFormulas.length];
		for (int i = 0; i < proofAntecedents.length; i++) {
			proofAntecedents[i] = suggestionFormulas[i].apply(map, null);
		}

		this.proof = new Proof(this.proof.antecedents, proofAntecedents, this.proof, consequentId);
	}

	private void completeConsequent(int consequentId, Map<String, Formula> map) {
		this.proof.completedConsequents[consequentId] = true;
		updateProofInfo(this.proof, map);

		boolean done = true;
		for (boolean completedConsequent : this.proof.completedConsequents) {
			if (!completedConsequent) {
				done = false;
				break;
			}
		}

		if (done) {
			Proof subproof = this.proof;
			this.proof = subproof.parent;
			if (this.proof != null) {
				completeConsequent(subproof.parentConsequentId, subproof.map);
			}
		}
	}

	private static void updateProofInfo(Proof proofInfo, Map<String, Formula> map) {
		proofInfo.map.putAll(map);

		for (int i = 0; i < proofInfo.antecedents.length; i++) {
			proofInfo.antecedents[i] = proofInfo.antecedents[i].apply(map, null);
		}

		for (int i = 0; i < proofInfo.consequents.length; i++) {
			proofInfo.consequents[i] = proofInfo.consequents[i].apply(map, null);
		}
	}

	private static FreeVariable createUniqueVariable() {
		// TODO: do something smarter: don't use a static and re-use names which are not used anymore
		if (UniqueVariableCounter >= 26) {
			throw new IllegalStateException();
		}
		char name = (char) ('A' + UniqueVariableCounter);
		UniqueVariableCounter++;
		return new FreeVariable(Character.toString(name));
	}
}
