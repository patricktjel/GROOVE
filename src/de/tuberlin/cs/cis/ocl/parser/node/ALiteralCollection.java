/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class ALiteralCollection extends PLiteralCollection
{
    private PCollectionKind _collectionKind_;
    private TLBrace _lBrace_;
    private PCollectionItemList _collectionItemList_;
    private TRBrace _rBrace_;

    public ALiteralCollection()
    {
        // Constructor
    }

    public ALiteralCollection(
        @SuppressWarnings("hiding") PCollectionKind _collectionKind_,
        @SuppressWarnings("hiding") TLBrace _lBrace_,
        @SuppressWarnings("hiding") PCollectionItemList _collectionItemList_,
        @SuppressWarnings("hiding") TRBrace _rBrace_)
    {
        // Constructor
        setCollectionKind(_collectionKind_);

        setLBrace(_lBrace_);

        setCollectionItemList(_collectionItemList_);

        setRBrace(_rBrace_);

    }

    @Override
    public Object clone()
    {
        return new ALiteralCollection(
            cloneNode(this._collectionKind_),
            cloneNode(this._lBrace_),
            cloneNode(this._collectionItemList_),
            cloneNode(this._rBrace_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALiteralCollection(this);
    }

    public PCollectionKind getCollectionKind()
    {
        return this._collectionKind_;
    }

    public void setCollectionKind(PCollectionKind node)
    {
        if(this._collectionKind_ != null)
        {
            this._collectionKind_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._collectionKind_ = node;
    }

    public TLBrace getLBrace()
    {
        return this._lBrace_;
    }

    public void setLBrace(TLBrace node)
    {
        if(this._lBrace_ != null)
        {
            this._lBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lBrace_ = node;
    }

    public PCollectionItemList getCollectionItemList()
    {
        return this._collectionItemList_;
    }

    public void setCollectionItemList(PCollectionItemList node)
    {
        if(this._collectionItemList_ != null)
        {
            this._collectionItemList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._collectionItemList_ = node;
    }

    public TRBrace getRBrace()
    {
        return this._rBrace_;
    }

    public void setRBrace(TRBrace node)
    {
        if(this._rBrace_ != null)
        {
            this._rBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rBrace_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._collectionKind_)
            + toString(this._lBrace_)
            + toString(this._collectionItemList_)
            + toString(this._rBrace_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._collectionKind_ == child)
        {
            this._collectionKind_ = null;
            return;
        }

        if(this._lBrace_ == child)
        {
            this._lBrace_ = null;
            return;
        }

        if(this._collectionItemList_ == child)
        {
            this._collectionItemList_ = null;
            return;
        }

        if(this._rBrace_ == child)
        {
            this._rBrace_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._collectionKind_ == oldChild)
        {
            setCollectionKind((PCollectionKind) newChild);
            return;
        }

        if(this._lBrace_ == oldChild)
        {
            setLBrace((TLBrace) newChild);
            return;
        }

        if(this._collectionItemList_ == oldChild)
        {
            setCollectionItemList((PCollectionItemList) newChild);
            return;
        }

        if(this._rBrace_ == oldChild)
        {
            setRBrace((TRBrace) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
