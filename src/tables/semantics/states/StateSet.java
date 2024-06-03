package tables.semantics.states;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class StateSet implements State {

    private final Set<Integer> ids;
    private boolean isEnd;
    private boolean isStart;
    private String label;

    public StateSet(String image) {
        List<Integer> ids = Arrays.stream(image.split("\\|")).map(Integer::valueOf).toList();
        this.ids = new TreeSet<>(ids);
        this.isEnd = false;
        this.isStart = false;
    }

    @Override
    public boolean isStart() {
        return isStart;
    }

    @Override
    public boolean isEnd() {
        return isEnd;
    }

    @Override
    public Set<Integer> getIds() {
        return ids;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public boolean isNoState() {
        return false;
    }

    @Override
    public void setEndState(boolean endState) {
        this.isEnd = endState;
    }

    @Override
    public void setStartState(boolean startState) {
        this.isStart = startState;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }
    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return ids.toString();
    }

    public SingleState createNewSingleState() {
        String id = ids.stream().sorted((a, b) -> b - a).map(String::valueOf).collect(Collectors.joining());
        return new SingleState(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof State startState) {
            return startState.getIds().containsAll(getIds());
        }
        return false;
    }
}
