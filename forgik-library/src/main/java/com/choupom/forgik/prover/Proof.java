/*
 * Java
 *
 * Copyright 2019-2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.operations.ApplyOperation;
import com.choupom.forgik.proof.tree.ProofReport;
import com.choupom.forgik.proof.tree.ProofReportIdentification;
import com.choupom.forgik.proof.tree.ProofReportRule;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rule.RuleApplicationResult;
import com.choupom.forgik.rule.RuleApplier;

/* package */ class Proof {

	private final int[] path;
	private final Proof parent;
	private final int parentConsequentId;
	private final Rule parentConsequentRule;
	private final int numAssumptions;
	private final FreeFormulaFactory freeFormulaFactory;

	private Formulas antecedents;
	private Formulas consequents;
	private final boolean[] completedConsequents;
	private final ProofReport[] consequentReports;
	private final Map<Integer, Formula> map;

	public Proof(int[] path, Formulas antecedents, Formulas consequents, Proof parent, int parentConsequentId,
			Rule parentConsequentRule, int numAssumptions, FreeFormulaFactory freeFormulaFactory) {
		this.path = path.clone();
		this.parent = parent;
		this.parentConsequentId = parentConsequentId;
		this.parentConsequentRule = parentConsequentRule;
		this.numAssumptions = numAssumptions;
		this.freeFormulaFactory = freeFormulaFactory;

		this.antecedents = antecedents;
		this.consequents = consequents;
		this.completedConsequents = new boolean[consequents.size()];
		this.consequentReports = new ProofReport[consequents.size()];
		this.map = new HashMap<>();
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

	public ProofInfo getInfo() {
		return new ProofInfo(this.path, this.antecedents, this.consequents, this.completedConsequents,
				this.consequentReports, this.parent, this.parentConsequentId, this.parentConsequentRule);
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

		Formula consequent = this.consequents.get(consequentId);
		Formula antecedent = this.antecedents.get(antecedentId);

		Identification identification = FormulaIdentifier.identify(antecedent, consequent);
		if (identification == null) {
			throw new ProverException("Antecedent can not be identified with consequent");
		}

		this.completedConsequents[consequentId] = true;
		this.consequentReports[consequentId] = new ProofReportIdentification(antecedent, antecedentId);
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
		this.consequentReports[consequentId] = subproof.createReport();
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

		Formula consequent = this.consequents.get(consequentId);

		RuleApplicationResult result = RuleApplier.apply(rule, consequent);
		if (result == null) {
			throw new ProverException("Rule can not be applied to consequent");
		}

		Map<Integer, Formula> leftoverMap = new HashMap<>();
		for (Integer ruleFormula : result.getLeftover()) {
			leftoverMap.put(ruleFormula, this.freeFormulaFactory.createFreeFormula());
		}
		ApplyOperation applyOperation = new ApplyOperation(leftoverMap);

		Formulas ruleAssumptions = result.getAssumptions();
		Formulas proofAntecedents = Formulas.concat(this.antecedents, ruleAssumptions.runOperation(applyOperation));

		Formulas ruleAntecedents = result.getAntecedents();
		Formulas proofConsequents = ruleAntecedents.runOperation(applyOperation);

		Map<Integer, Formula> consequentMap = new HashMap<>();
		for (Map.Entry<Integer, Formula> entry : result.getConsequentMap().entrySet()) {
			consequentMap.put(entry.getKey(), entry.getValue().runOperation(applyOperation));
		}
		applyMap(consequentMap); // TODO: is this ever reversed?

		int[] subpath = Arrays.copyOf(this.path, this.path.length + 1);
		subpath[this.path.length] = consequentId;
		return new Proof(subpath, proofAntecedents, proofConsequents, this, consequentId, rule, ruleAssumptions.size(),
				this.freeFormulaFactory);
	}

	private void applyMap(Map<Integer, Formula> map) {
		ApplyOperation applyOperation = new ApplyOperation(map);
		for (Map.Entry<Integer, Formula> entry : this.map.entrySet()) {
			entry.setValue(entry.getValue().runOperation(applyOperation));
		}
		this.map.putAll(map);

		this.antecedents = this.antecedents.runOperation(applyOperation);
		this.consequents = this.consequents.runOperation(applyOperation);

		for (int i = 0; i < this.consequentReports.length; i++) {
			ProofReport report = this.consequentReports[i];
			if (report != null) {
				this.consequentReports[i] = report.apply(map);
			}
		}
	}

	private ProofReport createReport() {
		Formulas assumptions = this.antecedents.getCopyOfRange(this.antecedents.size() - this.numAssumptions,
				this.antecedents.size());
		Formula parentConsequent = this.parent.consequents.get(this.parentConsequentId);
		return new ProofReportRule(this.parentConsequentRule, assumptions, this.consequentReports, parentConsequent);
	}
}
