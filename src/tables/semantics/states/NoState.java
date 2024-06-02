package tables.semantics.states;

import java.util.Set;

public class NoState implements State {
	int id;
	private static NoState instance;
	private NoState(){
		this.id = -1;
	}

	public static State getNoState() {
		if (instance == null) {
			instance = new NoState();
		}
		return instance;
	}

	@Override
	public String toString() {
		return "-";
	}

	@Override
	public boolean isStart() {
		return false;
	}

	@Override
	public boolean isEnd() {
		return false;
	}

	@Override
	public Set<Integer> getIds() {
		return Set.of();
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public boolean isNoState() {
		return true;
	}

	@Override
	public void setEndState(boolean endState) {

	}

	@Override
	public void setStartState(boolean startState) {

	}
}
