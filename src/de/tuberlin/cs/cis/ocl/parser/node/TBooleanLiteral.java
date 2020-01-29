/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.node;

import de.tuberlin.cs.cis.ocl.parser.analysis.*;

@SuppressWarnings("nls")
public final class TBooleanLiteral extends Token
{
    public TBooleanLiteral(String text)
    {
        setText(text);
    }

    public TBooleanLiteral(String text, int line, int pos)
    {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TBooleanLiteral(getText(), getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTBooleanLiteral(this);
    }
}
