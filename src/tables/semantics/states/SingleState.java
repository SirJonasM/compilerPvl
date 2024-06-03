package tables.semantics.states;


import java.util.Set;


public class SingleState implements State {
	private final int id;
	boolean isStart;
	String label;
	boolean isEnd;
	public SingleState(String image) {
		this.id = Integer.parseInt(image);
		this.isStart = false;
		this.isEnd = false;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public boolean isNoState() {
		return false;
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
		return Set.of(id);
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof State startState) {
			return startState.getIds().containsAll(getIds());
		}
		return false;
	}
	@Override
	public void setStartState(boolean isStart){
		this.isStart = isStart;
	}
	public void setEndState(boolean isEnd){
		this.isEnd = isEnd;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}
	@Override
	public String getLabel() {
		return label;
	}
}
