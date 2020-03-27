/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.operations.ApplyOperation;
import com.choupom.forgik.operations.ContainsFreeFormulaOperation;
import com.choupom.forgik.operations.IdentifyOperation;

public class FormulaIdentifier {

	private FormulaIdentifier() {
		// private constructor
	}

	public static Identification identify(Formula formula1, Formula formula2) {
		// create result map
		Map<Integer, Formula> resultMap = new HashMap<>();

		while (true) {
			// identify formulas
			Map<Integer, Formula> identificationMap = new HashMap<>();
			if (!formula2.runOperation(new IdentifyOperation(false, identificationMap), formula1)) {
				return null;
			}

			// check if there is no difference between the formulas
			if (identificationMap.isEmpty()) {
				break; // identification complete!
			}

			// create substitution map
			Integer originalFormula = Collections.max(identificationMap.keySet());
			Formula substituteFormula = identificationMap.get(originalFormula);
			Map<Integer, Formula> substitutionMap = new HashMap<>();
			substitutionMap.put(originalFormula, substituteFormula);

			// check infinite self substitution
			if (substituteFormula.runOperation(new ContainsFreeFormulaOperation(originalFormula))) {
				return null;
			}

			// apply substitution map
			ApplyOperation applyOperation = new ApplyOperation(substitutionMap);
			formula1 = formula1.runOperation(applyOperation);
			formula2 = formula2.runOperation(applyOperation);
			for (Map.Entry<Integer, Formula> entry : resultMap.entrySet()) {
				entry.setValue(entry.getValue().runOperation(applyOperation));
			}

			// merge substitution map into result map
			resultMap.putAll(substitutionMap);
		}

		// return identification result
		if (!formula1.equals(formula2)) {
			throw new IllegalStateException();
		}
		return new Identification(formula1, resultMap);
	}
}
