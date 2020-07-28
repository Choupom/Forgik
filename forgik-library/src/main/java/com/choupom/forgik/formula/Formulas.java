/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

import java.util.Arrays;
import java.util.Iterator;

import com.choupom.forgik.operations.EqualsOperation;
import com.choupom.forgik.operations.GetStringOperation;
import com.choupom.forgik.utils.ArrayIterator;

/**
 * A formulas object is an immutable formula array providing utility methods related to formulas.
 */
public class Formulas implements Iterable<Formula> {

	private final Formula[] array;

	private Formulas(Formula[] array) {
		this.array = array;
	}

	public static Formulas list(Formula... list) {
		return new Formulas(list);
	}

	public static Formulas concat(Formulas formulas1, Formulas formulas2) {
		int numFormulas1 = formulas1.array.length;
		int numFormulas2 = formulas2.array.length;
		Formula[] array = new Formula[numFormulas1 + numFormulas2];
		System.arraycopy(formulas1.array, 0, array, 0, numFormulas1);
		System.arraycopy(formulas2.array, 0, array, numFormulas1, numFormulas2);
		return new Formulas(array);
	}

	public static Formulas wrap(Formula[] array) {
		return new Formulas(array);
	}

	public Formulas getCopyOfRange(int from, int to) {
		return new Formulas(Arrays.copyOfRange(this.array, from, to));
	}

	public Formula get(int index) {
		return this.array[index];
	}

	public int size() {
		return this.array.length;
	}

	@Override
	public Iterator<Formula> iterator() {
		return new ArrayIterator<Formula>(this.array);
	}

	public void runOperation(VoidFormulaOperation<Void> operation) {
		for (Formula formula : this.array) {
			formula.runOperation(operation);
		}
	}

	public <P> void runOperation(VoidFormulaOperation<P> operation, P param) {
		for (Formula formula : this.array) {
			formula.runOperation(operation, param);
		}
	}

	public Formulas runOperation(FormulaOperation<Formula, Void> operation) {
		Formula[] newFormulas = new Formula[this.array.length];
		for (int i = 0; i < newFormulas.length; i++) {
			newFormulas[i] = this.array[i].runOperation(operation);
		}
		return new Formulas(newFormulas);
	}

	public <P> Formulas runOperation(FormulaOperation<Formula, P> operation, P param) {
		Formula[] newFormulas = new Formula[this.array.length];
		for (int i = 0; i < newFormulas.length; i++) {
			newFormulas[i] = this.array[i].runOperation(operation, param);
		}
		return new Formulas(newFormulas);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (!(object instanceof Formulas)) {
			return false;
		} else {
			Formulas other = (Formulas) object;

			if (this.array.length != other.array.length) {
				return false;
			}

			EqualsOperation equalsOperation = new EqualsOperation();
			for (int i = 0; i < this.array.length; i++) {
				if (!this.array[i].runOperation(equalsOperation, other.array[i])) {
					return false;
				}
			}

			return true;
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append('[');

		GetStringOperation getStringOperation = new GetStringOperation(stringBuilder);
		for (int i = 0; i < this.array.length; i++) {
			if(i != 0) {
				stringBuilder.append(", ");
			}
			this.array[i].runOperation(getStringOperation, Boolean.FALSE);
		}

		stringBuilder.append(']');
		return stringBuilder.toString();
	}
}
