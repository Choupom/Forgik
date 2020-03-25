/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.proof.linear;

import com.choupom.forgik.formula.Formula;

public interface Statement {

	Formula getConclusion();

	int getDepth();
}
