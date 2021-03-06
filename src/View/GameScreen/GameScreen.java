package View.GameScreen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import Controller.PlayerAction;
import Model.Dice.Dice;
import Model.Dice.DiceCollection;
import Model.Dice.DiceCombination;
import Model.Player.YatzyPlayer;
import View.YatzyScreen;

/**
 * GameScreen is a view panel showing the game board, including players, scores and dice.
 * Possible ActionCommands listeners may recieve include:
 *     Roll:        When the roll button is pressed
 *     Dice:        When a dice button is pressed
 *     Combination: When a combination button is pressed
 *     Mode:        When a player action mode has changed
 *     Change:      When the panel has changed
 * @author Isak
 */
public class GameScreen extends YatzyScreen {
    private ArrayList<DiceButton> diceButtons;
    private HashMap<PlayerAction, JRadioButton> playerActionRadioButtons;
    private HashMap<DiceCombination, CombinationButton> combinationButtons;
    private HashMap<YatzyPlayer, PlayerPanel> playerPanels;
    private JButton rollButton;
    private JPanel dicePanel;
    private JPanel combinationPanel;
    private JPanel playerPanel;

    /**
     * Gets the roll button
     * @return the roll button
     */
    public JButton getRollButton() {
        return this.rollButton;
    }

    /**
     * Gets the combination buttons in a HashMap with their corresponding DiceCombination
     * as keys.
     * @return the combination buttons
     */
    public HashMap<DiceCombination, CombinationButton> getCombinationButtons() {
        return this.combinationButtons;
    }

    /**
     * Gets the player panels in a HashMap with their corresponding YatzyPlayer as keys
     * @return the player panels
     */
    public HashMap<YatzyPlayer, PlayerPanel> getPlayerPanels() {
        return this.playerPanels;
    }

    /**
     * Gets the dice buttons
     * @return the dice buttons
     */
    public ArrayList<DiceButton> getDiceButtons() {
        return this.diceButtons;
    }

    /**
     * Gets the action associated with the selected radiobutton
     * @return the associated action
     */
    public PlayerAction getSelectedAction() {
        for (PlayerAction action : this.playerActionRadioButtons.keySet()) {
            JRadioButton radioButton = this.playerActionRadioButtons.get(action);

            if (radioButton.isSelected()) {
                return action;
            }
        }

        return null;
    }

    /**
     * Sets what radiobutton associated with a specific action to be selected.
     * @param action the associated action
     */
    public void setSelectedAction(PlayerAction action) {
        for (PlayerAction playerAction : this.playerActionRadioButtons.keySet()) {
            JRadioButton radioButton = this.playerActionRadioButtons.get(playerAction);
            radioButton.setSelected(playerAction == action);
        }
    }

    /**
     * Sets what dice to use to create the DiceButtons. Clears the view and recreates all DiceButtons.
     * @param dice the dice to use as model for the DiceButtons of the view
     */
    public void setDice(DiceCollection dice) {
        this.dicePanel.removeAll();
        this.diceButtons.clear();

        for (Dice d : dice) {
            DiceButton diceButton = new DiceButton(d);
            diceButton.addActionListener(this::fireActionPerformed);
            this.dicePanel.add(diceButton);
            this.diceButtons.add(diceButton);
        }

        this.fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Change"));
    }

    /**
     * Sets what players to use to create the playerPanels. Clears the view and recreates all playerPanels.
     * @param players the players to use as a model for the playerPanels of the view
     */
    public void setPlayers(ArrayList<YatzyPlayer> players) {
        this.playerPanel.removeAll();
        this.playerPanels.clear();

        for (YatzyPlayer player : players) {
            PlayerPanel panel = new PlayerPanel(player);
            this.playerPanel.add(panel);
            this.playerPanels.put(player, panel);
        }

        this.fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Change"));
    }

    /**
     * Sets what combinations to use to create the CombinationButtons. Clears the view and recreates
     * all CombinationButtons.
     * @param combinations the combinations to use as a model for the CombinationButtons of the view
     */
    public void setCombinations(DiceCombination[] combinations) {
        this.combinationPanel.removeAll();
        this.combinationButtons.clear();

        for (DiceCombination combination : DiceCombination.values()) {
            CombinationButton combinationButton = new CombinationButton(combination);
            combinationButton.addActionListener(this::fireActionPerformed);
            this.combinationPanel.add(combinationButton);
            this.combinationButtons.put(combination, combinationButton);

            if (combination == DiceCombination.SIXES) {
                // add bonus and upper total after sixes
                JLabel bonusLabel = new JLabel("Bonus");
                Dimension size = bonusLabel.getPreferredSize();
                size.height = 25;
                bonusLabel.setPreferredSize(size);
                this.combinationPanel.add(bonusLabel);

                JLabel upperTotalLabel = new JLabel("Total");
                upperTotalLabel.setPreferredSize(size);
                this.combinationPanel.add(upperTotalLabel);
            }
        }

        // add total last
        JLabel totalLabel = new JLabel("Total");
        Dimension size = totalLabel.getPreferredSize();
        size.height = 25;
        totalLabel.setPreferredSize(size);
        this.combinationPanel.add(totalLabel);

        this.fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Change"));
    }

    /**
     * Resets the GUI components of this panel
     */
    @Override
    public void reset() {
        super.reset();

        this.removeAll();
        this.initDefaultGUI();
        this.diceButtons.clear();
        this.playerPanels.clear();

        this.fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Change"));
    }

    /**
     * Initiates the GUI components of this panel
     */
    @Override
    protected void initDefaultGUI() {
        this.diceButtons = new ArrayList<DiceButton>();
        this.playerActionRadioButtons = new HashMap<PlayerAction, JRadioButton>();
        this.combinationButtons = new HashMap<DiceCombination, CombinationButton>();
        this.playerPanels = new HashMap<YatzyPlayer, PlayerPanel>();

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        this.add(wrapper, BorderLayout.CENTER);

        wrapper.add(this.createCombinationPanel(), BorderLayout.WEST);
        wrapper.add(this.createTablePanel(), BorderLayout.CENTER);
        wrapper.add(this.createDicePanel(), BorderLayout.EAST);

        this.setCombinations(DiceCombination.values());
    }

    /**
     * Creates the left panel of the view, containing the combinationButtons and player action modes
     * @return a new JPanel
     */
    private JPanel createCombinationPanel() {
        JPanel combinationPanelWrapper = new JPanel();
        combinationPanelWrapper.setLayout(new BorderLayout());

        JPanel combinationButtonPanel = new JPanel();
        combinationButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 4));
        combinationPanelWrapper.add(combinationButtonPanel, BorderLayout.SOUTH);

        this.combinationPanel = new JPanel();
        this.combinationPanel.setLayout(new GridLayout(0, 1, 0, 2));
        combinationButtonPanel.add(this.combinationPanel);

        JPanel actionPanelWrapper = new JPanel();
        actionPanelWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
        combinationPanelWrapper.add(actionPanelWrapper, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(0, 1, 0, 0));
        actionPanelWrapper.add(actionPanel);

        ButtonGroup group = new ButtonGroup();

        for (PlayerAction action : PlayerAction.values()) {
            JRadioButton radioButton = new JRadioButton(action.toString());
            radioButton.setActionCommand("Mode");
            radioButton.addActionListener(this::fireActionPerformed);
            group.add(radioButton);
            actionPanel.add(radioButton);
            this.playerActionRadioButtons.put(action, radioButton);
        }

        this.playerActionRadioButtons.get(PlayerAction.SCORE).setSelected(true);

        return combinationPanelWrapper;
    }

    /**
     * Creates the center panel of the view, containing the playerPanels
     * @return a new JPanel
     */
    private JPanel createTablePanel() {
        JPanel tablePanelWrapper = new JPanel();
        tablePanelWrapper.setLayout(new BorderLayout());

        this.playerPanel = new JPanel();
        this.playerPanel.setLayout(new GridLayout(1, 0, 1, 0));
        this.playerPanel.setBorder(new CompoundBorder(
                new SoftBevelBorder(BevelBorder.LOWERED),
                new EmptyBorder(0, 5, 0, 5)
        ));
        tablePanelWrapper.add(this.playerPanel, BorderLayout.CENTER);

        return tablePanelWrapper;
    }

    /**
     * Creates the right panel of the view, containing the diceButtons
     * @return a new JPanel
     */
    private JPanel createDicePanel() {
        JPanel dicePanelWrapper = new JPanel();
        dicePanelWrapper.setLayout(new BorderLayout());

        JPanel diceTopPanel = new JPanel();
        diceTopPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        dicePanelWrapper.add(diceTopPanel, BorderLayout.NORTH);

        this.dicePanel = new JPanel();
        this.dicePanel.setLayout(new GridLayout(0, 1, 0, 5));
        diceTopPanel.add(this.dicePanel);

        JPanel diceBottomPanel = new JPanel();
        diceBottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        dicePanelWrapper.add(diceBottomPanel, BorderLayout.SOUTH);

        this.rollButton = new JButton("Roll dice");
        this.rollButton.setActionCommand("Roll");
        this.rollButton.addActionListener(this::fireActionPerformed);
        diceBottomPanel.add(this.rollButton);

        return dicePanelWrapper;
    }
}
