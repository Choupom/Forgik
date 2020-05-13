/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.android;

import java.util.Arrays;

public class FormulaId {

	private int[] proofPath;
	private int FormulaId;

	public FormulaId(int[] proofPath, int FormulaId) {
		this.proofPath = proofPath.clone();
		this.FormulaId = FormulaId;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof com.choupom.forgik.android.FormulaId)) {
			return false;
		} else {
			com.choupom.forgik.android.FormulaId other = (com.choupom.forgik.android.FormulaId) obj;
			return (Arrays.equals(other.proofPath, this.proofPath) && other.FormulaId == this.FormulaId);
		}
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.proofPath) * 1000 + this.FormulaId;
	}
}
