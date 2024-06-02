package tables.semantics.table;

import java.util.List;

import tables.semantics.states.State;

public class Transition{
    State from;
    List<State> to;
    public Transition(State from) {
        this.from = from;
    }
    public Transition(State from, List<State> to) {
        this.from = from;
        this.to = to;
    }
    public State from() {
        return from;
    }
    public void setFrom(State from) {
        this.from = from;
    }
    public List<State> to() {
        return to;
    }
    public void setTo(List<State> to) {
        this.to = to;
    }
}

