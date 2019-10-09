/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import java.util.HashMap;
import java.util.Map;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FreeVariable;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rule.RuleApplicationResult;

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

	private int uniqueVariableCounter;

	private Proof proof;

	public Prover(Formula[] antecedents, Formula[] consequents) {
		this.uniqueVariableCounter = 0;
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

	public void completeConsequent(int consequentId, int antecedentId) {
		Formula consequent = this.proof.consequents[consequentId];
		Formula antecedent = this.proof.antecedents[antecedentId];

		Identification identification = FormulaIdentifier.identify(antecedent, consequent);
		if (identification != null) {
			completeConsequent(consequentId, identification.getMap());
		}
	}

	public void proveByRule(int consequentId, Rule rule) {
		Formula consequent = this.proof.consequents[consequentId];

		RuleApplicationResult result = rule.apply(consequent);
		if (result == null) {
			System.out.println("Given rule can not be applied to the consequent");
			return;
		}

		Map<String, Formula> leftoverMap = new HashMap<>();
		for (String ruleVariable : result.getLeftover()) {
			leftoverMap.put(ruleVariable, createUniqueVariable());
		}

		Formula[] ruleAssumptions = result.getAssumptions();
		Formula[] proofAntecedents = new Formula[this.proof.antecedents.length + ruleAssumptions.length];
		System.arraycopy(this.proof.antecedents, 0, proofAntecedents, 0, this.proof.antecedents.length);
		for (int i = 0; i < ruleAssumptions.length; i++) {
			proofAntecedents[this.proof.antecedents.length + i] = ruleAssumptions[i].apply(leftoverMap);
		}

		Formula[] ruleAntecedents = result.getAntecedents();
		Formula[] proofConsequents = new Formula[ruleAntecedents.length];
		for (int i = 0; i < ruleAntecedents.length; i++) {
			proofConsequents[i] = ruleAntecedents[i].apply(leftoverMap);
		}

		Map<String, Formula> consequentMap = new HashMap<>();
		for (Map.Entry<String, Formula> entry : result.getConsequentMap().entrySet()) {
			consequentMap.put(entry.getKey(), entry.getValue().apply(leftoverMap));
		}
		updateProofInfo(this.proof, consequentMap);

		this.proof = new Proof(proofAntecedents, proofConsequents, this.proof, consequentId);
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
			proofInfo.antecedents[i] = proofInfo.antecedents[i].apply(map);
		}

		for (int i = 0; i < proofInfo.consequents.length; i++) {
			proofInfo.consequents[i] = proofInfo.consequents[i].apply(map);
		}
	}

	private FreeVariable createUniqueVariable() {
		// TODO: re-use names which are not used anymore
		if (this.uniqueVariableCounter >= 26) {
			throw new IllegalStateException();
		}
		char name = (char) ('A' + this.uniqueVariableCounter);
		this.uniqueVariableCounter++;
		return new FreeVariable(Character.toString(name));
	}
}
