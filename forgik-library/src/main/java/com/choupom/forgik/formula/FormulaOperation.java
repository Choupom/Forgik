/*
 * Java
 *
 * Copyright 2020 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.formula;

public interface FormulaOperation<R, P> {

	R handleBinaryConnective(BinaryConnective binaryConnective, P param);

	R handleUnaryConnective(UnaryConnective unaryConnective, P param);

	R handlePredicate(Predicate predicate, P param);

	R handleFreeFormula(FreeFormula freeFormula, P param);
}
