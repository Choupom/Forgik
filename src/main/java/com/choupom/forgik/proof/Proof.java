/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FreeVariable;
import com.choupom.forgik.formula.Implication;
import com.choupom.forgik.formula.Negation;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.proof.ProofIO.Decision;
import com.choupom.forgik.rule.Rulebook;
import com.choupom.forgik.suggester.FormulaSuggesterReverse;
import com.choupom.forgik.suggester.SuggestionReverse;

public class Proof {

	private static class ProofState {

		public List<Formula> entries;
		public Formula goal;
		public Map<String, Formula> map;
	}

	private static int UniqueVariableCounter = 0;

	private ProofState state;

	public Proof(Formula[] antecedents, Formula consequent) {
		this.state = new ProofState();

		this.state.entries = new ArrayList<>();
		for (Formula antecedent : antecedents) {
			this.state.entries.add(antecedent);
		}

		this.state.goal = consequent;
		this.state.map = new HashMap<>();
	}

	public Identification prove(ProofIO io, Rulebook rulebook) {
		while (true) {
			Formula[] ioEntries = this.state.entries.toArray(new Formula[this.state.entries.size()]);
			Decision decision = io.requestDecision(ioEntries, this.state.goal);

			if (decision == Decision.CANCEL_PROOF) {
				return null;
			} else if (decision == Decision.COMPLETE_PROOF) {
				Identification identification = completeProof(io);
				if (identification != null) {
					return identification;
				}
			} else if (decision == Decision.ASSUME) {
				assume(io, rulebook);
			} else if (decision == Decision.ASSUME_NEGATION) {
				assumeNegation(io, rulebook);
			} else if (decision == Decision.SUGGEST_RULE) {
				suggestRule(io, rulebook);
			}
		}
	}

	private Identification completeProof(ProofIO io) {
		boolean proved = false;
		for (Formula entry : this.state.entries) {
			if (entry.checkEquals(this.state.goal)) {
				proved = true;
				break;
			}
		}
		if (proved) {
			return new Identification(this.state.goal, this.state.map);
		}

		List<Identification> identifications = new ArrayList<>();
		for (Formula entry : this.state.entries) {
			Identification identification = FormulaIdentifier.identify(entry, this.state.goal);
			if (identification != null) {
				identifications.add(identification);
			}
		}
		if (identifications.isEmpty()) {
			System.out.println("The goal has not been proven");
			return null;
		}

		Formula[] ioIdentifications = new Formula[identifications.size()];
		for (int i = 0; i < ioIdentifications.length; i++) {
			ioIdentifications[i] = identifications.get(i).getFormula();
		}

		int identificationId = io.requestIdentification(ioIdentifications);
		if (identificationId == -1) {
			return null;
		}

		Identification indentification = identifications.get(identificationId);
		this.state.map.putAll(indentification.getMap());
		return new Identification(indentification.getFormula(), this.state.map);
	}

	private void assume(ProofIO io, Rulebook rulebook) {
		Implication implication;
		if (this.state.goal instanceof Implication) {
			implication = (Implication) this.state.goal;
		} else if (this.state.goal instanceof FreeVariable) {
			Formula freeVariable1 = createUniqueVariable();
			Formula freeVariable2 = createUniqueVariable();
			implication = new Implication(freeVariable1, freeVariable2);
		} else {
			System.out.println("The goal is not an implication");
			return;
		}

		Formula[] subproofAntecedents = new Formula[this.state.entries.size() + 1];
		for (int i = 0; i < this.state.entries.size(); i++) {
			subproofAntecedents[i] = this.state.entries.get(i);
		}
		subproofAntecedents[this.state.entries.size()] = implication.getOperand1();
		Formula subproofConsequent = implication.getOperand2();

		Proof subproof = new Proof(subproofAntecedents, subproofConsequent);
		Identification subproofResult = subproof.prove(io, rulebook);
		if (subproofResult == null) {
			return;
		}

		this.state.entries.add(implication);
		this.state = updateState(this.state, subproofResult.getMap());
	}

	private void assumeNegation(ProofIO io, Rulebook rulebook) {
		if (!(this.state.goal instanceof Negation)) {
			System.out.println("The goal is not a negation");
			return;
		}

		Negation negation = (Negation) this.state.goal;
		Formula extraEntry = negation.getOperand();

		Formula[] goals = new Formula[2];
		goals[0] = createUniqueVariable();
		goals[1] = new Negation(goals[0]);

		ProofState newState = proveSubproofs(this.state, extraEntry, goals, io, rulebook);
		if (newState == null) {
			return;
		}

		this.state = newState;
		this.state.entries.add(this.state.goal);
	}

	private void suggestRule(ProofIO io, Rulebook rulebook) {
		SuggestionReverse[] suggestions = FormulaSuggesterReverse.suggestFromRulebook(this.state.goal, rulebook);
		if (suggestions.length == 0) {
			System.out.println("No suggestion");
			return;
		}

		Formula[][] ioSuggestions = new Formula[suggestions.length][];
		for (int i = 0; i < ioSuggestions.length; i++) {
			ioSuggestions[i] = suggestions[i].getFormulas();
		}

		int suggestionId = io.requestSuggestion(ioSuggestions);
		if (suggestionId == -1) {
			return;
		}

		SuggestionReverse suggestion = suggestions[suggestionId];

		Map<String, Formula> map = new HashMap<>();
		for (String name : suggestion.getLeftover()) {
			map.put(name, createUniqueVariable());
		}
		Formula[] rawSuggestionGoals = suggestion.getFormulas();
		Formula[] suggestionGoals = new Formula[rawSuggestionGoals.length];
		for (int i = 0; i < suggestionGoals.length; i++) {
			suggestionGoals[i] = rawSuggestionGoals[i].apply(map, null);
		}

		ProofState newState = proveSubproofs(this.state, null, suggestionGoals, io, rulebook);
		if (newState == null) {
			return;
		}

		this.state = newState;
		this.state.entries.add(this.state.goal);
	}

	private ProofState proveSubproofs(ProofState state, Formula extraEntry, Formula[] goals, ProofIO io,
			Rulebook rulebook) {
		while (true) {
			int subproofId = io.requestSubproof(goals);
			if (subproofId == -1) {
				return null;
			}

			Formula[] subproofAntecedents = concatFormulas(state.entries, extraEntry);
			Formula subproofConsequent = goals[subproofId];

			Proof subproof = new Proof(subproofAntecedents, subproofConsequent);
			Identification subproofResult = subproof.prove(io, rulebook);
			if (subproofResult == null) {
				continue;
			}

			Map<String, Formula> subproofMap = subproofResult.getMap();
			ProofState newState = updateState(state, subproofMap);

			if (goals.length < 2) {
				return newState;
			}

			Formula[] newGoals = new Formula[goals.length - 1];
			int j = 0;
			for (int i = 0; i < goals.length; i++) {
				if (i != subproofId) {
					newGoals[j++] = goals[i].apply(subproofMap, null);
				}
			}
			Formula newExtraEntry = null;
			if (extraEntry != null) {
				newExtraEntry = extraEntry.apply(subproofMap, null);
			}
			return proveSubproofs(newState, newExtraEntry, newGoals, io, rulebook);
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

	private static ProofState updateState(ProofState state, Map<String, Formula> map) {
		ProofState newState = new ProofState();

		newState.entries = new ArrayList<>();
		for (Formula entry : state.entries) {
			newState.entries.add(entry.apply(map, null));
		}

		newState.goal = state.goal.apply(map, null);

		newState.map = new HashMap<>();
		newState.map.putAll(state.map);
		newState.map.putAll(map);

		return newState;
	}

	private static Formula[] concatFormulas(List<Formula> formulas, Formula extraFormula) {
		int numFormulas = formulas.size();
		if (extraFormula != null) {
			numFormulas++;
		}

		Formula[] concat = new Formula[numFormulas];
		for (int i = 0; i < formulas.size(); i++) {
			concat[i] = formulas.get(i);
		}

		if (extraFormula != null) {
			concat[formulas.size()] = extraFormula;
		}

		return concat;
	}
}
