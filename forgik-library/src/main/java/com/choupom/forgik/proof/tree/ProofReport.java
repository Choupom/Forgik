/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof.tree;

import java.util.Map;

import com.choupom.forgik.formula.Formula;

public interface ProofReport {

	Formula getConclusion();

	ProofReport apply(Map<Integer, Formula> map);
}
