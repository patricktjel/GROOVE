/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class ANumberLiteral extends PLiteral
{
    private TNumberLiteral _numberLiteral_;

    public ANumberLiteral()
    {
        // Constructor
    }

    public ANumberLiteral(
        @SuppressWarnings("hiding") TNumberLiteral _numberLiteral_)
    {
        // Constructor
        setNumberLiteral(_numberLiteral_);

    }

    @Override
    public Object clone()
    {
        return new ANumberLiteral(
            cloneNode(this._numberLiteral_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANumberLiteral(this);
    }

    public TNumberLiteral getNumberLiteral()
    {
        return this._numberLiteral_;
    }

    public void setNumberLiteral(TNumberLiteral node)
    {
        if(this._numberLiteral_ != null)
        {
            this._numberLiteral_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._numberLiteral_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._numberLiteral_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._numberLiteral_ == child)
        {
            this._numberLiteral_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._numberLiteral_ == oldChild)
        {
            setNumberLiteral((TNumberLiteral) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
