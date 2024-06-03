package testing;

import org.junit.jupiter.api.Test;
import prettyprint.PrettyPrinter;
import tables.parser.Parser;
import tables.semantics.states.StateSet;
import tables.semantics.symbols.SemanticException;
import tables.semantics.symbols.Symbols;
import tables.semantics.table.Table;
import tables.semantics.table.Tables;
import tables.semantics.table.Transition;

public class NEAtoDEATest {
    @Test
    void testToDea() throws SemanticException {
        Parser p = Parser.fromFile("samples/sample2.txt");
        p.entries();
        Symbols s = p.getSymbols();
        Table nea = s.getTable("nondett");
        for(Transition t : nea.getTransitions()){
            t.from().setLabel(String.valueOf(t.from().getId()));
        }
        Table dea = Tables.toDea2(nea);
        PrettyPrinter.writeDotFile(dea);
        PrettyPrinter.writeDotFile(nea);
    }
}
