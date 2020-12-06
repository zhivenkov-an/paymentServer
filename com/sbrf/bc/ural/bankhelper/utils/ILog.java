package com.sbrf.bc.ural.bankhelper.utils;

public interface ILog
{
	void info(String text);
	void info(String style,String txt);
	void warning(String text);
	void trace(String txt);
	void error(String txt);
}
