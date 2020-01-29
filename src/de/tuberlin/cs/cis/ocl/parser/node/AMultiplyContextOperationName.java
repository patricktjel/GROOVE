/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class AMultiplyContextOperationName extends PContextOperationName
{
    private PMultiplyOperator _multiplyOperator_;

    public AMultiplyContextOperationName()
    {
        // Constructor
    }

    public AMultiplyContextOperationName(
        @SuppressWarnings("hiding") PMultiplyOperator _multiplyOperator_)
    {
        // Constructor
        setMultiplyOperator(_multiplyOperator_);

    }

    @Override
    public Object clone()
    {
        return new AMultiplyContextOperationName(
            cloneNode(this._multiplyOperator_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMultiplyContextOperationName(this);
    }

    public PMultiplyOperator getMultiplyOperator()
    {
        return this._multiplyOperator_;
    }

    public void setMultiplyOperator(PMultiplyOperator node)
    {
        if(this._multiplyOperator_ != null)
        {
            this._multiplyOperator_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._multiplyOperator_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._multiplyOperator_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._multiplyOperator_ == child)
        {
            this._multiplyOperator_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._multiplyOperator_ == oldChild)
        {
            setMultiplyOperator((PMultiplyOperator) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
