/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2010 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: Predicate_composite_type_graph.java 5479 2014-07-19 12:20:13Z rensink $
 */
package groove.prolog.builtin.type;

import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.grammar.type.TypeGraph;
import groove.prolog.GrooveEnvironment;
import groove.prolog.builtin.graph.GraphPrologCode;

/**
 * Predicate composite_type_graph(?TypeGraph)
 * @author Lesley Wevers
 */
public class Predicate_composite_type_graph extends GraphPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args)
        throws PrologException {

        if (!(interpreter.getEnvironment() instanceof GrooveEnvironment)) {
            GrooveEnvironment.invalidEnvironment();
        }

        TypeGraph typeGraph =
            ((GrooveEnvironment) interpreter.getEnvironment()).getGrooveState().getGraphGrammar().getTypeGraph();

        if (typeGraph == null) {
            return FAIL;
        }

        Term nodeTerm = new JavaObjectTerm(typeGraph);
        return interpreter.unify(args[0], nodeTerm);
    }
}
