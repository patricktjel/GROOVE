/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class ALogicalContextOperationName extends PContextOperationName
{
    private PLogicalOperator _logicalOperator_;

    public ALogicalContextOperationName()
    {
        // Constructor
    }

    public ALogicalContextOperationName(
        @SuppressWarnings("hiding") PLogicalOperator _logicalOperator_)
    {
        // Constructor
        setLogicalOperator(_logicalOperator_);

    }

    @Override
    public Object clone()
    {
        return new ALogicalContextOperationName(
            cloneNode(this._logicalOperator_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALogicalContextOperationName(this);
    }

    public PLogicalOperator getLogicalOperator()
    {
        return this._logicalOperator_;
    }

    public void setLogicalOperator(PLogicalOperator node)
    {
        if(this._logicalOperator_ != null)
        {
            this._logicalOperator_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._logicalOperator_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._logicalOperator_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._logicalOperator_ == child)
        {
            this._logicalOperator_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._logicalOperator_ == oldChild)
        {
            setLogicalOperator((PLogicalOperator) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
