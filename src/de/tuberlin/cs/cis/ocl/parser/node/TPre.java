/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class TPre extends Token
{
    public TPre()
    {
        super.setText("pre");
    }

    public TPre(int line, int pos)
    {
        super.setText("pre");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TPre(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTPre(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TPre text.");
    }
}
