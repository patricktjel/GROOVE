/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
 * $Id: AtomTerm.java 5479 2014-07-19 12:20:13Z rensink $
 */
package groove.control.term;

/**
 * Atomic block.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AtomTerm extends Term {
    /**
     * Constructs an atomic block.
     */
    public AtomTerm(Term arg0) {
        super(Op.ATOM, arg0);
    }

    @Override
    protected DerivationAttempt computeAttempt(boolean nested) {
        DerivationAttempt result = null;
        if (arg0().isTrial()) {
            DerivationAttempt ders = arg0().getAttempt(nested);
            result = createAttempt();
            for (Derivation deriv : ders) {
                result.add(deriv.newInstance(deriv.onFinish().transit(), true));
            }
            result.setSuccess(ders.onSuccess().atom());
            result.setFailure(ders.onFailure().atom());
            return result;
        }
        return result;
    }

    @Override
    protected int computeDepth() {
        return 0;
    }

    @Override
    protected Type computeType() {
        return arg0().getType();
    }

    @Override
    protected boolean isAtomic() {
        return true;
    }
}
