package tables.semantics.table;

import tables.semantics.expr.Expr;
import tables.semantics.states.NoState;
import tables.semantics.states.SingleState;
import tables.semantics.states.State;
import tables.semantics.symbols.SemanticException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.InflaterInputStream;


public class Tables {
    static List<Set<Integer>> Ans = new ArrayList<>();
    static int n;

    public static Table toDea(Table nonDeterministic) throws SemanticException {
        List<Transition> transitions = nonDeterministic.getTransitions();
        List<Set<Integer>> newStates = subsets(transitions.stream().map(s -> s.from().getId()).toList());
        List<State> STATES = new ArrayList<>();
        for (int i = 0; i < newStates.size(); i++) {
            STATES.add(new SingleState(String.valueOf(i)));
        }
        int startId = findStateId(newStates, Set.of(nonDeterministic.getStart()));
        STATES.get(startId).setStartState(true);

        int endId = findStateId(newStates, nonDeterministic.getEnds());
        STATES.get(endId).setEndState(true);
        STATES.set(STATES.size() - 1, NoState.getNoState());
        List<Expr> header = List.copyOf(nonDeterministic.getHeader());
        List<Transition> newTransitions = new ArrayList<>();
        for (int id = 0; id < STATES.size() - 1; id++) {
            Transition transition = new Transition(STATES.get(id));
            List<State> newTo = new ArrayList<>();
            Set<Integer> singleStateSet = newStates.get(id);
            for (int exprId = 0; exprId < header.size(); exprId++) {
                Set<Integer> ids = new HashSet<>();
                for (Integer singleState : singleStateSet) {
                    Transition currentTransition = transitions.get(singleState);
                    State toState = currentTransition.to().get(exprId);
                    ids.addAll(toState.getIds());
                }
                int toStateId = findStateId(newStates, ids);
                State newToState = STATES.get(toStateId);
                newTo.add(newToState);
            }
            transition.setTo(newTo);
            newTransitions.add(transition);
        }
        return new Table("nonDeterministic" + nonDeterministic.getId(), header, simplify(newTransitions));
    }

    static HashMap<HashSet<Integer>, State> STATES;
    static LinkedList<HashSet<Integer>> statesToDo;
    public static Table toDea2(Table nonDeterministic) throws SemanticException {
        STATES = new HashMap<>();
        statesToDo = new LinkedList<>();
        HashSet<Integer> start = getState(nonDeterministic, nonDeterministic.getStart());
        statesToDo.add(start);
        SingleState startState = new SingleState(String.valueOf(STATES.size()));
        startState.setStartState(true);
        startState.setLabel(start.toString());
        STATES.put(start,startState);
        List<Transition> transitions = new ArrayList<>();
        while (!statesToDo.isEmpty()){
            HashSet<Integer> newState = statesToDo.poll();
            Transition transition = getTransition(nonDeterministic, newState);
            transitions.add(transition);
        }
        for(Transition transition : transitions){
            System.out.println(transition.from + " | " + transition.to);
        }
        for(int i : nonDeterministic.getEnds()) {
            for (HashSet<Integer> set : STATES.keySet()){
                if(set.contains(i)){
                    STATES.get(set).setEndState(true);
                }
            }
        }
        System.out.println(STATES);
        List<Expr> header = new ArrayList<>(List.copyOf(nonDeterministic.getHeader()));
        header.removeLast();

        return new Table(nonDeterministic.getId() + "non",header ,transitions);
    }

    private static Transition getTransition(Table nonDeterministic, HashSet<Integer> newState) {
        State from = STATES.get(newState);
        List<State> to = new ArrayList<>();
        List<Expr> header = nonDeterministic.getHeader();
        for (int i = 0; i < header.size(); i++) {
            if (header.get(i).isEpsilon()) continue;
            HashSet<Integer> toSingleState = new HashSet<>();
            for (int state : newState) {
                nonDeterministic.getTransitions().get(state).to.get(i).getIds().forEach(a -> toSingleState.addAll(getState(nonDeterministic, a)));
            }
            State state;
            if(toSingleState.isEmpty()){
                state = NoState.getNoState();
            }else {
                state = new SingleState(String.valueOf(STATES.size()));
            }
            state.setLabel(toSingleState.toString());
            State newAddedState = STATES.putIfAbsent(toSingleState, state);
            if(newAddedState == null){
                statesToDo.add(toSingleState);
            }
            to.add(STATES.get(toSingleState));
        }
        return new Transition(from, to);
    }

    private static HashSet<Integer> getState(Table nonDeterministic, int stateId) {
        List<Expr> header = nonDeterministic.getHeader();
        HashSet<Integer> s = new HashSet<>();
        s.add(stateId);
        for (int i = 0; i < header.size(); i++) {
            if (!header.get(i).isEpsilon()) continue;
            State state = nonDeterministic.getTransitions().get(stateId).from;
            nonDeterministic.getTransitions().get(state.getId()).to.get(i).getIds().stream()
                    .filter(a -> !s.contains(a))
                    .forEach(v -> {
                        System.out.println("Adding: " + v + " to " + s);
                        s.addAll(getState(nonDeterministic, v));
                    });
        }
        return s;
    }

    private static List<Transition> simplify(List<Transition> transitions) {
        List<Transition> newTransitions = new ArrayList<>();
        Set<Integer> tos = new HashSet<>();
        for (Transition transition : transitions) {
            tos.addAll(transition.to().stream().map(State::getId).toList());
        }
        for (Transition transition : transitions) {
            State fromId = transition.from();
            if (tos.contains(fromId.getId()) || fromId.isStart()) {
                newTransitions.add(transition);
            }
        }
        return newTransitions;
    }

    private static int findStateId(List<Set<Integer>> newStates, Set<Integer> destinationsForExpression) {
        int i = 0;
        for (Set<Integer> stateSet : newStates) {
            if (stateSet.equals(destinationsForExpression)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static List<Set<Integer>> subsets(List<Integer> nums) {
        List<Integer> op = new ArrayList<>();
        n = nums.size();

        // call the helper function
        Helper(op, nums, 0);

        return Ans;
    }

    public static void Helper(List<Integer> op, List<Integer> nums, int startIndex) {
        // base case
        if (startIndex == n) {
            Ans.add(new TreeSet<>(op));
            return;
        }

        // recursive case
        // choice 1: include the current element
        op.add(nums.get(startIndex));
        Helper(op, nums, startIndex + 1);

        // backtracking step
        op.removeLast();

        // choice 2: exclude the current element
        Helper(op, nums, startIndex + 1);
    }

}
