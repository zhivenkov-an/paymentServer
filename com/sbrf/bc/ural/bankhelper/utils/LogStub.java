package com.sbrf.bc.ural.bankhelper.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sbrf.bc.ural.bankhelper.utils.html.HTMLRenderToFile;

public class LogStub implements ILog
{
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private HTMLRenderToFile rr;

	public boolean debug;
	public String encoding;
	
	public LogStub()
	{
		debug = false;
		encoding = "cp1251";
	}
	
	public void openLogFile(String fname,String cpage)
	{
		rr = new HTMLRenderToFile(fname,cpage);
		rr.write("-------------------------------------------------------------------------------------------------\n");
	}
	
	public void setEncoding(String enc)
	{
		encoding = enc;
	}
	@Override
	public void info(String text)
	{
		print(text);
		print2log(text,2);
	}

	@Override
	public void info(String style, String txt)
	{
		print(txt);
		print2log(txt,2);
	}

	@Override
	public void warning(String text)
	{
		if (debug) print(text);
		print2log(text,2);
	}

	@Override
	public void trace(String txt)
	{
		if (debug) print(txt);
		print2log(txt,2);
	}

	@Override
	public void error(String txt)
	{
		print(txt);
		print2log(txt,2);
	}
	public void print(String text)
	{
		
		try
		{
			System.out.write(text.getBytes(encoding));
			System.out.println("");
		}
		catch (IOException e)
		{
			throw new RuntimeException("io error",e);
		}
	}
	public void print2log(String text,int n)
	{
		
        Throwable ex = new Throwable();
        StackTraceElement ste = ex.getStackTrace()[n];
//        String callerClassName = ste.getClassName();
//        String callerMethodName = ste.getMethodName();
        String fileName = ste.getFileName();
        int callerLineNum = ste.getLineNumber();
        String line = String.format("%s %20s %s\n",sdf.format(new Date()), fileName + ":" + callerLineNum,text );
        if (rr!=null) rr.write(line);
	}
    public void h_exception(Exception e)
    {
        Throwable t = e;
        while(t!=null)
        {
            h_exception_intr(t);
            t = t.getCause();
        }
    }
    private void h_exception_intr(Throwable e)
    {
        ExceptionKeeper ek = new ExceptionKeeper(e,"");
        //error(ek.getException());
        //info(ek.getStack());
        //print2log("EXCEPTION\n----======= ERROR =============--------", 3);
        if (rr!=null) rr.write("----======= ERROR =============--------\n");
        if (rr!=null) rr.write(ek.getException());
        if (rr!=null) rr.write(ek.getStack());
    }

	public void close()
	{
		if (rr!=null) rr.close();
	}
}
