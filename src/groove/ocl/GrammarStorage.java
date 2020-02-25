package groove.ocl;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceKind;
import groove.grammar.type.TypeGraph;
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
public abstract class GrammarStorage {
    private final static Logger LOGGER = Log.getLogger(GrammarStorage.class.getName());

    private static final String graphLocation = "C:\\Users\\patri\\Google Drive\\UT\\afstuderen\\groove\\test.gps";
    private static SystemStore store;

    static {
        try {
            store = SystemStore.newStore(new File(graphLocation), false);
        } catch (IOException e) {
            LOGGER.severe("Could not load graph grammar");
        }
    }

    public static TypeGraph getTypeGraphs() {
        reload();
        return store.toGrammarModel().getTypeGraph();
    }

    public static void saveGraph(AspectGraph graph){
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

    private static void reload() {
        try {
            store.reload();
        } catch (IOException e) {
            LOGGER.severe("Reloading the grammar failed.");
        }
    }
}
