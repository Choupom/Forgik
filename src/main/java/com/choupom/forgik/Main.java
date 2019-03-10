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
import com.choupom.forgik.parser.FormulaParser;
import com.choupom.forgik.rule.DeductionRulebook;
import com.choupom.forgik.rule.Rulebook;
import com.choupom.forgik.suggester.FormulaSuggester;
import com.choupom.forgik.suggester.FormulaSuggesterReverse;
import com.choupom.forgik.suggester.Suggestion;
import com.choupom.forgik.suggester.SuggestionReverse;

public class Main {

	private static class Entry {
		public Formula f;
		public Formula goal;
		public Formula proof;

		public Entry(Formula f) {
			this(f, null, null);
		}

		public Entry(Formula f, Formula goal, Formula proof) {
			this.f = f;
			this.goal = goal;
			this.proof = proof;
		}
	}

	// private static final String MAIN_FORMULA = "((p > q) ^ (q > r)) > (p > r)";
	// private static final String MAIN_FORMULA = "(p v (p ^ p)) > p";
	// private static final String MAIN_FORMULA = "p > ((p v p) ^ p)";
	// private static final String MAIN_FORMULA = "((p v q) ^ -p) > q";
	// private static final String MAIN_FORMULA = "p > --p";
	// private static final String MAIN_FORMULA = "((p > q) ^ -q) > -p";
	// private static final String MAIN_FORMULA = "-(p > q) > (p ^ -q)";
	private static final String MAIN_FORMULA = "-(p > q) > --p";
	// private static final String MAIN_FORMULA = "(p > q) > -(p ^ -q)";

	public static void main(String[] args) {
		Rulebook rulebook = DeductionRulebook.getInstance();
		Formula formula = FormulaParser.parse(MAIN_FORMULA);
		run((Implication) formula, rulebook);
	}

	private static void run(Implication proof, Rulebook rulebook) {
		List<Entry> entries = new ArrayList<>();
		entries.add(new Entry(proof.getOperand1(), proof.getOperand2(), proof));

		try (Scanner input = new Scanner(System.in)) {
			while (true) {
				int lastAssumptionIndex = getLastAssumption(entries);
				if (lastAssumptionIndex == -1) {
					break;
				}

				Entry lastAssumption = entries.get(lastAssumptionIndex);
				printEntries(entries, lastAssumption.goal);

				String line = input.nextLine();
				if (line.length() == 0) {
					continue;
				}

				if (line.equals("<")) {
					boolean proved = false;
					for (Entry entry : entries) {
						if (entry.f.checkEquals(lastAssumption.goal)) {
							proved = true;
							break;
						}
					}
					if (!proved) {
						System.out.println("The goal has not been proven");
						return;
					}
					while (entries.size() > lastAssumptionIndex) {
						entries.remove(entries.size() - 1);
					}
					if (lastAssumption.proof != null) {
						entries.add(new Entry(lastAssumption.proof));
					}
				} else if (line.equals(">")) {
					if (!(lastAssumption.goal instanceof Implication)) {
						System.out.println("The goal is not an implication");
						return;
					}
					Implication implication = (Implication) lastAssumption.goal;
					entries.add(new Entry(implication.getOperand1(), implication.getOperand2(), implication));
				} else if (line.equals("-")) {
					if (!(lastAssumption.goal instanceof Negation)) {
						System.out.println("The goal is not a negation");
						return;
					}
					Negation negation = (Negation) lastAssumption.goal;
					// entries.add(new Entry(negation.getOperand(), new Variable("F"), negation));
					entries.add(new Entry(negation.getOperand(), new FreeVariable("A"), new FreeVariable("A")));
					entries.add(new Entry(negation.getOperand(), new FreeVariable("-A"), new FreeVariable("-A")));
				} else if (line.equals("G")) {
					SuggestionReverse[] suggestions = FormulaSuggesterReverse.suggestFromRulebook(lastAssumption.goal,
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
						for (int i = suggestion.formulas.length - 1; i >= 0; i--) {
							entries.add(new Entry(lastAssumption.f, suggestion.formulas[i], suggestion.formulas[i]));
						}
					}
				} else {
					String[] formulaIds = line.split(" ");

					Formula[] formulas = new Formula[formulaIds.length];
					for (int i = 0; i < formulaIds.length; i++) {
						int entryId = Integer.parseInt(formulaIds[i]);
						formulas[i] = entries.get(entryId).f;
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
						entries.add(new Entry(suggestion.getFormula()));
					}
				}
			}
		}
	}

	private static int getLastAssumption(List<Entry> entries) {
		for (int i = entries.size() - 1; i >= 0; i--) {
			Entry entry = entries.get(i);
			if (entry.goal != null) {
				return i;
			}
		}
		return -1;
	}

	private static void printEntries(List<Entry> entries, Formula goal) {
		int i = 0;
		for (Entry entry : entries) {
			System.out.println("[" + (i++) + "] " + entry.f);
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
