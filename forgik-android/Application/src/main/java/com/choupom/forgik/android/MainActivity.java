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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.choupom.forgik.android.challenge.Challenge;
import com.choupom.forgik.android.challenge.Challenges;
import com.choupom.forgik.formula.BinaryConnective;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.UnaryConnective;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.prover.ProofInfo;
import com.choupom.forgik.prover.Prover;
import com.choupom.forgik.prover.ProverException;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rulebook.RulebookParser;

import java.io.IOException;
import java.util.logging.Logger;

public class MainActivity extends Activity {

    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());

    private static final String RULEBOOK = "classical_logic";

    private static final String NEGATION_SYMBOL = "\u00AC";
    private static final String IMPLICATION_SYMBOL = "\u279D";
    private static final String CONJUNCTION_SYMBOL = "\u2227";
    private static final String DISJUNCTION_SYMBOL = "\u2228";

    private static final int[] RULES_PER_ROW = new int[] {2, 3, 3, 3};

    private final Rule[] rules;
    private Prover prover;
    private int currentChallengeIndex;
    private int selectedConsequentId;

    public MainActivity() throws IOException {
        this.rules = RulebookParser.parseRulebook(RULEBOOK).getRules();
        loadChallenge(0);
    }

    private void loadChallenge(int index) {
        Challenge challenge = Challenges.getChallenge(index);
        this.prover = new Prover(challenge.getAntecedents(), challenge.getConsequents());
        this.currentChallengeIndex = index;
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

        Button nextChallengeButton = findViewById(R.id.next_challenge_button);
        nextChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextChallenge();
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
        updateAntecedentsTable(antecedents, selectedConsequent);

        // update consequents table
        updateConsequentsTable(consequents, completedConsequents);

        // update information text
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
        updateRulesTable();

        // update cancel proof button
        Button cancelProofButton = findViewById(R.id.cancel_proof_button);
        cancelProofButton.setEnabled(!this.prover.isOnMainProof());

        // update next challenge button
        Button nextChallengeButton = findViewById(R.id.next_challenge_button);
        nextChallengeButton.setEnabled(this.prover.isMainProofComplete());
    }

    private void updateAntecedentsTable(Formula[] antecedents, Formula selectedConsequent) {
        TableLayout antecedentsTable = findViewById(R.id.antecedents_table);
        antecedentsTable.removeAllViews();

        for (int i = 0; i < antecedents.length; i++) {
            View view = createAntecedentView(i, antecedents[i], selectedConsequent);
            antecedentsTable.addView(view);
        }
    }

    private void updateConsequentsTable(Formula[] consequents, boolean[] completedConsequents) {
        TableLayout consequentsTable = findViewById(R.id.consequents_table);
        consequentsTable.removeAllViews();

        for (int i = 0; i < consequents.length; i++) {
            View view = createConsequentView(i, consequents[i], completedConsequents[i]);
            consequentsTable.addView(view);
        }
    }

    private void updateRulesTable() {
        TableLayout rulesTable = findViewById(R.id.rules_table);
        rulesTable.removeAllViews();

        if (this.selectedConsequentId != -1) {
            TableRow row = null;
            int rowIndex = 0;
            int rowCount = 0;

            for (int i = 0; i < this.rules.length; i++) {
                if (row == null) {
                    row = new TableRow(this);
                    row.setGravity(Gravity.CENTER_HORIZONTAL);
                    rulesTable.addView(row);
                }

                View view = createRuleView(this.rules[i]);
                row.addView(view);

                if (i+1 >= rowCount+RULES_PER_ROW[rowIndex]) {
                    row = null;
                    rowCount += RULES_PER_ROW[rowIndex];
                    rowIndex++;
                }
            }
        }
    }

    private View createAntecedentView(final int antecedentId, Formula antecedent, Formula selectedConsequent) {
        boolean identifiable = false;
        if (selectedConsequent != null) {
            Identification identification = FormulaIdentifier.identify(antecedent, selectedConsequent);
            identifiable = (identification != null);
        }

        View row = getLayoutInflater().inflate(R.layout.antecedent_entry, null);

        TextView leftFormulaView = row.findViewById(R.id.antecedent);
        leftFormulaView.setText(replaceConnectiveSymbols(antecedent.toString()));

        Button identifyButton = row.findViewById(R.id.identify);
        identifyButton.setEnabled(identifiable);
        identifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeConsequent(antecedentId);
            }
        });

        return row;
    }

    private View createConsequentView(final int consequentId, Formula consequent, boolean completedConsequent) {
        View row = getLayoutInflater().inflate(R.layout.consequent_entry, null);
        row.setEnabled(!completedConsequent);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectConsequent(consequentId);
            }
        });
        if (consequentId == this.selectedConsequentId) {
            row.setBackgroundColor(Color.parseColor("#DDDDFF"));
        }

        TextView leftFormulaView = row.findViewById(R.id.consequent);
        leftFormulaView.setText(replaceConnectiveSymbols(consequent.toString()));

        ImageView stateImage = row.findViewById(R.id.consequent_state);
        if (completedConsequent) {
            stateImage.setImageResource(R.drawable.baseline_assignment_turned_in_black_24);
            stateImage.setColorFilter(Color.parseColor("#22BB55"));
        } else {
            stateImage.setImageResource(R.drawable.baseline_assignment_black_24);
            stateImage.setColorFilter(Color.parseColor("#AA3355"));
        }

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
        String ruleName = replaceConnectiveSymbols(rule.getName());
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

    private void nextChallenge() {
        int newIndex = this.currentChallengeIndex+1;
        if (newIndex == Challenges.getNumChallenges()) {
            newIndex = 0;
        }

        loadChallenge(newIndex);
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

    private static String replaceConnectiveSymbols(String string) {
        string = string.replace(UnaryConnective.Type.NEGATION.getSymbol(), NEGATION_SYMBOL);
        string = string.replace(BinaryConnective.Type.IMPLICATION.getSymbol(), IMPLICATION_SYMBOL);
        string = string.replace(BinaryConnective.Type.CONJUNCTION.getSymbol(), CONJUNCTION_SYMBOL);
        string = string.replace(BinaryConnective.Type.DISJUNCTION.getSymbol(), DISJUNCTION_SYMBOL);
        return string;
    }
}
