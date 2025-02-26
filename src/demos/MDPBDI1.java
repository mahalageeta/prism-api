package demos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sound.midi.SysexMessage;

import demos.MDPModelGenerator.GridModel;
import parser.State;
import parser.VarList;
import parser.ast.Declaration;
import parser.ast.DeclarationBool;
import parser.ast.DeclarationInt;
import parser.ast.DeclarationType;
import parser.ast.Expression;
import parser.type.Type;
import parser.type.TypeBool;
import parser.type.TypeInt;
import prism.DefaultModelGenerator;
import prism.ModelGenerator;
import prism.ModelType;
import prism.Prism;
import prism.PrismDevNullLog;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLangException;
import prism.PrismLog;

public class MDPBDI1 {
    public static HashMap<Integer, ArrayList<String>> AgenttransitionValueG = new HashMap<Integer, ArrayList<String>>();
    public static HashMap<String, ArrayList<Double>> SelectionTransitionProb = new HashMap<String, ArrayList<Double>>();
    public static HashMap<String, ArrayList<Double>> transitionProbG = new HashMap<String, ArrayList<Double>>();
    public static Map<String, Set<String>> variablesG = new HashMap();
    public static ArrayList<String> AgentsNameG = new ArrayList<String>();
    public static HashMap<Integer, ArrayList<String>> AgentsActionsG = new HashMap<Integer, ArrayList<String>>();
    public static HashMap<String, ArrayList<String>> transitionValueG = new HashMap<String, ArrayList<String>>();
    static int numberofAgents = 0;
    public static ArrayList<String> variblesextractedG = new ArrayList<String>();
    public static ArrayList<String> sequenceOfActions = new ArrayList<>();

    // public static int actionTrack

    public static void main(String[] args) throws IOException {

        File directoryPath = new File("src\\demos\\Data\\AgentTranProb");
        File filesList[] = directoryPath.listFiles();
        Integer numberofIntention = filesList.length;
        System.out.println("Number of Intnetion " + numberofIntention);
        for (int f = 0; f < filesList.length; f++) {
            Map<String, Set<String>> variablesSet = new HashMap<>();
            ArrayList<String> AgentsName = new ArrayList<String>();
            Map<String, ArrayList<String>> agentActions = new HashMap<>();
            HashMap<Integer, ArrayList<String>> AgentsActions = new HashMap<Integer, ArrayList<String>>();
            HashMap<String, ArrayList<String>> transitionValue = new HashMap<String, ArrayList<String>>();
            HashMap<String, ArrayList<Double>> transitionProb = new HashMap<String, ArrayList<Double>>();
            HashMap<Integer, ArrayList<String>> AgenttransitionValue = new HashMap<Integer, ArrayList<String>>();
            Map<String, Set<String>> variables = new HashMap<>();
            HashMap<String, Integer> ActionOutcomes = new HashMap<String, Integer>();

            /****
             * For each intnetion id we need to build the MDP
             */
            // System.out.println("Now for each intnetion id i need to build the mdp");
            System.out.println("************* Intention id ************ " + filesList[f].getName());
            String currrentInten = filesList[f].getName();
            // System.out.println("Read the actions for Intnetion id = " +
            // filesList[f].getName());
            ReadIntention ri = new ReadIntention();
            agentActions = ri.getAgentActions(currrentInten);
            // System.out.println("Agents Action " + agentActions);
            variablesSet = ri.getvaribles(currrentInten);
            // System.out.println(" Varibles " + variablesSet);
            variables.putAll(variablesSet);
            AgentsName.addAll(agentActions.keySet());
            // System.out.println(" AgentsName " + AgentsName);
            numberofAgents = AgentsName.size();

            Transitions at = new Transitions();
            ActionOutcomes = at.getActionsTransitions(currrentInten);
            // System.out.println("Action Number of consequences " + ActionOutcomes);

            // ActionOutcome acv = new ActionOutcome();
            // transitionValues = acv.getExecutionValuesNew(currrentInten, transitions);
            // System.out.println(" Value In each Transitions " + transitionValues);

            // ActionOutcome acp = new ActionOutcome();
            // transitionProb = acp.getExecutionProb(currrentInten, transitions);
            // System.out.println(" Prob In each Transitions " + transitionProb);

            // if (!variables.isEmpty()) {
            // variables.clear();
            // }

            // variables.addAll(variablesSet);

            // System.out.println("Varibles " + variables);

            List<String> agents = new ArrayList<>(agentActions.keySet());

            for (int i = 0; i < agents.size(); i++) {
                AgentsActions.put(i + 1, agentActions.get(agents.get(i)));
            }

            for (int a = 0; a < AgentsActions.size(); a++) {
                ArrayList<String> Agentmechvalue = new ArrayList<String>();
                for (int m = 0; m < AgentsActions.get(a + 1).size(); m++) {
                    String action = AgentsActions.get(a + 1).get(m);
                    Path path = Paths.get("src\\demos\\Data\\AgentTranProb\\" + currrentInten + "\\" + action);
                    try {
                        ArrayList<String> totalTransitions = (ArrayList<String>) Files.readAllLines(path,
                                StandardCharsets.UTF_8);
                        ArrayList<Double> mechprob = new ArrayList<Double>();
                        ArrayList<String> mechvalue = new ArrayList<String>();
                        for (int t = 0; t < totalTransitions.size(); t++) {
                            String tranValue = totalTransitions.get(t);
                            String[] eachValue = tranValue.split(":", 2);
                            String Values = eachValue[0].trim();
                            String prob = eachValue[1].trim();
                            mechvalue.add(Values);
                            Agentmechvalue.add(Values);
                            mechprob.add(Double.parseDouble(prob));
                        }
                        transitionValue.put(action, mechvalue);
                        transitionProb.put(action, mechprob);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                AgenttransitionValue.put(a + 1, Agentmechvalue);
            }

            System.out.println("numberofAgents" + numberofAgents);
            System.out.println("Agents =" + agents);
            // System.out.println("AgentActions mapping " + AgentsActions);
            System.out.println("transitionValue " + transitionValue);
            // System.out.println("transitionVProb " + transitionProb);
            // System.out.println("Agent transitionValue " + AgenttransitionValue);
            // variables.add(0, "agent");
            // System.out.println("variables " + variables);

            if (!variablesG.isEmpty()) {
                variablesG.clear();
            }

            variablesG.putAll(variables);

            if (!AgentsNameG.isEmpty()) {
                AgentsNameG.clear();
            }

            AgentsNameG.addAll(AgentsName);

            if (!AgentsActionsG.isEmpty()) {
                AgentsActionsG.clear();
            }

            AgentsActionsG.putAll(AgentsActions);

            if (!transitionProbG.isEmpty()) {
                transitionProbG.clear();
            }

            transitionProbG.putAll(transitionProb);

            if (!AgenttransitionValueG.isEmpty()) {
                AgenttransitionValueG.clear();
            }

            AgenttransitionValueG.putAll(AgenttransitionValue);

            if (!transitionValueG.isEmpty()) {
                transitionValueG.clear();
            }

            List<String> extractedValues = new ArrayList<>();
            for (Set<String> values : variablesG.values()) {
                extractedValues.addAll(values);
            }
            List<String> result = extractedValues.stream()
                    .map(s -> Arrays.asList(s.split(",")))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            if (!variblesextractedG.isEmpty()) {
                variblesextractedG.clear();
            }
            variblesextractedG.addAll(result);
            // we need to have an extra varible which monior sequence actions to achove a
            // higher
            // level goal.
            // variblesextractedG.add(0, "AS");

            transitionValueG.putAll(transitionValue);

            // Extract sequential order of actions

            if (!sequenceOfActions.isEmpty()) {
                sequenceOfActions.clear();
            }

            for (ArrayList<String> actions : AgentsActionsG.values()) {
                sequenceOfActions.addAll(actions);
            }

            // // System.out.println("numberofAgents = " + numberofAgents);
            // System.out.println("AgentsNameG " + AgentsNameG);
            // System.out.println("AgentsActionsG " + AgentsActionsG);
            // System.out.println("variablesG " + variablesG);
            // System.out.println("variblesextractedG " + variblesextractedG);
            // System.out.println("transitionValueG " + transitionValueG);
            // System.out.println("transitionProbG " + transitionProbG);
            // System.out.println("AgenttransitionValueG " + AgenttransitionValueG);

            Instant start = Instant.now();
            new MDPBDI1().run();

            Instant end = Instant.now();
            Duration timeElapsed = Duration.between(start, end);
            System.out.println("Time taken: " + timeElapsed.toMillis() + "milliseconds");

            System.out.println("=================  END  ====================");

        }

    }

    public void run() throws FileNotFoundException {

        try {
            // Create a log for PRISM output (hidden or stdout)
            PrismLog mainLog = new PrismFileLog("stdout");
            // Initialise PRISM engine
            Prism prism = new Prism(mainLog);
            prism.initialise();
            prism.setEngine(Prism.EXPLICIT);
            // Create a model generator to specify the model that PRISM should build
            MDPModel modelGen = new MDPModel(sequenceOfActions.size());
            // Load the model generator into PRISM,
            // export the model to a dot file (which triggers its construction)
            prism.loadModelGenerator(modelGen);
            prism.exportTransToFile(true, Prism.EXPORT_DOT_STATES, new File("mdp.dot"));
            prism.exportTransToFile(true, Prism.EXPORT_PLAIN, new File("ExStates.tra"));
            // prism.exportTransToFile(true,Prism.EXPORT_ROWS, new
            // File("ExStateTransitionRowWise.tra"));
            prism.exportStatesToFile(Prism.EXPORT_PLAIN, new File("ExStates.sta"));
            prism.exportLabelsToFile(null, Prism.EXPORT_PLAIN, new File("ExStates.lab"));
            // Then do some model checking and print the result
            String[] props = new String[] {
                    "Pmax=?[F \"p1\"]"

            };
            for (String prop : props) {
                System.out.println(prop + ":");
                System.out.println(prism.modelCheck(prop).getResult());
            }

            // Close down PRISM
            prism.closeDown();

        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (PrismException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    class MDPModel implements ModelGenerator {
        private State exploreState;
        private int maxactions;
        HashMap<String, Object> valueOfVariable = new HashMap<String, Object>();
        private Map<State, Integer> stateToActionIndexMap = new HashMap<>();
        // Track the action index for each state

        public MDPModel(int maxactions) {
            this.maxactions = maxactions;
        }

        @Override
        public ModelType getModelType() {
            System.out.println("maxactions " + maxactions);
            System.out.println("getModelType = " + ModelType.MDP);
            return ModelType.MDP;
        }

        @Override
        public List<String> getVarNames() {
            System.out.println("getVarNames" + variblesextractedG);
            // System.out.println("Total Number of varibles =" + variblesextractedG.size());

            return variblesextractedG;

        }

        @Override
        public List<Type> getVarTypes() {
            List<Type> resultList = new ArrayList<>();

            // resultList.add(TypeInt.getInstance());// monitor the sequence of actions

            for (int i = 0; i < variblesextractedG.size(); i++) {
                resultList.add(TypeBool.getInstance());
            }
            System.out.println("getVarTypes = " + resultList);
            return resultList;
        }

        @Override
        public State getInitialState() throws PrismException {
            System.out.println(" getInitialState ");
            State initialState = new State(variblesextractedG.size());
            // initialState = initialState.setValue(0, 1);
            for (int i = 0; i < variblesextractedG.size(); i++) {
                initialState = initialState.setValue(i, false);
            }

            System.out.println("Initial State " + initialState);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println();
            return initialState;
        }

        @Override
        public DeclarationType getVarDeclarationType(int i) throws PrismException {
            Type type = getVarType(i);
            System.out.println("getVarDeclarationType value of i = " + i);

            return new DeclarationBool();

        }

        // There is just one label: "goal"

        @Override
        public List<String> getLabelNames() {
            System.out.println("getLabelNames");
            return Arrays.asList("p1");
            // return Arrays.asList("achivement", "maintain");
        }

        @Override
        public void exploreState(State exploreState) throws PrismException {
            // Store the state (for reference, and because will clone/copy it later)
            System.out.println(".................EXPLORE.........................." + "\n");
            this.exploreState = exploreState;
            int currentActionIndex = stateToActionIndexMap.getOrDefault(exploreState, 0);
            System.out.println("currentActionIndex " + currentActionIndex);
            System.out.println("stateToActionIndexMap " + stateToActionIndexMap);
            stateToActionIndexMap.put(exploreState, currentActionIndex + 1);
            System.out.println("After Update stateToActionIndexMap " + stateToActionIndexMap);
            System.out.println("exploreState " + exploreState);
            for (int s = 0; s < variblesextractedG.size(); s++) {
                valueOfVariable.put(variblesextractedG.get(s),
                        ((Boolean) exploreState.varValues[s]).booleanValue());
            }
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>" + "\n");

        }

        @Override
        public int getNumChoices() throws PrismException {
            System.out.println("..........................................." + "\n");
            System.out.println("getNumChoices ");
            // From sequence of actions like a1,a2 then at a time a1 is alivalble
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
            State currentState = exploreState;
            int currentActionIndex = stateToActionIndexMap.getOrDefault(currentState, 0);
            int remainingActions = sequenceOfActions.size() - currentActionIndex;
            if (remainingActions > 0) {
                System.out.println("Remaining actions: " + remainingActions);
                return 1; // Only one choice available at a time
            } else {
                System.out.println("No more actions to execute.");
                return 0; // No choices available, execution is complete
            }

        }

        @Override
        public int getNumTransitions(int i) throws PrismException {
            System.out.println("..........................................." + "\n");
            System.out.println("getNumTransitions  i = " + i);
            State currentState = exploreState;
            int currentActionIndex = stateToActionIndexMap.getOrDefault(currentState, 0);
            int remainingActions = sequenceOfActions.size() - currentActionIndex;
            ArrayList<Double> SelectionExecutionprob = new ArrayList<Double>();
            int transitions = 0;
            // int remainingActions = sequenceOfActions.size() - (executedActions - 1);
            if (remainingActions > 0) {
                String action = sequenceOfActions.get(currentActionIndex - 1);
                System.out.println("action " + action);
                transitions = transitionProbG.get(action).size();
                for (int t = 0; t < transitionProbG.get(action).size(); t++) {
                    double prob = transitionProbG.get(action).get(t);
                    prob = Double.parseDouble(new DecimalFormat("##.###").format(prob));
                    SelectionExecutionprob.add(prob);
                }

                SelectionTransitionProb.put(action, SelectionExecutionprob);
                System.out.println("Number of trasnsitions for action " + action + " = " + transitions);
                // System.out.println("SelectionTransitionProb = " + SelectionTransitionProb);
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
                return transitions;

            } else {
                // No more actions to execute
                System.out.println("No more actions to execute");
                return 0;
            }

        }

        @Override
        /**
         * Get the action label of a transition within a choice, specified by its
         * index/offset.
         * The label can be any Object, but will often be treated as a string, so it
         * should at least
         * have a meaningful toString() method implemented. Absence of an action label
         * is denoted by null.
         * Note: For most types of models, the action label will be the same for all
         * transitions within
         * the same nondeterministic choice (i.e. for each different value of
         * {@code offset}),
         * but for Markov chains this may not necessarily be the case.
         * 
         * @param i      Index of the nondeterministic choice
         * @param offset Index of the transition within the choice
         */

        public Object getTransitionAction(int i, int offset) throws PrismException {
            System.out.println("..................................... " + "\n");
            System.out.println("getTransitionAction i= " + i);
            State currentState = exploreState;
            int currentActionIndex = stateToActionIndexMap.getOrDefault(currentState, 0);
            int remainingActions = sequenceOfActions.size() - currentActionIndex;
            String action = sequenceOfActions.get(currentActionIndex - 1);
            System.out.println("Transit Action " + action);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
            return action;

        }

        @Override
        public Double getTransitionProbability(int i, int offset) throws PrismException {
            System.out.println(".........................................." + "\n");
            System.out.println("getTransitionProbability i= " + i);
            System.out.println("getTransitionProbability offset= " + offset);
            State currentState = exploreState;
            int currentActionIndex = stateToActionIndexMap.getOrDefault(currentState, 0);
            int remainingActions = sequenceOfActions.size() - currentActionIndex;

            String action = sequenceOfActions.get(currentActionIndex - 1);
            double prob = SelectionTransitionProb.get(action).get(offset);
            System.out.println("Transit Probability = " + prob);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
            return prob;

        }

        @Override
        /**
         * Get the target (as a new State object) of a transition within a choice,
         * specified by its index/offset.
         * 
         * @param i      Index of the nondeterministic choice
         * @param offset Index of the transition within the choice
         */
        public State computeTransitionTarget(int i, int offset) throws PrismException {
            System.out.println(".........................................." + "\n");
            System.out.println("computeTransitionTarget i= " + i);
            System.out.println("computeTransitionTarget offset= " + offset);
            System.out.println("valueOfVariable " + valueOfVariable);
            State target = new State(exploreState);
            List<String> extractedValues = new ArrayList<>();
            for (Set<String> values : variablesG.values()) {
                extractedValues.addAll(values);
            }
            State currentState = exploreState;
            int currentActionIndex = stateToActionIndexMap.getOrDefault(currentState, 0);
            int remainingActions = sequenceOfActions.size() - currentActionIndex;
            String action = sequenceOfActions.get(currentActionIndex - 1);
            String tranValue = transitionValueG.get(action).get(offset);
            List<String> var = variablesG.get(action).stream()
                    .map(s -> Arrays.asList(s.split(",")))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            String[] varValue = tranValue.split(",", (extractedValues.size()));
            String currentTarget = target.toString().replaceAll("\\(",
                    "").replaceAll("\\)", "");
            System.out.println("current state = " + currentTarget);
            String[] parts = currentTarget.split(",");
            for (int v = 0; v < var.size(); v++) {
                Boolean newTotalValue = Boolean.parseBoolean(varValue[v]);
                target.setValue(variblesextractedG.indexOf(var.get(v)), newTotalValue);

            }
            // target.setValue(0, actionSeqNo + 1);

            System.out.println("new state= " + target);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
            return target;

        }

        @Override
        public boolean isLabelTrue(int i) throws PrismException {

            System.out.println("..............................");
            System.out.println("isLabelTrue i " + i);

            switch (i) {
                case 0:
                    // "target" (top-right corner)
                    return "p1".equals("true") && "p2".equals("true");
                default:
                    throw new PrismException("Label number \"" + i + "\" not defined");
            }

        }

    }
}
