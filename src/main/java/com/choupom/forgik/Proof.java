/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FreeVariable;
import com.choupom.forgik.formula.Implication;
import com.choupom.forgik.formula.Negation;
import com.choupom.forgik.rule.Rulebook;
import com.choupom.forgik.suggester.FormulaSuggester;
import com.choupom.forgik.suggester.FormulaSuggesterReverse;
import com.choupom.forgik.suggester.Suggestion;
import com.choupom.forgik.suggester.SuggestionReverse;

public class Proof {

	private final Formula[] antecedents; // "premises"
	private final Formula consequent; // "conclusion"

	public Proof(Formula[] antecedents, Formula consequent) {
		this.antecedents = antecedents.clone();
		this.consequent = consequent;
	}

	public boolean prove(Scanner input, Rulebook rulebook) {
		List<Formula> entries = new ArrayList<>();
		for (Formula antecedent : this.antecedents) {
			entries.add(antecedent);
		}

		while (true) {
			printEntries(entries, this.consequent);

			String line = input.nextLine();
			if (line.length() == 0) {
				continue;
			}

			if (line.equals("<")) {
				boolean proved = false;
				for (Formula entry : entries) {
					if (entry.checkEquals(this.consequent)) {
						proved = true;
						break;
					}
				}
				if (!proved) {
					System.out.println("The goal has not been proven");
					continue;
				}
				break;
			} else if (line.equals(">")) {
				if (!(this.consequent instanceof Implication)) {
					System.out.println("The goal is not an implication");
					continue;
				}
				Implication implication = (Implication) this.consequent;
				Formula[] subproofAntecedents = new Formula[entries.size() + 1];
				for (int i = 0; i < entries.size(); i++) {
					subproofAntecedents[i] = entries.get(i);
				}
				subproofAntecedents[entries.size()] = implication.getOperand1();
				Formula subproofConsequent = implication.getOperand2();
				Proof subproof = new Proof(subproofAntecedents, subproofConsequent);
				if (!subproof.prove(input, rulebook)) {
					continue;
				}
				entries.add(implication);
			} else if (line.equals("-")) {
				if (!(this.consequent instanceof Negation)) {
					System.out.println("The goal is not a negation");
					continue;
				}
				Negation negation = (Negation) this.consequent;
				Formula[] subproofAntecedents = new Formula[entries.size() + 1];
				for (int i = 0; i < entries.size(); i++) {
					subproofAntecedents[i] = entries.get(i);
				}
				subproofAntecedents[entries.size()] = negation.getOperand();
				Formula subproof1Consequent = new FreeVariable("A");
				Formula subproof2Consequent = new Negation(new FreeVariable("A"));
				Proof subproof1 = new Proof(subproofAntecedents, subproof1Consequent);
				if (!subproof1.prove(input, rulebook)) {
					continue;
				}
				Proof subproof2 = new Proof(subproofAntecedents, subproof2Consequent);
				if (!subproof2.prove(input, rulebook)) {
					continue;
				}
				entries.add(negation);
			} else if (line.equals("G")) {
				SuggestionReverse[] suggestions = FormulaSuggesterReverse.suggestFromRulebook(this.consequent,
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
					boolean proved = true;
					for (Formula formula : suggestion.formulas) {
						Formula[] subproofAntecedents = new Formula[entries.size()];
						for (int i = 0; i < entries.size(); i++) {
							subproofAntecedents[i] = entries.get(i);
						}
						Formula subproofConsequent = formula;
						Proof subproof = new Proof(subproofAntecedents, subproofConsequent);
						if (!subproof.prove(input, rulebook)) {
							proved = false;
							break;
						}
					}
					if (!proved) {
						continue;
					}
					entries.add(this.consequent);
				}
			} else {
				String[] formulaIds = line.split(" ");

				Formula[] formulas = new Formula[formulaIds.length];
				for (int i = 0; i < formulaIds.length; i++) {
					int entryId = Integer.parseInt(formulaIds[i]);
					formulas[i] = entries.get(entryId);
				}

				Suggestion[] suggestions = FormulaSuggester.suggestFromRulebook(formulas, rulebook);
				if (suggestions.length == 0) {
					System.out.println("No suggestion");
					continue;
				}

				printSuggestions(suggestions);

				String response = input.nextLine();
				if (response.length() > 0) {
					int suggestionId = Integer.parseInt(response);
					Suggestion suggestion = suggestions[suggestionId];
					entries.add(suggestion.getFormula());
				}
			}
		}

		return true;
	}

	private static void printEntries(List<Formula> entries, Formula goal) {
		int i = 0;
		for (Formula entry : entries) {
			System.out.println("[" + (i++) + "] " + entry);
		}
		System.out.println("...");
		System.out.println("[GOAL] " + goal + "?");
	}

	private static void printSuggestions(Suggestion[] suggestions) {
		for (int i = 0; i < suggestions.length; i++) {
			System.out.println("{" + i + "} " + suggestions[i].getFormula());
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
