/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import java.util.*;
import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class AAdditiveExpression extends PAdditiveExpression
{
    private PMultiplicativeExpression _multiplicativeExpression_;
    private final LinkedList<PAddition> _addition_ = new LinkedList<PAddition>();

    public AAdditiveExpression()
    {
        // Constructor
    }

    public AAdditiveExpression(
        @SuppressWarnings("hiding") PMultiplicativeExpression _multiplicativeExpression_,
        @SuppressWarnings("hiding") List<?> _addition_)
    {
        // Constructor
        setMultiplicativeExpression(_multiplicativeExpression_);

        setAddition(_addition_);

    }

    @Override
    public Object clone()
    {
        return new AAdditiveExpression(
            cloneNode(this._multiplicativeExpression_),
            cloneList(this._addition_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAdditiveExpression(this);
    }

    public PMultiplicativeExpression getMultiplicativeExpression()
    {
        return this._multiplicativeExpression_;
    }

    public void setMultiplicativeExpression(PMultiplicativeExpression node)
    {
        if(this._multiplicativeExpression_ != null)
        {
            this._multiplicativeExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._multiplicativeExpression_ = node;
    }

    public LinkedList<PAddition> getAddition()
    {
        return this._addition_;
    }

    public void setAddition(List<?> list)
    {
        for(PAddition e : this._addition_)
        {
            e.parent(null);
        }
        this._addition_.clear();

        for(Object obj_e : list)
        {
            PAddition e = (PAddition) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._addition_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._multiplicativeExpression_)
            + toString(this._addition_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._multiplicativeExpression_ == child)
        {
            this._multiplicativeExpression_ = null;
            return;
        }

        if(this._addition_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._multiplicativeExpression_ == oldChild)
        {
            setMultiplicativeExpression((PMultiplicativeExpression) newChild);
            return;
        }

        for(ListIterator<PAddition> i = this._addition_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PAddition) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}
