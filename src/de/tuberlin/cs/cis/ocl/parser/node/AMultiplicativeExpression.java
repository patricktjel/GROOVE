/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import java.util.*;
import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class AMultiplicativeExpression extends PMultiplicativeExpression
{
    private PUnaryExpression _unaryExpression_;
    private final LinkedList<PMultiplication> _multiplication_ = new LinkedList<PMultiplication>();

    public AMultiplicativeExpression()
    {
        // Constructor
    }

    public AMultiplicativeExpression(
        @SuppressWarnings("hiding") PUnaryExpression _unaryExpression_,
        @SuppressWarnings("hiding") List<?> _multiplication_)
    {
        // Constructor
        setUnaryExpression(_unaryExpression_);

        setMultiplication(_multiplication_);

    }

    @Override
    public Object clone()
    {
        return new AMultiplicativeExpression(
            cloneNode(this._unaryExpression_),
            cloneList(this._multiplication_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMultiplicativeExpression(this);
    }

    public PUnaryExpression getUnaryExpression()
    {
        return this._unaryExpression_;
    }

    public void setUnaryExpression(PUnaryExpression node)
    {
        if(this._unaryExpression_ != null)
        {
            this._unaryExpression_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._unaryExpression_ = node;
    }

    public LinkedList<PMultiplication> getMultiplication()
    {
        return this._multiplication_;
    }

    public void setMultiplication(List<?> list)
    {
        for(PMultiplication e : this._multiplication_)
        {
            e.parent(null);
        }
        this._multiplication_.clear();

        for(Object obj_e : list)
        {
            PMultiplication e = (PMultiplication) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._multiplication_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._unaryExpression_)
            + toString(this._multiplication_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._unaryExpression_ == child)
        {
            this._unaryExpression_ = null;
            return;
        }

        if(this._multiplication_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._unaryExpression_ == oldChild)
        {
            setUnaryExpression((PUnaryExpression) newChild);
            return;
        }

        for(ListIterator<PMultiplication> i = this._multiplication_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PMultiplication) newChild);
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
