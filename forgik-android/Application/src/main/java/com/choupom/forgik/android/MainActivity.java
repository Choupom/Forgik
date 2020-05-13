/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import com.choupom.forgik.challenge.Challenge;
import com.choupom.forgik.formula.BinaryConnective;
import com.choupom.forgik.formula.Formula;
import com.choupom.forgik.formula.Formulas;
import com.choupom.forgik.formula.FreeFormula;
import com.choupom.forgik.formula.UnaryConnective;
import com.choupom.forgik.identifier.FormulaIdentifier;
import com.choupom.forgik.identifier.Identification;
import com.choupom.forgik.proof.ProofConverter;
import com.choupom.forgik.proof.linear.AssumptionStatement;
import com.choupom.forgik.proof.linear.PremiseStatement;
import com.choupom.forgik.proof.linear.RuleStatement;
import com.choupom.forgik.proof.linear.Statement;
import com.choupom.forgik.proof.tree.ProofReport;
import com.choupom.forgik.prover.ProofInfo;
import com.choupom.forgik.prover.Prover;
import com.choupom.forgik.prover.ProverException;
import com.choupom.forgik.rule.Rule;
import com.choupom.forgik.rule.RuleApplier;
import com.choupom.forgik.rulebook.Rulebook;
import com.choupom.forgik.rulebook.RulebookParser;
import com.google.android.gms.common.util.ArrayUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MainActivity extends Activity {

    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());

    private static final String FREE_FORMULA_SYMBOL = "\u03C6";
    private static final String NEGATION_SYMBOL = "\u00AC";
    private static final String IMPLICATION_SYMBOL = "\u279D";
    private static final String CONJUNCTION_SYMBOL = "\u2227";
    private static final String DISJUNCTION_SYMBOL = "\u2228";

    private static final int[] RULES_PER_ROW = new int[] {2, 3, 3, 3};

    private final Challenge[] challenges;
    private final LinearLayout[] ruleRows;
    private final Map<Integer, View> antecedentViews;
    private final Map<FormulaId, View> consequentViews;
    private final Map<Rule, Button> ruleViews;
    private final Map<Integer, View> statementViews;

    private int currentChallengeIndex;
    private Rule[] rules;
    private Prover prover;

    private int selectedConsequentId;
    private int selectedStatementId;

    public MainActivity() {
        this.challenges = loadChallenges();
        this.ruleRows = new LinearLayout[RULES_PER_ROW.length];
        this.antecedentViews = new HashMap<>();
        this.consequentViews = new HashMap<>();
        this.ruleViews = new HashMap<>();
        this.statementViews = new HashMap<>();
        loadChallenge(0);
    }

    private Challenge[] loadChallenges() {
        try {
            return ChallengesHelper.loadSortedChallenges();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void loadChallenge(int index) {
        try {
            Challenge challenge = this.challenges[index];
            Rulebook rulebook = RulebookParser.parseRulebook(challenge.getRulebook());
            this.rules = rulebook.getRules();
            this.prover = new Prover(challenge.getAntecedents(), challenge.getConsequents());
            this.currentChallengeIndex = index;
            for (int i = 0; i < this.ruleRows.length; i++) {
                this.ruleRows[i] = null;
            }
            this.antecedentViews.clear();
            this.consequentViews.clear();
            this.statementViews.clear();
            this.ruleViews.clear();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_view);

        Button cancelProofButton = (Button) findViewById(R.id.cancel_subproof_button);
        cancelProofButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelSubproof();
            }
        });

        Button nextChallengeButton = (Button) findViewById(R.id.next_challenge_button);
        nextChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextChallenge();
            }
        });

        resetProofState(-1);
    }

    private void updateView() {
        // start transition
        ViewGroup mainView = (ViewGroup) findViewById(R.id.main_view);
        AutoTransition autoTransition = new AutoTransition();
        autoTransition.excludeTarget(R.id.consequent, true);
        TransitionManager.beginDelayedTransition(mainView, autoTransition);

        // get proof info
        ProofInfo proofInfo = this.prover.getProofInfo();

        // get selected consequent
        Formulas consequents = proofInfo.getConsequents();
        Formula selectedConsequent = null;
        if (this.selectedConsequentId != -1) {
            selectedConsequent = consequents.get(this.selectedConsequentId);
        }

        // update antecedents table
        updateAntecedentsTable(proofInfo, selectedConsequent);

        // update consequents table
        updateConsequentsTable(proofInfo);

        // update rules table
        updateRulesTable(selectedConsequent);

        // update proof table
        updateProofTable(proofInfo);

        // update cancel subproof button
        Button cancelSubproofButton = (Button) findViewById(R.id.cancel_subproof_button);
        cancelSubproofButton.setEnabled(!this.prover.isOnMainProof());
    }

    private void updateAntecedentsTable(ProofInfo proofInfo, Formula selectedConsequent) {
        LinearLayout antecedentsTable = (LinearLayout) findViewById(R.id.antecedents_table);
        antecedentsTable.removeAllViews();

        int index = 0;
        for (Formula antecedent : proofInfo.getAntecedents()) {
            Integer antecedentId = Integer.valueOf(index);
            View row = this.antecedentViews.get(antecedentId);
            if (row == null) {
                row = getLayoutInflater().inflate(R.layout.antecedent_entry, null);
                this.antecedentViews.put(antecedentId, row);
            }
            updateAntecedentView(row, index, antecedent, selectedConsequent);
            antecedentsTable.addView(row);
            index++;
        }
    }

    private void updateConsequentsTable(ProofInfo proofInfo) {
        LinearLayout consequentsTable = (LinearLayout) findViewById(R.id.consequents_table);
        consequentsTable.removeAllViews();

        int maxDepth = computeMaxDepth(proofInfo);

		int childConsequentId = -1;
        Rule childConsequentRule = null;
		int depth = 0;
        while (proofInfo != null) {
            int index = 0;
            boolean[] completedConsequents = proofInfo.getCompletedConsequents();
            for (Formula consequent : proofInfo.getConsequents()) {
                FormulaId consequentId = new FormulaId(proofInfo.getPath(), index);
                View row = this.consequentViews.get(consequentId);
                if (row == null) {
                    row = getLayoutInflater().inflate(R.layout.consequent_entry, null);
                    this.consequentViews.put(consequentId, row);
                }
                boolean completed = completedConsequents[index];
                boolean ongoing = (index == childConsequentId);
                boolean selectable = (!completed && depth == 0);
                updateConsequentView(row, index, consequent, completed, ongoing, selectable, maxDepth-depth, childConsequentRule);
                consequentsTable.addView(row);
                index++;
            }
            childConsequentId = proofInfo.getParentConsequentId();
            childConsequentRule = proofInfo.getParentConsequentRule();
            proofInfo = proofInfo.getParentProof();
            depth++;
        }
    }

    private void updateRulesTable(Formula selectedConsequent) {
        LinearLayout rulesTable = (LinearLayout) findViewById(R.id.rules_table);
        rulesTable.setVisibility(!this.prover.isMainProofComplete() ? View.VISIBLE : View.GONE);
        rulesTable.removeAllViews();

        int rowIndex = 0;
        int rowCount = 0;

        for (int i = 0; i < this.rules.length; i++) {
            LinearLayout row = this.ruleRows[rowIndex];
            if (row == null) {
                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_HORIZONTAL);
                this.ruleRows[rowIndex] = row;
            }
            if (i == rowCount) {
                rulesTable.addView(row);
                row.removeAllViews();
            }

            Rule rule = this.rules[i];
            Button button = this.ruleViews.get(rule);
            if (button == null) {
                button = (Button) getLayoutInflater().inflate(R.layout.rule_entry, null);
                this.ruleViews.put(rule, button);
            }
            updateRuleView(button, rule, selectedConsequent);
            row.addView(button);

            if (i+1 >= rowCount+RULES_PER_ROW[rowIndex]) {
                rowCount += RULES_PER_ROW[rowIndex];
                rowIndex++;
            }
        }
    }

    private void updateProofTable(ProofInfo proofInfo) {
        LinearLayout proofTable = (LinearLayout) findViewById(R.id.proof_table);
        proofTable.removeAllViews();

        if (this.prover.isMainProofComplete()) {
            Statement[] statements = ProofConverter.generateLinearProof(proofInfo.getAntecedents(), proofInfo.getConsequentProofs());
            int[] selectedAntecedentStatements = new int[0];
            if (this.selectedStatementId != -1 && statements[this.selectedStatementId] instanceof RuleStatement) {
                RuleStatement selectedStatement = (RuleStatement) statements[this.selectedStatementId];
                selectedAntecedentStatements = selectedStatement.getAntecedentStatements();
            }

            for (int i = 0; i < statements.length; i++) {
                View row = this.statementViews.get(i);
                if (row == null) {
                    row = getLayoutInflater().inflate(R.layout.proof_entry, null);
                    this.statementViews.put(i, row);
                }
                updateStatementView(row, i, statements[i], selectedAntecedentStatements);
                proofTable.addView(row);
            }
        }
    }

    private void updateAntecedentView(View row, final int antecedentId, Formula antecedent, Formula selectedConsequent) {
        boolean identifiable = false;
        if (selectedConsequent != null) {
            Identification identification = FormulaIdentifier.identify(antecedent, selectedConsequent);
            identifiable = (identification != null);
        }

        TextView leftFormulaView = (TextView) row.findViewById(R.id.antecedent);
        leftFormulaView.setText(createFormulaText(antecedent));

        Button identifyButton = (Button) row.findViewById(R.id.identify);
        identifyButton.setEnabled(identifiable);
        identifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeConsequent(antecedentId);
            }
        });
    }

    private void updateConsequentView(View row, final int consequentId, Formula consequent, boolean completed, boolean ongoing, boolean selectable, int depth, Rule rule) {
        row.setEnabled(selectable);
        if (selectable) {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectConsequent(consequentId);
                }
            });
        }
        if (selectable && consequentId == this.selectedConsequentId) {
            row.setBackgroundColor(Color.parseColor("#DDDDFF"));
        } else {
            row.setBackgroundColor(Color.parseColor("#EEEEEE"));
        }

        TextSwitcher leftFormulaView = (TextSwitcher) row.findViewById(R.id.consequent);

        Animation inAnimation = new AlphaAnimation(0.0f, 1.0f);
        inAnimation.setDuration(300);
        leftFormulaView.setInAnimation(inAnimation);

        Animation outAnimation = new AlphaAnimation(1.0f, 0.0f);
        outAnimation.setDuration(300);
        leftFormulaView.setOutAnimation(outAnimation);

        CharSequence oldText = ((TextView) leftFormulaView.getCurrentView()).getText();
        CharSequence newText = createFormulaText(consequent);
        if (!newText.toString().equals(oldText.toString())) {
            leftFormulaView.setText(newText);
        }

        StatementConclusionView currentView = (StatementConclusionView) leftFormulaView.getCurrentView();
        currentView.setPadding(depth*StatementConclusionView.INDENT_WIDTH, 0, 0, 0);
        currentView.setDepth(depth);

        ImageView stateImage = (ImageView) row.findViewById(R.id.consequent_state);
        stateImage.setVisibility(!ongoing ? View.VISIBLE : View.GONE);
        if (completed) {
            stateImage.setImageResource(R.drawable.baseline_assignment_turned_in_black_24);
            stateImage.setColorFilter(Color.parseColor("#22BB55"));
        } else {
            stateImage.setImageResource(R.drawable.baseline_assignment_black_24);
            stateImage.setColorFilter(Color.parseColor("#AA3355"));
        }

        TextView ruleView = (TextView) row.findViewById(R.id.consequent_rule);
        ruleView.setVisibility(ongoing ? View.VISIBLE : View.GONE);
        if (ongoing) {
            ruleView.setText(makeHtml(formatRuleName(rule.getName())));
        }
    }

    private void updateRuleView(Button button, final Rule rule, Formula selectedConsequent) {
        button.setEnabled(selectedConsequent != null && RuleApplier.canApply(rule, selectedConsequent));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proveByRule(rule);
            }
        });
        button.setText(makeHtml(formatRuleName(rule.getName())));
    }

    private void updateStatementView(View row, final int statementId, Statement statement, int[] selectedAntecedentStatements) {
        StatementConclusionView conclusionView = (StatementConclusionView) row.findViewById(R.id.proof_conclusion);
        TextView sourceView = (TextView) row.findViewById(R.id.proof_source);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectStatement(statementId);
            }
        });

        if (statementId == this.selectedStatementId) {
            row.setBackgroundColor(Color.parseColor("#DDDDFF"));
        } else if (ArrayUtils.contains(selectedAntecedentStatements, statementId)) {
            row.setBackgroundColor(Color.parseColor("#F7DDDD"));
        } else {
            row.setBackgroundColor(Color.parseColor("#EEEEEE"));
        }

        conclusionView.setPadding(statement.getDepth()*StatementConclusionView.INDENT_WIDTH, 0, 0, 0);
        conclusionView.setDepth(statement.getDepth());

        String conclusionString = replaceConnectiveSymbols(statement.getConclusion().toString());
        conclusionView.setText(makeHtml(conclusionString));

        String sourceString = "";
        if (statement instanceof PremiseStatement) {
            sourceString = "premise";
        } else if (statement instanceof AssumptionStatement) {
            sourceString = "assumption";
        } else if (statement instanceof RuleStatement) {
            RuleStatement ruleStatement = (RuleStatement) statement;
            sourceString = formatRuleName(ruleStatement.getRule().getName());
        }

        sourceView.setText(makeHtml(sourceString));
    }

    private void selectConsequent(int consequentId) {
        this.selectedConsequentId = consequentId;
        updateView();
    }

    private void selectStatement(int statementId) {
        this.selectedStatementId = statementId;
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
            resetProofState(-1);

            if (this.prover.isProofComplete() && !this.prover.isOnMainProof()) {
                startCompleteProofTask(300);
            }
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
            resetProofState(-1);
        }
    }

    private void completeSubproof() {
        try {
            this.prover.completeProof();
        } catch (ProverException e) {
            LOGGER.severe(e.getMessage());
            return;
        }

        // TODO: cleanup antecedentViews and consequentViews

        resetProofState(-1);

        if (this.prover.isProofComplete() && !this.prover.isOnMainProof()) {
            startCompleteProofTask(1_000);
        }
    }

    private void cancelSubproof() {
        int parentConsequentId = this.prover.getProofInfo().getParentConsequentId();

        try {
            this.prover.cancelProof();
        } catch (ProverException e) {
            LOGGER.severe(e.getMessage());
            return;
        }

        // TODO: cleanup antecedentViews and consequentViews

        resetProofState(parentConsequentId);
    }

    private void nextChallenge() {
        int newIndex = this.currentChallengeIndex + 1;
        if (newIndex == challenges.length) {
            newIndex = 0;
        }

        loadChallenge(newIndex);
        resetProofState(-1);
    }

    private void resetProofState(int selectedConsequentId) {
        this.selectedStatementId = -1;

        if (selectedConsequentId == -1) {
            ProofInfo proofInfo = this.prover.getProofInfo();
            boolean[] completedConsequents = proofInfo.getCompletedConsequents();

            for (int i = 0; i < completedConsequents.length; i++) {
                if (!completedConsequents[i]) {
                    selectedConsequentId = i;
                    break;
                }
            }
        }

        this.selectedConsequentId = selectedConsequentId;

        updateView();
    }

    private void startCompleteProofTask(int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                completeSubproof();
            }
        }, delay);
    }

    private static int computeMaxDepth(ProofInfo proofInfo) {
        int maxDepth = 0;
        while (proofInfo != null) {
            maxDepth++;
            proofInfo = proofInfo.getParentProof();
        }
        return maxDepth-1;
    }

    private static Spanned createFormulaText(Formula formula) {
        String string = formula.toString();
        string = replaceConnectiveSymbols(string);
        string = replaceFreeFormulas(string);
        return makeHtml(string);
    }

    private static String formatRuleName(String ruleName) {
        ruleName = replaceConnectiveSymbols(ruleName);
        ruleName = ruleName.replace("1", "<sub><small>1</small></sub>");
        ruleName = ruleName.replace("2", "<sub><small>2</small></sub>");
        return ruleName;
    }

    private static String replaceConnectiveSymbols(String string) {
        string = string.replace(UnaryConnective.Type.NEGATION.getSymbol(), NEGATION_SYMBOL);
        string = string.replace(BinaryConnective.Type.IMPLICATION.getSymbol(), IMPLICATION_SYMBOL);
        string = string.replace(BinaryConnective.Type.CONJUNCTION.getSymbol(), CONJUNCTION_SYMBOL);
        string = string.replace(BinaryConnective.Type.DISJUNCTION.getSymbol(), DISJUNCTION_SYMBOL);
        return string;
    }

    private static String replaceFreeFormulas(String string) {
        return string.replaceAll("\\"+ FreeFormula.STRING_PREFIX+"([0-9]+)",
				FREE_FORMULA_SYMBOL+"<sub><small>$1</small></sub>");
    }

    private static Spanned makeHtml(String string) {
        return HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }
}
