# Classical logic rulebook

These two rules:
 * `X, -X |- F` (special case of modus ponens)
 * `F |- X` (ex falso quodlibet)

Have been replaced by this rule:
 * `X, -X |- Y` (ex falso quodlibet)

By doing so, we never need to mention `F`, and the user does not have to know that `-X` is equivalent to `X > F`
