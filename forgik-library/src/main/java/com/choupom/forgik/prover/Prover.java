/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.prover;

import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.rule.Rule;

public class Prover {

	private final FreeFormulaFactory freeFormulaFactory;
	private final Proof mainProof;

	private Proof proof;

	public Prover(Formulas antecedents, Formulas consequents) {
		this.freeFormulaFactory = new FreeFormulaFactory();
		this.mainProof = new Proof(new int[0], antecedents, consequents, null, -1, null, antecedents.size(),
				this.freeFormulaFactory);

		this.proof = this.mainProof;
	}

	public boolean isOnMainProof() {
		return (this.proof == this.mainProof);
	}

	public boolean isMainProofComplete() {
		return this.mainProof.isComplete();
	}

	public boolean isProofComplete() {
		return this.proof.isComplete();
	}

	public ProofInfo getProofInfo() {
		return this.proof.getInfo();
	}

	public void cancelProof() throws ProverException {
		Proof parentProof = this.proof.getParent();
		if (parentProof == null) {
			throw new ProverException("Main proof may not be canceled");
		}

		this.proof = parentProof;
	}

	public void completeConsequent(int consequentId, int antecedentId) throws ProverException {
		this.proof.completeConsequentByIdentification(consequentId, antecedentId);
	}

	public void proveByRule(int consequentId, Rule rule) throws ProverException {
		this.proof = this.proof.createProofForConsequent(consequentId, rule);
	}

	public void completeProof() throws ProverException {
		Proof parentProof = this.proof.getParent();
		if (parentProof == null) {
			throw new ProverException("Main proof may not be completed");
		}

		int parentConsequentId = this.proof.getParentConsequentId();
		parentProof.completeConsequentBySubproof(parentConsequentId, this.proof);
		this.proof = parentProof;
	}
}
