/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class ACollectionKind extends PCollectionKind
{
    private TCollection _collection_;

    public ACollectionKind()
    {
        // Constructor
    }

    public ACollectionKind(
        @SuppressWarnings("hiding") TCollection _collection_)
    {
        // Constructor
        setCollection(_collection_);

    }

    @Override
    public Object clone()
    {
        return new ACollectionKind(
            cloneNode(this._collection_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseACollectionKind(this);
    }

    public TCollection getCollection()
    {
        return this._collection_;
    }

    public void setCollection(TCollection node)
    {
        if(this._collection_ != null)
        {
            this._collection_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._collection_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._collection_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._collection_ == child)
        {
            this._collection_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._collection_ == oldChild)
        {
            setCollection((TCollection) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
