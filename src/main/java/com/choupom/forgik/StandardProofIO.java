/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik;

import java.util.Scanner;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.proof.ProofIO;
import com.choupom.forgik.suggester.SuggestionReverse;

public class StandardProofIO implements ProofIO {

	private final Scanner input;

	public StandardProofIO() {
		this.input = new Scanner(System.in);
	}

	@Override
	public Decision requestDecision(Formula[] entries, Formula goal) {
		int i = 0;
		for (Formula entry : entries) {
			System.out.println("[" + (i++) + "] " + entry);
		}
		System.out.println("...");
		System.out.println("[GOAL] " + goal + "?");

		// System.out.println("What do you want to do?");
		// System.out.println("{Q} Cancel this proof");
		// System.out.println("{<} Complete this proof");
		// System.out.println("{>} Make an assumption");
		// System.out.println("{-} Assume the negation");
		// System.out.println("{G} Suggest rules");
		return getDecision();
	}

	@Override
	public int requestIdentification(Identification[] identifications) {
		System.out.println("What did you prove?");

		for (int i = 0; i < identifications.length; i++) {
			Identification identification = identifications[i];
			if (identification != null) {
				System.out.println("{" + i + "} " + identification.getFormula());
			}
		}

		while (true) {
			int index = getIndex(0, identifications.length - 1);
			if (index == -1) {
				return -1;
			} else if (identifications[index] == null) {
				System.out.println("Invalid index");
			}
			return index;
		}
	}

	@Override
	public int requestSuggestion(SuggestionReverse[] suggestions) {
		System.out.println("Which rule do you want to use?");

		for (int i = 0; i < suggestions.length; i++) {
			System.out.print("{" + i + "}");
			for (Formula formula : suggestions[i].getFormulas()) {
				System.out.print(" [" + formula + "]");
			}
			System.out.println();
		}

		return getIndex(0, suggestions.length - 1);
	}

	@Override
	public int requestSubproof(Formula[] entries, Formula[] goals, boolean[] completedGoals) {
		int numUncompletedGoals = 0;
		int firstUncompletedGoal = -1;
		for (int i = 0; i < completedGoals.length; i++) {
			if (!completedGoals[i]) {
				numUncompletedGoals++;
				firstUncompletedGoal = i;
			}
		}
		if (numUncompletedGoals < 2) {
			return firstUncompletedGoal;
		}

		int i = 0;
		for (Formula entry : entries) {
			System.out.println("[" + (i++) + "] " + entry);
		}

		System.out.println("What do you want to prove first?");

		for (i = 0; i < goals.length; i++) {
			if (!completedGoals[i]) {
				System.out.println("{" + i + "} " + goals[i]);
			}
		}

		while (true) {
			int index = getIndex(0, goals.length - 1);
			if (index == -1) {
				return -1;
			} else if (completedGoals[index]) {
				System.out.println("Invalid index");
			}
			return index;
		}
	}

	private Decision getDecision() {
		while (true) {
			String line = this.input.nextLine();
			if (line.equals("<")) {
				return Decision.COMPLETE_PROOF;
			} else if (line.equals(">")) {
				return Decision.ASSUME;
			} else if (line.equals("-")) {
				return Decision.ASSUME_NEGATION;
			} else if (line.equals("G")) {
				return Decision.SUGGEST_RULE;
			} else if (line.equals("Q")) {
				return Decision.CANCEL_PROOF;
			} else {
				System.out.println("Invalid input");
			}
		}
	}

	private int getIndex(int min, int max) {
		while (true) {
			String line = this.input.nextLine();
			if (line.equals("Q")) {
				return -1;
			}

			try {
				int index = Integer.parseInt(line);
				if (index >= min && index <= max) {
					return index;
				} else {
					System.out.println("Index out of range");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input");
			}
		}
	}
}
