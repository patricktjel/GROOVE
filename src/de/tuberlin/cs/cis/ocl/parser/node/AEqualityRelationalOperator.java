/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class AEqualityRelationalOperator extends PRelationalOperator
{
    private PEquationOperator _equationOperator_;

    public AEqualityRelationalOperator()
    {
        // Constructor
    }

    public AEqualityRelationalOperator(
        @SuppressWarnings("hiding") PEquationOperator _equationOperator_)
    {
        // Constructor
        setEquationOperator(_equationOperator_);

    }

    @Override
    public Object clone()
    {
        return new AEqualityRelationalOperator(
            cloneNode(this._equationOperator_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAEqualityRelationalOperator(this);
    }

    public PEquationOperator getEquationOperator()
    {
        return this._equationOperator_;
    }

    public void setEquationOperator(PEquationOperator node)
    {
        if(this._equationOperator_ != null)
        {
            this._equationOperator_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._equationOperator_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._equationOperator_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._equationOperator_ == child)
        {
            this._equationOperator_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._equationOperator_ == oldChild)
        {
            setEquationOperator((PEquationOperator) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
