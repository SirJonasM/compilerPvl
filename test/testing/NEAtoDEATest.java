package testing;

import org.junit.jupiter.api.Test;
import prettyprint.PrettyPrinter;
import tables.parser.Parser;
import tables.semantics.states.StateSet;
import tables.semantics.symbols.SemanticException;
import tables.semantics.symbols.Symbols;
import tables.semantics.table.Table;
import tables.semantics.table.Tables;

public class NEAtoDEATest {
    @Test
    void test() throws SemanticException {
        int i = 0;
        System.out.println(i++);
        System.out.println(i);
        StateSet stateSet = new StateSet("1|2|3");
        System.out.println(stateSet);
        Parser p = Parser.fromFile("samples/sample2.txt");
        p.entries();
        Symbols s = p.getSymbols();
        Table t = s.getTable("nondett");
        Table dea = Tables.toDea(t);
        System.out.println(dea);
        PrettyPrinter.writeDotFile(dea);
    }
}
