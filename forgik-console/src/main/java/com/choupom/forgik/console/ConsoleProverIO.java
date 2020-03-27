/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.console;

import java.util.Scanner;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.rule.Rule;

public class ConsoleProverIO {

	enum Decision {

		/** Cancel the ongoing proof */
		CANCEL_PROOF,

		/** Complete the ongoing proof (the goal has been proved) */
		COMPLETE_PROOF,

		/** Suggest rules in order to prove the goal */
		SUGGEST_RULE
	}

	private final Scanner input;

	public ConsoleProverIO() {
		this.input = new Scanner(System.in);
	}

	public Decision requestDecision(Formulas entries, Formula goal) {
		int i = 0;
		for (Formula entry : entries) {
			System.out.println("[" + (i++) + "] " + entry);
		}
		System.out.println("...");
		System.out.println("[GOAL] " + goal + "?");

		// System.out.println("What do you want to do?");
		// System.out.println("{Q} Cancel this proof");
		// System.out.println("{<} Complete this proof");
		// System.out.println("{R} Suggest rules");
		return getDecision();
	}

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

	public int requestRule(Rule[] rules) {
		System.out.println("Which rule do you want to use?");

		for (int i = 0; i < rules.length; i++) {
			System.out.println("{" + i + "} " + rules[i].getName());
		}

		return getIndex(0, rules.length - 1);
	}

	public int requestSubproof(Formulas entries, Formulas goals, boolean[] completedGoals) {
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

		for (i = 0; i < goals.size(); i++) {
			if (!completedGoals[i]) {
				System.out.println("{" + i + "} " + goals.get(i));
			}
		}

		while (true) {
			int index = getIndex(0, goals.size() - 1);
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
			} else if (line.equals("R")) {
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
