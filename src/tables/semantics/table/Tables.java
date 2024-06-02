package tables.semantics.table;

import tables.semantics.expr.Expr;
import tables.semantics.states.NoState;
import tables.semantics.states.SingleState;
import tables.semantics.states.State;
import tables.semantics.symbols.SemanticException;

import java.util.*;


public class Tables {
    static List<Set<Integer>> Ans = new ArrayList<>();
    static int n;

    public static Table toDea(Table nonDeterministic) throws SemanticException {
        List<Transition> transitions = nonDeterministic.getTransitions();
        List<Set<Integer>> newStates = subsets(transitions.stream().map(s -> s.from().getId()).toList());
        List<State> STATES = new ArrayList<>();
        for (int i = 0; i< newStates.size(); i++) {
            STATES.add(new SingleState(String.valueOf(i)));
        }
        int startId = findStateId(newStates,Set.of(nonDeterministic.getStart()));
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
            for(int exprId = 0; exprId < header.size(); exprId++) {
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

    private static List<Transition> simplify(List<Transition> transitions) {
        List<Transition> newTransitions = new ArrayList<>();
        Set<Integer> tos = new HashSet<>();
        for (Transition transition : transitions) {
            tos.addAll(transition.to().stream().map(State::getId).toList());
        }
        for(Transition transition : transitions) {
            State fromId = transition.from();
            if(tos.contains(fromId.getId()) || fromId.isStart()){
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
