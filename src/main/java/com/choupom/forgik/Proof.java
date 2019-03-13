/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FreeVariable;
import com.choupom.forgik.formula.Implication;
import com.choupom.forgik.formula.Negation;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
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

	public Identification prove(Scanner input, Rulebook rulebook) {
		while (true) {
			printState(this.state);

			String line = input.nextLine();
			if (line.length() == 0) {
				continue;
			}

			if (line.equals("<")) {
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

				List<Identification> suggestions = new ArrayList<>();
				for (Formula entry : this.state.entries) {
					Identification identification = FormulaIdentifier.identify(entry, this.state.goal);
					if (identification != null) {
						suggestions.add(identification);
					}
				}
				if (suggestions.size() == 0) {
					System.out.println("The goal has not been proven");
					continue;
				}

				printIdentifications(suggestions);

				String response = input.nextLine();
				if (response.length() > 0) {
					int suggestionId = Integer.parseInt(response);
					Identification indentification = suggestions.get(suggestionId);
					this.state.map.putAll(indentification.getMap());
					return new Identification(indentification.getFormula(), this.state.map);
				}
			} else if (line.equals(">")) {
				Implication implication;
				if (this.state.goal instanceof Implication) {
					implication = (Implication) this.state.goal;
				} else if (this.state.goal instanceof FreeVariable) {
					Formula freeVariable1 = createUniqueVariable();
					Formula freeVariable2 = createUniqueVariable();
					implication = new Implication(freeVariable1, freeVariable2);
				} else {
					System.out.println("The goal is not an implication");
					continue;
				}

				Formula[] subproofAntecedents = new Formula[this.state.entries.size() + 1];
				for (int i = 0; i < this.state.entries.size(); i++) {
					subproofAntecedents[i] = this.state.entries.get(i);
				}
				subproofAntecedents[this.state.entries.size()] = implication.getOperand1();
				Formula subproofConsequent = implication.getOperand2();
				Proof subproof = new Proof(subproofAntecedents, subproofConsequent);
				Identification subproofResult = subproof.prove(input, rulebook);
				if (subproofResult == null) {
					continue;
				}

				this.state.entries.add(implication);
				this.state = updateState(this.state, subproofResult.getMap());
			} else if (line.equals("-")) {
				if (!(this.state.goal instanceof Negation)) {
					System.out.println("The goal is not a negation");
					continue;
				}

				Formula[] subproofAntecedents = new Formula[this.state.entries.size() + 1];
				for (int i = 0; i < this.state.entries.size(); i++) {
					subproofAntecedents[i] = this.state.entries.get(i);
				}
				Negation negation = (Negation) this.state.goal;
				subproofAntecedents[this.state.entries.size()] = negation.getOperand();
				Formula subproofConsequent = createUniqueVariable();
				Proof subproof = new Proof(subproofAntecedents, subproofConsequent);
				Identification subproofResult = subproof.prove(input, rulebook);
				if (subproofResult == null) {
					continue;
				}

				ProofState newState = updateState(this.state, subproofResult.getMap());

				subproofAntecedents = new Formula[newState.entries.size() + 1];
				for (int i = 0; i < newState.entries.size(); i++) {
					subproofAntecedents[i] = newState.entries.get(i);
				}
				negation = (Negation) newState.goal;
				subproofAntecedents[newState.entries.size()] = negation.getOperand();
				subproofConsequent = new Negation(subproofResult.getFormula());
				subproof = new Proof(subproofAntecedents, subproofConsequent);
				subproofResult = subproof.prove(input, rulebook);
				if (subproofResult == null) {
					continue;
				}

				this.state = updateState(newState, subproofResult.getMap());
				this.state.entries.add(this.state.goal);
			} else if (line.equals("G")) {
				SuggestionReverse[] suggestions = FormulaSuggesterReverse.suggestFromRulebook(this.state.goal,
						rulebook);
				if (suggestions.length == 0) {
					System.out.println("No suggestion");
					continue;
				}

				printSuggestionsReverse(suggestions);

				String response = input.nextLine();
				if (response.length() > 0) {
					int suggestionId = Integer.parseInt(response);
					SuggestionReverse suggestion = suggestions[suggestionId];

					Map<String, Formula> map = new HashMap<>();
					for (String name : suggestion.getLeftover()) {
						map.put(name, createUniqueVariable());
					}

					ProofState newState = this.state;

					boolean proved = true;
					for (Formula formula : suggestion.getFormulas()) {
						Formula[] subproofAntecedents = new Formula[newState.entries.size()];
						for (int i = 0; i < newState.entries.size(); i++) {
							subproofAntecedents[i] = newState.entries.get(i);
						}
						Formula subproofConsequent = formula.apply(map, null).apply(newState.map, null);

						Proof subproof = new Proof(subproofAntecedents, subproofConsequent);
						Identification subproofResult = subproof.prove(input, rulebook);
						if (subproofResult == null) {
							proved = false;
							break;
						}

						newState = updateState(newState, subproofResult.getMap());
					}
					if (!proved) {
						continue;
					}

					this.state = newState;
					this.state.entries.add(this.state.goal);
				}
			} else {
				System.out.println("Invalid input");
			}
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

	private static void printState(ProofState state) {
		int i = 0;
		for (Formula entry : state.entries) {
			System.out.println("[" + (i++) + "] " + entry);
		}
		System.out.println("...");
		System.out.println("[GOAL] " + state.goal + "?");
	}

	private static void printIdentifications(List<Identification> identifications) {
		int i = 0;
		for (Identification identification : identifications) {
			System.out.println("{" + (i++) + "} " + identification.getFormula());
		}
	}

	private static void printSuggestionsReverse(SuggestionReverse[] suggestions) {
		for (int i = 0; i < suggestions.length; i++) {
			System.out.print("{" + i + "}");
			for (Formula formula : suggestions[i].getFormulas()) {
				System.out.print(" [" + formula + "]");
			}
			System.out.println();
		}
	}
}
