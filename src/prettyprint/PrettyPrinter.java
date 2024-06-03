package prettyprint;

import tables.semantics.expr.Expr;
import tables.semantics.states.State;
import tables.semantics.states.StateSet;
import tables.semantics.table.Table;
import tables.semantics.table.Transition;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class PrettyPrinter {
    public static void writeDotFile(Table table) {
        try (FileWriter fileWriter = new FileWriter(table.getId() + ".dot")) {
            List<Expr> header = table.getHeader();
            List<Transition> transitionList = table.getTransitions();
            fileWriter.append("digraph ").append(table.getId()).append("{\n");
            fileWriter.append("\"").append(table.getTransitions().get(table.getStart()).from().getLabel()).append("\"[shape=circle, style=\"rounded\", color=\"blue\"];\n");
            for (int i : table.getEnds()) {
                fileWriter.append("\"").append(table.getTransitions().get(i).from().getLabel()).append("\"[peripheries=2, shape=circle, style=\"rounded\"];\n");
            }
            for (Transition transition : transitionList) {
                int i = -1;
                State from = transition.from();
                if (from.isNoState()) continue;
                for (State to : transition.to()) {
                    i++;
                    if (to.isNoState()) continue;
                    for(int ids : to.getIds()){
                        fileWriter.append("\"").append(from.getLabel()).append("\"");
                        fileWriter.append(" -> ");
                        fileWriter.append("\"").append(table.getTransitions().get(ids).from().getLabel()).append("\"");
                        fileWriter.append("[label=\"").append(String.valueOf(header.get(i))).append("\"];\n");
                    }
                }
            }
            fileWriter.append("}");
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            tryRunning(table.getId());
        } catch (IOException e) {
            System.out.println("Seems like GraphViz is not installed.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void tryRunning(String s) throws IOException, InterruptedException {
        // Befehl erstellen
        System.out.println(s);
        ProcessBuilder builder = new ProcessBuilder("dot", "-Tpng", s + ".dot", "-o", s + ".png");

        Process process = builder.start();
        // Ausgabe des Prozesses lesen
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // Warten, bis der Prozess beendet ist
        int exitCode = process.waitFor();
        System.out.println("Prozess beendet mit Exit-Code: " + exitCode);
    }
}
