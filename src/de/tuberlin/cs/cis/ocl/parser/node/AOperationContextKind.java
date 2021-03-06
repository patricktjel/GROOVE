/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class AOperationContextKind extends PContextKind
{
    private PName _name_;
    private TDcolon _dcolon_;
    private PContextOperationName _contextOperationName_;
    private TLPar _lPar_;
    private PFormalParameterList _formalParameterList_;
    private TRPar _rPar_;
    private PReturnType _returnType_;

    public AOperationContextKind()
    {
        // Constructor
    }

    public AOperationContextKind(
        @SuppressWarnings("hiding") PName _name_,
        @SuppressWarnings("hiding") TDcolon _dcolon_,
        @SuppressWarnings("hiding") PContextOperationName _contextOperationName_,
        @SuppressWarnings("hiding") TLPar _lPar_,
        @SuppressWarnings("hiding") PFormalParameterList _formalParameterList_,
        @SuppressWarnings("hiding") TRPar _rPar_,
        @SuppressWarnings("hiding") PReturnType _returnType_)
    {
        // Constructor
        setName(_name_);

        setDcolon(_dcolon_);

        setContextOperationName(_contextOperationName_);

        setLPar(_lPar_);

        setFormalParameterList(_formalParameterList_);

        setRPar(_rPar_);

        setReturnType(_returnType_);

    }

    @Override
    public Object clone()
    {
        return new AOperationContextKind(
            cloneNode(this._name_),
            cloneNode(this._dcolon_),
            cloneNode(this._contextOperationName_),
            cloneNode(this._lPar_),
            cloneNode(this._formalParameterList_),
            cloneNode(this._rPar_),
            cloneNode(this._returnType_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAOperationContextKind(this);
    }

    public PName getName()
    {
        return this._name_;
    }

    public void setName(PName node)
    {
        if(this._name_ != null)
        {
            this._name_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._name_ = node;
    }

    public TDcolon getDcolon()
    {
        return this._dcolon_;
    }

    public void setDcolon(TDcolon node)
    {
        if(this._dcolon_ != null)
        {
            this._dcolon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._dcolon_ = node;
    }

    public PContextOperationName getContextOperationName()
    {
        return this._contextOperationName_;
    }

    public void setContextOperationName(PContextOperationName node)
    {
        if(this._contextOperationName_ != null)
        {
            this._contextOperationName_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._contextOperationName_ = node;
    }

    public TLPar getLPar()
    {
        return this._lPar_;
    }

    public void setLPar(TLPar node)
    {
        if(this._lPar_ != null)
        {
            this._lPar_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lPar_ = node;
    }

    public PFormalParameterList getFormalParameterList()
    {
        return this._formalParameterList_;
    }

    public void setFormalParameterList(PFormalParameterList node)
    {
        if(this._formalParameterList_ != null)
        {
            this._formalParameterList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._formalParameterList_ = node;
    }

    public TRPar getRPar()
    {
        return this._rPar_;
    }

    public void setRPar(TRPar node)
    {
        if(this._rPar_ != null)
        {
            this._rPar_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rPar_ = node;
    }

    public PReturnType getReturnType()
    {
        return this._returnType_;
    }

    public void setReturnType(PReturnType node)
    {
        if(this._returnType_ != null)
        {
            this._returnType_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._returnType_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._name_)
            + toString(this._dcolon_)
            + toString(this._contextOperationName_)
            + toString(this._lPar_)
            + toString(this._formalParameterList_)
            + toString(this._rPar_)
            + toString(this._returnType_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._name_ == child)
        {
            this._name_ = null;
            return;
        }

        if(this._dcolon_ == child)
        {
            this._dcolon_ = null;
            return;
        }

        if(this._contextOperationName_ == child)
        {
            this._contextOperationName_ = null;
            return;
        }

        if(this._lPar_ == child)
        {
            this._lPar_ = null;
            return;
        }

        if(this._formalParameterList_ == child)
        {
            this._formalParameterList_ = null;
            return;
        }

        if(this._rPar_ == child)
        {
            this._rPar_ = null;
            return;
        }

        if(this._returnType_ == child)
        {
            this._returnType_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._name_ == oldChild)
        {
            setName((PName) newChild);
            return;
        }

        if(this._dcolon_ == oldChild)
        {
            setDcolon((TDcolon) newChild);
            return;
        }

        if(this._contextOperationName_ == oldChild)
        {
            setContextOperationName((PContextOperationName) newChild);
            return;
        }

        if(this._lPar_ == oldChild)
        {
            setLPar((TLPar) newChild);
            return;
        }

        if(this._formalParameterList_ == oldChild)
        {
            setFormalParameterList((PFormalParameterList) newChild);
            return;
        }

        if(this._rPar_ == oldChild)
        {
            setRPar((TRPar) newChild);
            return;
        }

        if(this._returnType_ == oldChild)
        {
            setReturnType((PReturnType) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
