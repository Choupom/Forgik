/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.choupom.forgik.android.challenge.Challenge;
import com.choupom.forgik.android.challenge.Challenges;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.FormulaSettings;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.prover.ProofInfo;
import com.choupom.forgik.prover.Prover;
import com.choupom.forgik.prover.ProverException;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rulebook.RulebookParser;
import com.google.android.flexbox.FlexboxLayout;

import java.io.IOException;
import java.util.logging.Logger;

public class MainActivity extends Activity {

    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());

    private static final String RULEBOOK = "classical_logic";

    private static final String CONJUNCTION_STRING = "\u2227";
    private static final String DISJUNCTION_STRING = "\u2228";
	private static final String IMPLICATION_STRING = "\u279D";
	private static final String NEGATION_STRING = "\u00AC";

    private final Rule[] rules;
    private Prover prover;
    private int selectedConsequentId;

    public MainActivity() throws IOException {
        FormulaSettings settings = FormulaSettings.getInstance();
        settings.setConjunctionString(CONJUNCTION_STRING);
        settings.setDisjunctionString(DISJUNCTION_STRING);
        settings.setImplicationString(IMPLICATION_STRING);
        settings.setNegationString(NEGATION_STRING);

        this.rules = RulebookParser.parseRulebook(RULEBOOK).getRules();

        Challenge challenge = Challenges.getRandomChallenge();
        this.prover = new Prover(challenge.getAntecedents(), challenge.getConsequents());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_view);

        Button cancelProofButton = findViewById(R.id.cancel_proof_button);
        cancelProofButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelProof();
            }
        });

        Button rerollChallengeButton = findViewById(R.id.reroll_challenge_button);
        rerollChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rerollChallenge();
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
            final int antecedentId = i;
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    completeConsequent(antecedentId);
                }
            };
            boolean identifiable = false;
            if (selectedConsequent != null) {
                Identification identification = FormulaIdentifier.identify(antecedent, selectedConsequent);
                identifiable = (identification != null);
            }
            View view = createAntecedentView(antecedent, identifiable, onClickListener);
            antecedentsTable.addView(view);
        }

        // update consequents table
        TableLayout consequentsTable = findViewById(R.id.consequents_table);
        consequentsTable.removeAllViews();

        for (int i = 0; i < consequents.length; i++) {
            Formula consequent = consequents[i];
            final int consequentId = i;
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectConsequent(consequentId);
                }
            };
            boolean isSelected = (this.selectedConsequentId == i);
            boolean isCompleted = completedConsequents[i];
            View view = createConsequentView(consequent, onClickListener, isSelected, isCompleted);
            consequentsTable.addView(view);
        }

        // update please select consequent text
        TextView informationText = findViewById(R.id.information_text);
        informationText.setVisibility(selectedConsequent == null ? View.VISIBLE : View.GONE);
        if (this.prover.isMainProofComplete()) {
            informationText.setText(R.string.congratulations);
        } else {
            informationText.setText(R.string.please_select_consequent);
        }

        // update apply rule text
        View applyRuleView = findViewById(R.id.apply_rule);
        applyRuleView.setVisibility(selectedConsequent != null ? View.VISIBLE : View.GONE);

        // update rules table
        FlexboxLayout rulesTable = findViewById(R.id.rules_table);
        rulesTable.removeAllViews();

        if (selectedConsequent != null) {
            for (Rule rule : this.rules) {
                View view = createRuleView(rule);
                rulesTable.addView(view);
            }
        }

        // update cancel proof button
        Button cancelProofButton = findViewById(R.id.cancel_proof_button);
        cancelProofButton.setEnabled(!this.prover.isOnMainProof());
    }

    private View createAntecedentView(Formula antecedent, boolean identifiable, View.OnClickListener onClickListener) {
        View row = getLayoutInflater().inflate(R.layout.antecedent_entry, null);

        TextView leftFormulaView = row.findViewById(R.id.antecedent);
        leftFormulaView.setText(antecedent.toString());

        Button identifyButton = row.findViewById(R.id.identify);
        identifyButton.setOnClickListener(onClickListener);
        identifyButton.setEnabled(identifiable);

        return row;
    }

    private View createConsequentView(Formula formula, View.OnClickListener onClickListener,
                                      boolean isSelected, boolean isCompleted) {
        View row = getLayoutInflater().inflate(R.layout.consequent_entry, null);
        row.setOnClickListener(onClickListener);
        row.setEnabled(!isCompleted);
        if (isSelected) {
            row.setBackgroundColor(Color.parseColor("#DDDDFF"));
        }

        TextView leftFormulaView = row.findViewById(R.id.consequent);
        leftFormulaView.setText(formula.toString());

        ImageView stateImage = row.findViewById(R.id.consequent_state);
        int image = (isCompleted ? R.drawable.baseline_assignment_turned_in_black_24 : R.drawable.baseline_assignment_black_24);
        String color = (isCompleted ? "#22BB55" : "#AA3355");
        stateImage.setImageResource(image);
        stateImage.setColorFilter(Color.parseColor(color));

        return row;
    }

    private View createRuleView(final Rule rule) {
        Button button = (Button) getLayoutInflater().inflate(R.layout.rule_entry, null);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proveByRule(rule);
            }
        });
        String ruleName = rule.getName();
        ruleName = ruleName.replace("1", "<sub><small>1</small></sub>");
        ruleName = ruleName.replace("2", "<sub><small>2</small></sub>");
        button.setText(Html.fromHtml(ruleName));
        return button;
    }

    private void selectConsequent(int consequentId) {
        this.selectedConsequentId = consequentId;
        updateView();
    }

    private void completeConsequent(int antecedentId) {
		if (this.selectedConsequentId != -1) {
		    try {
                this.prover.completeConsequent(this.selectedConsequentId, antecedentId);
            } catch (ProverException e) {
		        LOGGER.severe(e.getMessage());
		        return;
            }
            resetProofState();
        }
    }

    private void proveByRule(Rule rule) {
        if (this.selectedConsequentId != -1) {
            try {
                this.prover.proveByRule(this.selectedConsequentId, rule);
            } catch (ProverException e) {
                LOGGER.severe(e.getMessage());
                return;
            }
            resetProofState();
        }
    }

    private void cancelProof() {
        try {
          this.prover.cancelProof();
        } catch (ProverException e) {
            LOGGER.severe(e.getMessage());
            return;
        }
        resetProofState();
    }

    private void rerollChallenge() {
        Challenge challenge = Challenges.getRandomChallenge();
        this.prover = new Prover(challenge.getAntecedents(), challenge.getConsequents());
        resetProofState();
    }

    private void resetProofState() {
        this.selectedConsequentId = -1;

        ProofInfo proofInfo = this.prover.getProofInfo();
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
