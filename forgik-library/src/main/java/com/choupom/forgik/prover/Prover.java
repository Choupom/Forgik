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

		public boolean isComplete() {
			for (boolean completedConsequent : this.completedConsequents) {
				if (!completedConsequent) {
					return false;
				}
			}
			return true;
		}
	}

	private int uniqueVariableCounter;
	private Proof proof;

	public Prover(Formula[] antecedents, Formula[] consequents) {
		this.uniqueVariableCounter = 0;
		this.proof = new Proof(antecedents, consequents, null, -1);
	}

	public boolean isMainProofComplete() {
		return (isOnMainProof() && this.proof.isComplete());
	}

	public boolean isOnMainProof() {
		return (this.proof.parent == null);
	}

	public ProofInfo getProofInfo() {
		return new ProofInfo(this.proof.antecedents, this.proof.consequents, this.proof.completedConsequents);
	}

	public void cancelProof() throws ProverException {
		if (this.proof.parent == null) {
			throw new ProverException("Main proof can not be canceled");
		}

		this.proof = this.proof.parent;
	}

	public void completeConsequent(int consequentId, int antecedentId) throws ProverException {
		if (this.proof.completedConsequents[consequentId]) {
			throw new ProverException("Consequent is already completed");
		}

		Formula consequent = this.proof.consequents[consequentId];
		Formula antecedent = this.proof.antecedents[antecedentId];

		Identification identification = FormulaIdentifier.identify(antecedent, consequent);
		if (identification == null) {
			throw new ProverException("Antecedent can not be identified with consequent");
		}

		completeConsequent(consequentId, identification.getMap());
	}

	public void proveByRule(int consequentId, Rule rule) throws ProverException {
		if (this.proof.completedConsequents[consequentId]) {
			throw new ProverException("Consequent is already completed");
		}

		Formula consequent = this.proof.consequents[consequentId];

		RuleApplicationResult result = rule.apply(consequent);
		if (result == null) {
			throw new ProverException("Rule can not be applied to consequent");
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
		updateProof(this.proof, consequentMap);

		this.proof = new Proof(proofAntecedents, proofConsequents, this.proof, consequentId);
	}

	private void completeConsequent(int consequentId, Map<String, Formula> map) {
		this.proof.completedConsequents[consequentId] = true;
		updateProof(this.proof, map);

		if (this.proof.isComplete() && !isOnMainProof()) {
			Proof subproof = this.proof;
			this.proof = subproof.parent;
			completeConsequent(subproof.parentConsequentId, subproof.map);
		}
	}

	private static void updateProof(Proof proof, Map<String, Formula> map) {
		proof.map.putAll(map);

		for (int i = 0; i < proof.antecedents.length; i++) {
			proof.antecedents[i] = proof.antecedents[i].apply(map);
		}

		for (int i = 0; i < proof.consequents.length; i++) {
			proof.consequents[i] = proof.consequents[i].apply(map);
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
