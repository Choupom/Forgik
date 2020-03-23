/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rule.RuleApplicationResult;

/* package */ class Proof {

	private final Formula[] antecedents;
	private final Formula[] consequents;
	private final Proof parent;
	private final int parentConsequentId;
	private final Rule parentConsequentRule;
	private final int numAssumptions;
	private final FreeFormulaFactory freeFormulaFactory;

	private final boolean[] completedConsequents;
	private final Map<Integer, Formula> map;
	private final ProofReport[] reports;

	public Proof(Formula[] antecedents, Formula[] consequents, Proof parent, int parentConsequentId,
			Rule parentConsequentRule, int numAssumptions, FreeFormulaFactory freeFormulaFactory) {
		this.antecedents = antecedents.clone();
		this.consequents = consequents.clone();
		this.parent = parent;
		this.parentConsequentId = parentConsequentId;
		this.parentConsequentRule = parentConsequentRule;
		this.numAssumptions = numAssumptions;
		this.freeFormulaFactory = freeFormulaFactory;

		this.completedConsequents = new boolean[consequents.length];
		this.map = new HashMap<>();
		this.reports = new ProofReport[consequents.length];
	}

	public Proof getParent() {
		return this.parent;
	}

	public int getParentConsequentId() {
		return this.parentConsequentId;
	}

	public boolean isComplete() {
		for (boolean completedConsequent : this.completedConsequents) {
			if (!completedConsequent) {
				return false;
			}
		}
		return true;
	}

	public ProofReport[] getReports() {
		return this.reports.clone();
	}

	public ProofInfo getInfo() {
		return new ProofInfo(this.antecedents, this.consequents, this.completedConsequents);
	}

	/**
	 * Completes a consequent of this proof by identifying it with one of the antecedents of this proof.
	 *
	 * @param consequentId
	 *            the id of the consequent to complete.
	 * @param antecedentId
	 *            the id of the antecedent to identify.
	 * @throws ProverException
	 *             if the consequent has already been completed.
	 */
	public void completeConsequentByIdentification(int consequentId, int antecedentId) throws ProverException {
		if (this.completedConsequents[consequentId]) {
			throw new ProverException("Consequent is already completed");
		}

		Formula consequent = this.consequents[consequentId];
		Formula antecedent = this.antecedents[antecedentId];

		Identification identification = FormulaIdentifier.identify(antecedent, consequent);
		if (identification == null) {
			throw new ProverException("Antecedent can not be identified with consequent");
		}

		this.completedConsequents[consequentId] = true;
		applyMap(identification.getMap());
	}

	/**
	 * Completes a consequent of this proof by using a complete subproof.
	 *
	 * @param consequentId
	 *            the id of the consequent to complete.
	 * @param subproof
	 *            the complete subproof.
	 * @throws ProverException
	 *             if the consequent has already been completed.
	 * @throws ProverException
	 *             if the subproof is not complete.
	 */
	public void completeConsequentBySubproof(int consequentId, Proof subproof) throws ProverException {
		if (this.completedConsequents[consequentId]) {
			throw new ProverException("Consequent is already completed");
		}

		if (!subproof.isComplete()) {
			throw new ProverException("Subproof is not complete");
		}

		this.completedConsequents[consequentId] = true;
		this.reports[consequentId] = subproof.createReport();
		applyMap(subproof.map);
	}

	/**
	 * Creates a subproof which proves a consequent of this proof by using a rule.
	 *
	 * @param consequentId
	 *            the id of the consequent to prove.
	 * @param rule
	 *            the rule to use.
	 * @return the created subproof.
	 * @throws ProverException
	 *             if the consequent has already been completed.
	 */
	public Proof createProofForConsequent(int consequentId, Rule rule) throws ProverException {
		if (this.completedConsequents[consequentId]) {
			throw new ProverException("Consequent is already completed");
		}

		Formula consequent = this.consequents[consequentId];

		RuleApplicationResult result = rule.apply(consequent);
		if (result == null) {
			throw new ProverException("Rule can not be applied to consequent");
		}

		Map<Integer, Formula> leftoverMap = new HashMap<>();
		for (Integer ruleFormula : result.getLeftover()) {
			leftoverMap.put(ruleFormula, this.freeFormulaFactory.createFreeFormula());
		}

		Formula[] ruleAssumptions = result.getAssumptions();
		Formula[] proofAntecedents = new Formula[this.antecedents.length + ruleAssumptions.length];
		System.arraycopy(this.antecedents, 0, proofAntecedents, 0, this.antecedents.length);
		for (int i = 0; i < ruleAssumptions.length; i++) {
			proofAntecedents[this.antecedents.length + i] = ruleAssumptions[i].apply(leftoverMap);
		}

		Formula[] ruleAntecedents = result.getAntecedents();
		Formula[] proofConsequents = new Formula[ruleAntecedents.length];
		for (int i = 0; i < ruleAntecedents.length; i++) {
			proofConsequents[i] = ruleAntecedents[i].apply(leftoverMap);
		}

		Map<Integer, Formula> consequentMap = new HashMap<>();
		for (Map.Entry<Integer, Formula> entry : result.getConsequentMap().entrySet()) {
			consequentMap.put(entry.getKey(), entry.getValue().apply(leftoverMap));
		}
		applyMap(consequentMap); // TODO: is this ever reversed?

		return new Proof(proofAntecedents, proofConsequents, this, consequentId, rule, ruleAssumptions.length,
				this.freeFormulaFactory);
	}

	private void applyMap(Map<Integer, Formula> map) {
		this.map.putAll(map);

		for (int i = 0; i < this.antecedents.length; i++) {
			this.antecedents[i] = this.antecedents[i].apply(map);
		}

		for (int i = 0; i < this.consequents.length; i++) {
			this.consequents[i] = this.consequents[i].apply(map);
		}

		for (int i = 0; i < this.reports.length; i++) {
			ProofReport report = this.reports[i];
			if (report != null) {
				this.reports[i] = report.apply(map);
			}
		}
	}

	private ProofReport createReport() {
		Formula[] assumptions = Arrays.copyOfRange(this.antecedents, this.antecedents.length - this.numAssumptions,
				this.antecedents.length);
		Formula parentConsequent = this.parent.consequents[this.parentConsequentId];
		return new ProofReport(this.parentConsequentRule, assumptions, this.consequents, parentConsequent, this.reports);
	}
}
