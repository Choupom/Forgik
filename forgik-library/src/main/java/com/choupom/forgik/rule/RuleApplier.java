/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.formula.FreeFormula;
import com.choupom.forgik.operations.ApplyOperation;
import com.choupom.forgik.operations.GetFreeFormulasOperation;
import com.choupom.forgik.operations.IdentifyOperation;

public class RuleApplier {

	private RuleApplier() {
		// private constructor
	}

	public static boolean canApply(Rule rule, Formula consequent) {
		Map<Integer, Formula> map = new HashMap<>();
		return identify(rule, consequent, map);
	}

	public static RuleApplicationResult apply(Rule rule, Formula consequent) {
		// identify rule consequent with given consequent
		Map<Integer, Formula> map = new HashMap<>();
		if (!identify(rule, consequent, map)) {
			return null;
		}

		// compute assumptions and antecedents
		ApplyOperation applyOperation = new ApplyOperation(map);
		Formulas assumptions = rule.getAssumptions().runOperation(applyOperation);
		Formulas antecedents = rule.getAntecedents().runOperation(applyOperation);

		// list all the free formulas of the rule which have not been identified
		Set<Integer> leftover = getRuleFreeFormulas(rule);
		leftover.removeAll(map.keySet());

		// list all the free formulas of the given consequent
		Set<Integer> consequentFreeFormulas = new HashSet<>();
		consequent.runOperation(new GetFreeFormulasOperation(consequentFreeFormulas));

		// TODO: document this block
		Map<Integer, Formula> consequentMap = new HashMap<>();
		for (Integer consequentFreeFormula : consequentFreeFormulas) {
			Formula mapped = map.get(consequentFreeFormula);
			if (mapped != null) {
				mapped = mapped.runOperation(applyOperation);
				if (!mapped.equals(new FreeFormula(consequentFreeFormula))) {
					consequentMap.put(consequentFreeFormula, mapped);
				}
			}
		}

		// return application result
		return new RuleApplicationResult(assumptions, antecedents, leftover, consequentMap);
	}

	private static boolean identify(Rule rule, Formula consequent, Map<Integer, Formula> map) {
		// TODO: use FormulaIdentifier instead of IdentifyOperation
		IdentifyOperation identifyOperation = new IdentifyOperation(true, map);
		return rule.getConsequent().runOperation(identifyOperation, consequent);
	}

	private static Set<Integer> getRuleFreeFormulas(Rule rule) {
		Set<Integer> freeFormulas = new HashSet<>();
		GetFreeFormulasOperation getOperation = new GetFreeFormulasOperation(freeFormulas);
		rule.getAssumptions().runOperation(getOperation);
		rule.getAntecedents().runOperation(getOperation);
		rule.getConsequent().runOperation(getOperation);
		return freeFormulas;
	}
}
