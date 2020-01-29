/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class ASimpleTypePostfix extends PSimpleTypePostfix
{
    private TColon _colon_;
    private PSimpleTypeSpecifier _simpleTypeSpecifier_;

    public ASimpleTypePostfix()
    {
        // Constructor
    }

    public ASimpleTypePostfix(
        @SuppressWarnings("hiding") TColon _colon_,
        @SuppressWarnings("hiding") PSimpleTypeSpecifier _simpleTypeSpecifier_)
    {
        // Constructor
        setColon(_colon_);

        setSimpleTypeSpecifier(_simpleTypeSpecifier_);

    }

    @Override
    public Object clone()
    {
        return new ASimpleTypePostfix(
            cloneNode(this._colon_),
            cloneNode(this._simpleTypeSpecifier_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseASimpleTypePostfix(this);
    }

    public TColon getColon()
    {
        return this._colon_;
    }

    public void setColon(TColon node)
    {
        if(this._colon_ != null)
        {
            this._colon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._colon_ = node;
    }

    public PSimpleTypeSpecifier getSimpleTypeSpecifier()
    {
        return this._simpleTypeSpecifier_;
    }

    public void setSimpleTypeSpecifier(PSimpleTypeSpecifier node)
    {
        if(this._simpleTypeSpecifier_ != null)
        {
            this._simpleTypeSpecifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._simpleTypeSpecifier_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._colon_)
            + toString(this._simpleTypeSpecifier_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._colon_ == child)
        {
            this._colon_ = null;
            return;
        }

        if(this._simpleTypeSpecifier_ == child)
        {
            this._simpleTypeSpecifier_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._colon_ == oldChild)
        {
            setColon((TColon) newChild);
            return;
        }

        if(this._simpleTypeSpecifier_ == oldChild)
        {
            setSimpleTypeSpecifier((PSimpleTypeSpecifier) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
