package tables.semantics.table;

import java.util.*;

import tables.semantics.expr.Expr;
import tables.semantics.states.State;
import tables.semantics.symbols.SemanticException;

public class Table {

	private final String id;
	private final List<Expr> header;
	private final List<Transition> transitions;

	private final int start;
	private final Set<Integer> ends;


	public Table(String id, List<Expr> header, List<Transition> transitions) throws SemanticException {
		this.id = id;
		checkRanges(header);
		this.header = header;

		checkStates(transitions);
		this.transitions = transitions;

		checkMarkups(transitions);
		this.start = getStart(transitions);
		this.ends = getEnds(transitions);
	}

	private void checkRanges(List<Expr> header) throws SemanticException {
		List<Set<Character>> contained = new ArrayList<>();
		for(Expr e : header) {
			Set<Character> chars = new TreeSet<>();
			for(int i = 0; i < 256; i++) {
				char c = (char)i;
				if(e.includes(c))
					chars.add(c);
			}
			contained.add(chars);
		}
		boolean error = false;
		for(int i = 0; i < contained.size(); i++) {
			Set<Character> cs1 = contained.get(i);
			for(int k = 0; k < contained.size(); k++) {
				Set<Character> cs2 = contained.get(k);

				if(i < k)
					for(Character c : cs1)
						if(cs2.contains(c)) {
							System.err.println(header.get(i) + " and " + header.get(k) + " have commonon elements");
							error = true;
							break;
						}
			}
		}
		if(error)
			throw new SemanticException("FSM " + id + " must not have common transition elements");
	}

	private void checkMarkups(List<Transition> transitions) throws SemanticException {
		Set<Integer> starts = new TreeSet<>();
		Set<Integer> ends = new TreeSet<>();

		for(Transition t : transitions) {
			if(t.from().isStart())
				starts.add(t.from().getId());
			if(t.from().isEnd())
				ends.add(t.from().getId());
		}
		boolean error = false;
		if(starts.isEmpty()) {
			System.err.println("No start defined for " + id);
			error = true;
		}
		if(starts.size() > 1) {
			System.err.println("Multiple starts defined for " + id);
			error = true;
		}


		if(ends.isEmpty()) {
			System.err.println("No ends defined for " + id);
			error = true;
		}

		if(error)
			throw new SemanticException("FSM " + id + " wrongly set starts and ends");
	}

	private void checkStates(List<Transition> transitions) throws SemanticException {
		Set<Integer> defined = new TreeSet<>();
		boolean error = false;
		for(Transition t : transitions) {
			int id = t.from().getId();
			if(defined.contains(id)) {
				System.err.println("FSM " + this.id + " defines " + id + " more than once");
				error = true;
			}else
				defined.add(id);
		}

		for(Transition t : transitions) {
			for(State s : t.to()) {
				Set<Integer> ids = s.getIds();
				for(Integer id : ids)
					if(!s.isNoState() && defined.stream().noneMatch(state -> Objects.equals(state, id))) {
						System.err.println("FSM " + this.id + " undefined " + id);
						error = true;
					}
			}
		}

		if(error)
			throw new SemanticException("FSM " + id + " wrongly defined ids");
	}

	private int getStart(List<Transition> transitions) {
		return transitions.stream()
				.filter(t -> t.from().isStart())
				.map(t -> t.from().getId())
				.findFirst().get();
	}

	private Set<Integer> getEnds(List<Transition> transitions) {
		List<Integer> ends = transitions.stream()
				.filter(t -> t.from().isEnd())
				.map(t -> t.from().getId())
				.toList();

		return new TreeSet<>(ends);
	}

	public List<Expr> getHeader() {
		return header;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public String getId() {
		return id;
	}

	public boolean isDeterministic() {
		for(Expr e : header)
			if(e.isEpsilon())
				return false;

		for(Transition e : transitions)
			for(State s : e.to())
				if(s.getIds().size() != 1)
					return false;

		return true;
	}

	public boolean isNonDeterministic() {
		return !isDeterministic();
	}

	public int getStart() {
		return start;
	}

	public boolean isEnd(int state) {
		return ends.contains(state);
	}
	public Set<Integer> getEnds(){
		return ends;
	}

	public boolean canConsume(char c) {
		for(Expr e : header) {
			System.out.println(e);
			if (e.includes(c))
				return true;
		}
		return  false;
	}

	public int next(int state, char c) {
		int pos = -1;
		for(int i = 0; i < header.size(); i++) {
			Expr e = header.get(i);
			if(e.includes(c)) {
				pos = i;
				break;
			}
		}

		if(pos == -1)
			return State.invalidState;

		for(Transition e : transitions)
			if(e.from().getId() == state)
				return e.to().get(pos).getId();

		return State.invalidState;
	}

	private String toString = null;

	@Override
	public String toString() {
		if(toString != null)
			return toString;

		Object[][] table = new String[transitions.size() + 1][header.size() + 1];

		{
			table[0][0] = id;
			int k = 1;
			for(Expr e : header)
				table[0][k++] = e.toShortString();
		}
		{
			int i = 1;
			for(Transition t : transitions) {
				table[i][0] = t.from().toString();
				int k = 1;
				for(State s : t.to())
					table[i][k++] = s.toString();
				i++;
			}
		}

		int max0 = 0;
        for (Object[] objects : table)
            if (max0 < objects[0].toString().length())
                max0 = objects[0].toString().length();

		int max = 0;
        for (Object[] objects : table)
            for (int k = 1; k < objects.length; k++)
                if (max < objects[k].toString().length())
                    max = objects[k].toString().length();
		max++;

		String fmt = "%" + max0 + "s |";
		for(int i = 1; i < table[0].length; i++)
			fmt = fmt + "%" + max + "s |";
		fmt = fmt + "%n";


		StringBuilder sb = new StringBuilder(String.format(fmt, table[0]));
        sb.append("-".repeat(max0));
		sb.append("-+");

		for(int i = 1; i < table[0].length; i++) {
            sb.append("-".repeat(Math.max(0, max)));
			sb.append("-+");
		}

		sb.append('\n');

		for(int i = 1; i < table.length; i++)
			sb.append(String.format(fmt, table[i]));

		toString = sb.toString();
		return toString;
	}

}
