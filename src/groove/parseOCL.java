package groove;

import groove.ocl.GraphBuilder;
import groove.ocl.OCLParser;

public class parseOCL {

    static public void main(String[] args) {
        OCLParser parser = new OCLParser();
        parser.parseOCL("context Person inv: self.age >= 18");
//        testGraphBuilder();
    }

    /**
     * This constructs the graph for "context Person inv: self.age >= 18"
     */
    public static void testGraphBuilder() {
        GraphBuilder builder = new GraphBuilder("test");
        builder.addNode("forall","forall:");
        builder.addNode("person", "type:Person");

        builder.addNode("exists","exists:");
        builder.addNode("int:","int:");

        builder.addNode("prod","prod:");
        builder.addNode("bool:true", "bool:true");
        builder.addNode("int:18","int:18");

        builder.addEdge("person", "@", "forall");
        builder.addEdge("exists", "in", "forall");
        builder.addEdge("int:", "@", "exists");
        builder.addEdge("prod", "@", "exists");

        builder.addEdge("person", "age", "int:");
        builder.addEdge("prod", "arg:0", "int:");
        builder.addEdge("prod", "arg:1", "int:18");
        builder.addEdge("prod", "int:ge", "bool:true");

        builder.save();
    }
}
