package tables.samples;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tables.parser.Parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ParseSample2Test {
	Parser p;

	@BeforeEach
	void setUp(){
		p = Parser.fromFile("samples/sample2.txt");
	}
	@Test
	void test(){
		assertDoesNotThrow(() -> p.entries());

		System.out.println(p.getSymbols());
		
	}

}
