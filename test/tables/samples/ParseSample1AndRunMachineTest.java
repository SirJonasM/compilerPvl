package tables.samples;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import statemachine.StateMachine;
import tables.parser.Parser;
import tables.semantics.symbols.SemanticException;
import tables.semantics.symbols.Symbols;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParseSample1AndRunMachineTest {
	Parser p;
	@BeforeEach
	void setUp(){
		p = Parser.fromFile("samples/sample1.txt");

	}
	@Test
	void test() throws SemanticException {
		p.entries();

		Symbols s = p.getSymbols();
		System.out.println(s);

		StateMachine sm = new StateMachine(s.getTable("integer"));
		System.out.println(sm.toDetailedString());

		{
			sm.init();
			int pos = 0;
			String input = "1234";
			while(sm.isRunning()) {
				char c = (pos < input.length() ? input.charAt(pos++) : 0);
				sm.consume(c);
			}
			System.out.println("Consumption of '" + input + "' succeeded" + " : " + sm.succeeded());
		}

		{
			sm.init();
			int pos = 0;
			String input = "abc";
			while(sm.isRunning()) {
				char c = (pos < input.length() ? input.charAt(pos++) : 0);
				sm.consume(c);
			}
			System.out.println("Consumption of '" + input + "' succeeded" + " : " + sm.succeeded());
		}
	}

}
