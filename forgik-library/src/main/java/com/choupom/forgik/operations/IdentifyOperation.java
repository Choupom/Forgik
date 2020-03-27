/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.operations;

import java.util.Map;

import com.choupom.forgik.formula.BinaryConnective;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FormulaOperation;
import com.choupom.forgik.formula.FreeFormula;
import com.choupom.forgik.formula.Predicate;
import com.choupom.forgik.formula.UnaryConnective;

public class IdentifyOperation implements FormulaOperation<Boolean, Formula> {

	private final boolean assertAllEquals;
	private final Map<Integer, Formula> map;

	public IdentifyOperation(boolean assertAllEquals, Map<Integer, Formula> map) {
		this.assertAllEquals = assertAllEquals;
		this.map = map;
	}

	@Override
	public Boolean handleBinaryConnective(BinaryConnective binaryConnective, Formula other) {
		if (other instanceof FreeFormula) {
			return other.runOperation(this, binaryConnective);
		}

		if (!(other instanceof BinaryConnective)) {
			return Boolean.FALSE;
		}

		BinaryConnective otherConnective = (BinaryConnective) other;
		return (binaryConnective.getType() == otherConnective.getType()
				&& binaryConnective.getOperand1().runOperation(this, otherConnective.getOperand1())
				&& binaryConnective.getOperand2().runOperation(this, otherConnective.getOperand2()));
	}

	@Override
	public Boolean handleUnaryConnective(UnaryConnective unaryConnective, Formula other) {
		if (other instanceof FreeFormula) {
			return other.runOperation(this, unaryConnective);
		}

		if (!(other instanceof UnaryConnective)) {
			return Boolean.FALSE;
		}

		UnaryConnective otherConnective = (UnaryConnective) other;
		return (unaryConnective.getType() == otherConnective.getType()
				&& unaryConnective.getOperand().runOperation(this, otherConnective.getOperand()));
	}

	@Override
	public Boolean handlePredicate(Predicate predicate, Formula other) {
		if (other instanceof FreeFormula) {
			return other.runOperation(this, predicate);
		}

		if (!(other instanceof Predicate)) {
			return Boolean.FALSE;
		}

		Predicate otherPredicate = (Predicate) other;
		return (predicate.getName() == otherPredicate.getName());
	}

	@Override
	public Boolean handleFreeFormula(FreeFormula freeFormula, Formula other) {
		if (other instanceof FreeFormula) {
			FreeFormula otherFreeFormula = (FreeFormula) other;
			if (otherFreeFormula.getId() == freeFormula.getId()) {
				return Boolean.TRUE;
			}
			if (!addToMap(otherFreeFormula.getId(), freeFormula)) {
				return Boolean.FALSE;
			}
		}

		return addToMap(freeFormula.getId(), other);
	}

	private boolean addToMap(int id, Formula formula) {
		Integer idInteger = Integer.valueOf(id);
		Formula mapFormula = this.map.get(idInteger);
		if (mapFormula == null) {
			this.map.put(idInteger, formula);
			return true;
		} else {
			return (!this.assertAllEquals || mapFormula.equals(formula));
		}
	}
}
