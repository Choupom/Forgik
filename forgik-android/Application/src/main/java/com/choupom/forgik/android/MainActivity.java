/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.choupom.forgik.android.challenge.Challenge;
import com.choupom.forgik.android.challenge.Challenges;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FormulaSettings;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.prover.ProofInfo;
import com.choupom.forgik.prover.Prover;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rule.RulebookParser;
import com.choupom.forgik.suggester.FormulaSuggester;
import com.choupom.forgik.suggester.Suggestion;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class MainActivity extends Activity {

    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());

    private static final String RULEBOOK = "deduction";

    private static final String CONJUNCTION_STRING = "\u2227";
    private static final String DISJUNCTION_STRING = "\u2228";
	private static final String IMPLICATION_STRING = "\u279D";
	private static final String NEGATION_STRING = "\u00AC";

    private final Rule[] rules;
    private final Prover prover;
    private int selectedConsequentId;

    public MainActivity() throws IOException {
        FormulaSettings.setOperatorsStrings(CONJUNCTION_STRING, DISJUNCTION_STRING, IMPLICATION_STRING, NEGATION_STRING);

        Challenge[] challenges = Challenges.getChallenges();
        int challengeId = new Random().nextInt(challenges.length);
        Challenge challenge = challenges[challengeId];

        this.rules = RulebookParser.parseRulebook(RULEBOOK);
        this.prover = new Prover(challenge.getAntecedents(), challenge.getConsequents());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_view);

        Button proveImplicationButton = findViewById(R.id.prove_implication_button);
        proveImplicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proveByImplication();
            }
        });

        Button proveByContradictionButton = findViewById(R.id.prove_by_contradiction_button);
        proveByContradictionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proveByContradiction();
            }
        });

        Button cancelProofButton = findViewById(R.id.cancel_proof_button);
        cancelProofButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelProof();
            }
        });

        resetProofState();
    }

    private void updateView() {
    	// get proof info
        ProofInfo proofInfo = this.prover.getProofInfo();
        Formula[] antecedents = proofInfo.getAntecedents();
        Formula[] consequents = proofInfo.getConsequents();
        boolean[] completedConsequents = proofInfo.getCompletedConsequents();

		Formula selectedConsequent = null;
		if (this.selectedConsequentId != -1) {
			selectedConsequent = consequents[this.selectedConsequentId];
		}

        // update antecedents table
        TableLayout antecedentsTable = findViewById(R.id.antecedents_table);
        antecedentsTable.removeAllViews();

        for (int i = 0; i < antecedents.length; i++) {
            Formula antecedent = antecedents[i];
            Formula identifiedFormula = null;
            View.OnClickListener onClickListener = null;
            if (selectedConsequent != null) {
                final Identification identification = FormulaIdentifier.identify(antecedent, selectedConsequent);
                if (identification != null) {
                    identifiedFormula = identification.getFormula();
                    final int antecedentId = i;
                    onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            completeConsequent(antecedentId, identification.getMap());
                        }
                    };
                }
            }
            View view = createAntecedentView(antecedent, identifiedFormula, onClickListener);
            antecedentsTable.addView(view);
        }

        // update consequents table
        TableRow consequentsTable = findViewById(R.id.consequents_table);
        consequentsTable.removeAllViews();

        for (int i = 0; i < consequents.length; i++) {
            Formula consequent = consequents[i];
            final int consequentId = i;
            View.OnClickListener onClickListener = null;
            if (!completedConsequents[i]) {
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectConsequent(consequentId);
                    }
                };
            }
            boolean isSelected = (this.selectedConsequentId == i);
            boolean isCompleted = completedConsequents[i];
            View view = createConsequentView(consequent, onClickListener, isSelected, isCompleted);
            consequentsTable.addView(view);
        }

        // update rules table
        LinearLayout rulesTable = findViewById(R.id.rules_table);
        rulesTable.removeAllViews();

        if (selectedConsequent != null) {
            Suggestion[] suggestions = FormulaSuggester.suggest(selectedConsequent, this.rules);

            for (Suggestion suggestion : suggestions) {
                View view = createRuleView(suggestion);
                rulesTable.addView(view);
            }
        }

        // update prove implication button
		boolean isImplication = (this.selectedConsequentId != -1 && this.prover.canProveImplication(this.selectedConsequentId));
		Button proveImplicationButton = findViewById(R.id.prove_implication_button);
		proveImplicationButton.setVisibility(isImplication ? View.VISIBLE : View.GONE);

		// update prove by contradiction button
		boolean isNegation = (this.selectedConsequentId != -1 && this.prover.canProveByContradiction(this.selectedConsequentId));
		Button proveByContradictionButton = findViewById(R.id.prove_by_contradiction_button);
		proveByContradictionButton.setVisibility(isNegation ? View.VISIBLE : View.GONE);
    }

    private View createAntecedentView(Formula leftFormula, @Nullable Formula rightFormula,
                        View.OnClickListener onClickListener) {
        TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.antecedent_entry, null);

        TextView leftFormulaView = row.findViewById(R.id.left_formula);
        leftFormulaView.setText(leftFormula.toString());

        if (rightFormula != null) {
            TextView rightFormulaView = row.findViewById(R.id.right_formula);
            rightFormulaView.setText(rightFormula.toString());
			rightFormulaView.setOnClickListener(onClickListener);
        }

        return row;
    }

    private View createConsequentView(Formula formula, View.OnClickListener onClickListener,
                                      boolean isSelected, boolean isCompleted) {
        TextView column = (TextView) getLayoutInflater().inflate(R.layout.consequent_entry, null);
        column.setText(formula.toString());
        column.setOnClickListener(onClickListener);
        if (isCompleted) {
            column.setBackgroundColor(0xFFBBBBBB);
        } else if (isSelected) {
            column.setBackgroundColor(0xFFDDDDFF);
        }

        return column;
    }

    private View createRuleView(final Suggestion suggestion) {
        TableLayout table = (TableLayout) getLayoutInflater().inflate(R.layout.rule_entry, null);

        TableRow row = new TableRow(this);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proveByRule(suggestion.getRule());
            }
        });

        for (Formula formula : suggestion.getFormulas()) {
            TextView column = (TextView) getLayoutInflater().inflate(R.layout.consequent_entry, null);
            column.setText(formula.toString());
            row.addView(column);
        }

        table.addView(row);
        return table;
    }

    private void selectConsequent(int consequentId) {
        this.selectedConsequentId = consequentId;
        updateView();
    }

    private void completeConsequent(int antecedentId, Map<String, Formula> map) {
		if (this.selectedConsequentId != -1) {
            this.prover.completeConsequent(this.selectedConsequentId, antecedentId, map);
            resetProofState();
        }
    }

    private void proveByImplication() {
        if (this.selectedConsequentId != -1) {
            this.prover.proveImplication(this.selectedConsequentId);
            resetProofState();
        }
    }

    private void proveByContradiction() {
        if (this.selectedConsequentId != -1) {
            this.prover.proveByContradiction(this.selectedConsequentId);
            resetProofState();
        }
    }

    private void proveByRule(Rule rule) {
        if (this.selectedConsequentId != -1) {
            this.prover.proveByRule(this.selectedConsequentId, rule);
            resetProofState();
        }
    }

    private void cancelProof() {
        this.prover.cancelProof();
        resetProofState();
    }

    private void resetProofState() {
        this.selectedConsequentId = -1;

        ProofInfo proofInfo = this.prover.getProofInfo();
        if (proofInfo == null) {
        	finish();
        	return;
		}
        boolean[] completedConsequents = proofInfo.getCompletedConsequents();

        int uncompletedConsequentId = -1;
        int numUncompletedConsequents = 0;
        for (int i = 0; i < completedConsequents.length; i++) {
            if (!completedConsequents[i]) {
                uncompletedConsequentId = i;
                numUncompletedConsequents++;
            }
        }
        if (numUncompletedConsequents == 1) {
            this.selectedConsequentId = uncompletedConsequentId;
        }

        updateView();
    }
}
