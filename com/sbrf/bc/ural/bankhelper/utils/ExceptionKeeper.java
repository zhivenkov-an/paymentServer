package com.sbrf.bc.ural.bankhelper.utils;

public class ExceptionKeeper
{
    private StringBuilder exception = new StringBuilder();
    private StringBuilder stack = new StringBuilder();

    public ExceptionKeeper(Throwable t,String info)
    {
        exception.append(info);
        exception.append(t.getClass().toString());
        if (t.getMessage()!=null)
        {
            exception.append("\n");
            exception.append(t.getMessage());
        }
//        exception = t.toString();
        StackTraceElement[] st = t.getStackTrace();
        exception.append("\n");
        exception.append(st[0].toString());
        for (StackTraceElement s : st)
        {
            //String str = s.toString();
            stack.append(s.toString());
            stack.append("\n");
        }
    }

    public String getException()
    {
        return exception.toString();
    }

    public String getStack()
    {
        return stack.toString();
    }
}