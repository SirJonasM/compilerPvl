package tables.parser;

import static tables.scanner.TokenType.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import tables.scanner.Scanner;
import tables.scanner.Token;
import tables.scanner.TokenType;
import tables.semantics.expr.Char;
import tables.semantics.expr.Constant;
import tables.semantics.expr.Expr;
import tables.semantics.expr.Range;
import tables.semantics.states.*;
import tables.semantics.symbols.SemanticException;
import tables.semantics.symbols.Symbols;
import tables.semantics.table.Table;
import tables.semantics.table.Transition;

public class Parser {
	
	private final Scanner scanner;
	private final Symbols symbols;
	private final Set<TokenType> stateSet;
	
	private Token current;
	
	private Parser(Scanner scanner) {
		this.scanner = scanner;
		this.symbols = new Symbols();
		this.stateSet = initStateSet();

		this.current = scanner.next();
	}
	
	public static Parser fromFile(String filename) {
		return new Parser(Scanner.fromFile(filename));
	}

	public static Parser fromString(String text) {
		return new Parser(Scanner.fromString(text));
	}
	
	private Set<TokenType> initStateSet() {
		Set<TokenType> types = EnumSet.noneOf(TokenType.class);
		types.add(START_STATE);
		types.add(END_STATE);
		types.add(SINGLE_STATE);
		types.add(STATE_SET);
		return types;
	}

	public Symbols getSymbols() {
		return symbols;
	}

	private String eat(TokenType type) throws ParseException {
		if (type == current.type) {
			String image = current.image;
			current = scanner.next();
			return image;
		}else
			throw new ParseException(current, type);
	}
	
	private void skip() {
		while(current.type != EOL)
			current = scanner.next();
		current = scanner.next(); // eat EOL
	}
	
	private boolean isState(TokenType type) {
		return stateSet.contains(type);
	}

	public void entries() {
		while(current.type != EOI){
			try {
				Constant c; Table t;
				switch(current.type){
				case EOL : eat(EOL); break;
				case DEF: c = constant(); if(c != null) symbols.add(c); break;
				case FSM: {
					t = table();
					if(t != null) {
						symbols.add(t);
					}
					break;
				}
				default : throw new ParseException(current, DEF, FSM);
				}
			} catch (ParseException e) {
				System.out.println(e.getMessage());
				skip();
			} catch (SemanticException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private Constant constant() {				
		try {
			String id;
			Expr e;		

			eat(DEF); id = eat(ID);  e = expr(); eat(EOL);
			
			return new Constant(id, e);
		} catch (ParseException | SemanticException ex) {
			System.out.println(ex.getMessage());
			skip();
		}
		return null;
	}

	private Expr expr() throws ParseException, SemanticException {
		return switch (current.type) {
            case CHAR -> chr();
            case RANGE -> range();
            default -> throw new ParseException(current, CHAR, RANGE);
        };
	}
	
	private Expr chr() throws ParseException {
		if(current.type != CHAR)
			throw new ParseException(current, CHAR);

		Expr expr = Char.of(current.image);
		eat(CHAR);
		return expr;
	}
	
	private Expr range() throws ParseException, SemanticException {
		if(current.type != RANGE)
			throw new ParseException(current, RANGE);

		Expr expr = new Range(current.image);
		eat(RANGE);
		return expr;
	}
	
	

	private Table table() throws ParseException, SemanticException {
		String id;
		try {
			eat(FSM); id = eat(ID); eat(EOL);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			skip();
			return null;
		}		
		List<Expr> h;
		List<Transition> t;
		
		h = header();
		t = transitions(h.size());
		
		return new Table(id, h, t);
	}

	private List<Expr> header() throws ParseException, SemanticException {
		while(current.type == EOL) // skip empty lines
			current = scanner.next();
		
		List<Expr> list = new ArrayList<>();
		Expr e;
		String id; 
		
		while(current.type != EOL) {
			switch(current.type) {
			case CHAR  : e = chr(); list.add(e); break;
			case RANGE : e = range(); list.add(e); break;
			case ID    : id = eat(ID); list.add(symbols.getConstant(id)); break;
			default    : throw new ParseException(current, CHAR, RANGE, ID);
			}
		}
		eat(EOL);
		return list;
		
	}
	
	private List<Transition> transitions(int size) throws ParseException, SemanticException {
		while(current.type == EOL) // skip empty lines
			current = scanner.next();
		
		Transition t;
		List<Transition> ts = new ArrayList<>();
				
		while(isState(current.type)) {
			switch(current.type) {
			case START_STATE	: 
			case END_STATE		: 
			case SINGLE_STATE	: t = transition(size); ts.add(t); break;
			default :  throw new ParseException(current, START_STATE, END_STATE, SINGLE_STATE);
			}
		}
		return ts;
	}

	private Transition transition(int size) throws SemanticException {
		Transition t = null;
		try {
			State s; List<State> sl;
			s = fromState(); sl = toStates(size);
			t = new Transition(s, sl);
		} catch (ParseException ex) {
			System.out.println(ex.getMessage());
			skip();
		}
		return t;
	}

	private State fromState() throws ParseException {
		State s = switch (current.type) {
            case START_STATE -> {
				State state = new SingleState(current.image.substring(0,current.image.length()-1));
				state.setStartState(true);
				yield state;
			}
            case END_STATE -> {
				State state = new SingleState(current.image.substring(0,current.image.length()-1));
				state.setEndState(true);
				yield state;
			}
            case SINGLE_STATE -> new SingleState(current.image);
            default -> throw new ParseException(current, START_STATE, END_STATE, SINGLE_STATE);
        };

        eat(current.type);
		return s;
	}

	private List<State> toStates(int size) throws ParseException, SemanticException {
		List<State> sl = new ArrayList<>();
		State s;

		while(current.type != EOL) {
			s = toState(); sl.add(s);
		}
		eat(EOL);
		if(sl.size() != size)
			throw new SemanticException(sl + "does not match expected length of " + size);
		
		return sl;		
	}
	
	private State toState() throws ParseException, SemanticException {
		State s = switch (current.type) {
            case SINGLE_STATE -> new SingleState(current.image);
            case STATE_SET -> new StateSet(current.image);
            case NO_STATE -> NoState.getNoState();
            default -> throw new ParseException(current, SINGLE_STATE, STATE_SET, NO_STATE);
        };

        eat(current.type);
		return s;
	}
}
