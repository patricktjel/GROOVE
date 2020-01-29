/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class ASetCollectionKind extends PCollectionKind
{
    private TSet _set_;

    public ASetCollectionKind()
    {
        // Constructor
    }

    public ASetCollectionKind(
        @SuppressWarnings("hiding") TSet _set_)
    {
        // Constructor
        setSet(_set_);

    }

    @Override
    public Object clone()
    {
        return new ASetCollectionKind(
            cloneNode(this._set_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseASetCollectionKind(this);
    }

    public TSet getSet()
    {
        return this._set_;
    }

    public void setSet(TSet node)
    {
        if(this._set_ != null)
        {
            this._set_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._set_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._set_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._set_ == child)
        {
            this._set_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._set_ == oldChild)
        {
            setSet((TSet) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
