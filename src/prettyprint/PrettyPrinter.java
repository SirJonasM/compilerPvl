package prettyprint;

import tables.semantics.expr.Expr;
import tables.semantics.states.State;
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
            fileWriter.append(String.valueOf(table.getStart())).append("[shape=circle, style=\"rounded\", color=\"blue\"];\n");
            for (int i : table.getEnds()) {
                fileWriter.append(String.valueOf(i)).append("[shape=circle, style=\"rounded\",color=\"red\"];\n");
            }
            for (Transition transition : transitionList) {
                int i = -1;
                State from = transition.from();
                if (from.isNoState()) continue;
                for (State to : transition.to()) {
                    i++;
                    if (to.isNoState()) continue;
                    fileWriter.append(String.valueOf(from.getId()));
                    fileWriter.append(" -> ");
                    fileWriter.append(String.valueOf(to.getId()));
                    fileWriter.append("[label=\"").append(String.valueOf(header.get(i))).append("\"];\n");
                }
            }
            fileWriter.append("}");
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tryRunning(table.getId());
    }

    private static void tryRunning(String s) {
        try {
            // Befehl erstellen
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


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
