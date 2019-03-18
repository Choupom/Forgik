/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.suggester.Suggestion;

public interface ProofIO {

	enum Decision {

		/** Cancel the ongoing proof */
		CANCEL_PROOF,

		/** Complete the ongoing proof (the goal has been proved) */
		COMPLETE_PROOF,

		/** Make an assumption in order to prove the goal (which should be an implication or free variable) */
		ASSUME,

		/** Assume the negation of the goal in order to prove that it is absurd */
		ASSUME_NEGATION,

		/** Suggest rules in order to prove the goal */
		SUGGEST_RULE
	}

	Decision requestDecision(Formula[] entries, Formula goal);

	int requestIdentification(Identification[] identifications);

	int requestSuggestion(Suggestion[] suggestions);

	int requestSubproof(Formula[] entries, Formula[] goals, boolean[] completedGoals);
}
