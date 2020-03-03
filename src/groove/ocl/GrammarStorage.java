package groove.ocl;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceKind;
import groove.grammar.type.TypeGraph;
import groove.graph.plain.PlainGraph;
import groove.io.store.SystemStore;
import groove.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * An helper class that regulates the connection with the grammar files
 * An instance is not allowed therefore the class is abstract
 */
public class GrammarStorage {
    private final static Logger LOGGER = Log.getLogger(GrammarStorage.class.getName());

    private SystemStore store;

    public GrammarStorage(String graphLocation) {
        try {
            store = SystemStore.newStore(new File(graphLocation), false);
        } catch (IOException e) {
            LOGGER.severe("Could not load graph grammar");
        }
    }

    public TypeGraph getTypeGraphs() {
        reload();
        return store.toGrammarModel().getTypeGraph();
    }

    public void saveGraph(PlainGraph plainGraph){
        // first transform it to an AspectGraph
        AspectGraph graph = AspectGraph.newInstance(plainGraph);
        try {
            reload();
            //TODO maybe not the best way to set the graph fixed.
            graph.setFixed();
            store.putGraphs(ResourceKind.RULE, Collections.singleton(graph), false);
            LOGGER.info(String.format("Rule graph: %s is saved", graph.getQualName()));
        } catch (IOException e) {
            LOGGER.warning(String.format("Could not save: %s", graph.getQualName()));
        }
    }

    private void reload() {
        try {
            store.reload();
        } catch (IOException e) {
            LOGGER.severe("Reloading the grammar failed.");
        }
    }
}
