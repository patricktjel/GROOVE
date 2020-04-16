package groove.gui.action;

import de.tuberlin.cs.cis.ocl.parser.lexer.Lexer;
import de.tuberlin.cs.cis.ocl.parser.lexer.LexerException;
import de.tuberlin.cs.cis.ocl.parser.node.Start;
import de.tuberlin.cs.cis.ocl.parser.parser.Parser;
import de.tuberlin.cs.cis.ocl.parser.parser.ParserException;
import groove.grammar.QualName;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceKind;
import groove.graph.plain.PlainGraph;
import groove.gui.Options;
import groove.gui.Simulator;
import groove.io.store.EditType;
import groove.ocl.GrammarStorage;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.LaxSimplifier;
import groove.ocl.lax.condition.LaxCondition;
import groove.ocl.parser.TranslateOCLToLax;

import javax.swing.*;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Map;

/** Action to create and start editing a new control program. */
public class NewAction extends SimulatorAction {
    /** Constructs a new action, for a given control panel. */
    public NewAction(Simulator simulator, ResourceKind resource) {
        super(simulator, EditType.CREATE, resource);
    }

    @Override
    public void execute() {
        ResourceKind resource = getResourceKind();

        if (resource.equals(ResourceKind.OCL)) {
            final String newName = askOClConstraint(Options.getNewResourceName(resource));
            createOclConstraint(newName);
            return;
        }

        final QualName newName = askNewName(Options.getNewResourceName(resource), true);
        if (newName != null) {
            try {
                if (resource.isGraphBased()) {
                    final AspectGraph newGraph = AspectGraph.emptyGraph(newName.toString(), resource.getGraphRole());
                    getSimulatorModel().doAddGraph(resource, newGraph, false);
                } else {
                    getSimulatorModel().doAddText(getResourceKind(), newName, "");
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getDisplay().startEditResource(newName);
                    }
                });
            } catch (IOException e) {
                showErrorDialog(e,
                    "Error creating new %s '%s'",
                    resource.getDescription(),
                    newName);
            }
        }
    }

    private void createOclConstraint(String ocl) {
        try {
            parseOCL(ocl);
        } catch (ParserException | IOException | LexerException e) {
            showErrorDialog(e,
                    "Error creating new OCL");
        }
    }

    private void parseOCL(String ocl) throws ParserException, IOException, LexerException {
        String graphLocation = this.getSimulator().getModel().getGrammar().getStore().getLocation().getAbsolutePath();
        GrammarStorage grammarStorage = new GrammarStorage(graphLocation);

        // create parser
        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(ocl))));
        Start parseTree = parser.parse();

        // translate the ocl constraint(s)
        TranslateOCLToLax translateOCLToLax = new TranslateOCLToLax(grammarStorage.getTypeGraphs());
        parseTree.apply(translateOCLToLax);

        Map<LaxCondition, GraphBuilder> conditions = translateOCLToLax.getResults();
        for (Map.Entry<LaxCondition, GraphBuilder> entry : conditions.entrySet()) {
            LaxCondition condition = entry.getKey();
            GraphBuilder graphBuilder = entry.getValue();

            // simplify every constraint
            LaxSimplifier laxSimplifier = new LaxSimplifier(graphBuilder);
            laxSimplifier.simplify(condition);

            // and save the graph
            PlainGraph graph = graphBuilder.laxToGraph(condition);
            grammarStorage.saveGraph(graph);
        }
    }

    @Override
    public void refresh() {
        setEnabled(getGrammarStore() != null);
    }
}