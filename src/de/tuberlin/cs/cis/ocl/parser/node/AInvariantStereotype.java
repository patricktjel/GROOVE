/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class AInvariantStereotype extends PStereotype
{
    private TInv _inv_;

    public AInvariantStereotype()
    {
        // Constructor
    }

    public AInvariantStereotype(
        @SuppressWarnings("hiding") TInv _inv_)
    {
        // Constructor
        setInv(_inv_);

    }

    @Override
    public Object clone()
    {
        return new AInvariantStereotype(
            cloneNode(this._inv_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAInvariantStereotype(this);
    }

    public TInv getInv()
    {
        return this._inv_;
    }

    public void setInv(TInv node)
    {
        if(this._inv_ != null)
        {
            this._inv_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._inv_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._inv_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._inv_ == child)
        {
            this._inv_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._inv_ == oldChild)
        {
            setInv((TInv) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
