package tables.semantics.states;

import java.util.Set;

public interface State  {
    int invalidState = -1;
    boolean isStart();
    boolean isEnd();
    Set<Integer> getIds();
    int getId();
    boolean isNoState();
    void setEndState(boolean endState);
    void setStartState(boolean startState);
}
