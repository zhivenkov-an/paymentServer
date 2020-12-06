package com.sbrf.bc.ural.bankhelper.utils.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.LinkedList;

public class HTMLRenderToFile
{
    private Writer writer;
    private LinkedList<String> tag_stack = new LinkedList<String>();
    public HTMLRenderToFile(String filename,String codepage)
    {
        try
        {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename,true), codepage));
        } 
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Не поддерживаемая кодировка файла");
        } 
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("файл не найден:"+filename);
        }
    }
    public void init_local()
    {
        InputStream asStream = HTMLRenderToFile.class.getResourceAsStream("html.html");
        if (asStream == null) throw new NullPointerException();
        copy_from_file(asStream);
        _add_tag_onstack("html");
        _add_tag_onstack("head");
        endTag();
    }
    public void copy_from_file(InputStream stream_template)
    {
        BufferedReader br;
        try
        {
            br = new BufferedReader(new InputStreamReader(stream_template, "cp1251"));
        } catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Не поддерживаемая кодировка файла");
        } 
//        catch (FileNotFoundException e)
//        {
//            throw new XFrameWorkException("файл не найден:"+fname);
//        }
        String s;
        try
        {
            while(true)
            {
                s = br.readLine();
                if (s==null) break;
                writer.write(s);
                writer.write("\n");
            }
            
        } catch (IOException e)
        {
            throw new RuntimeException("Ошибка ввода-вывода",e);
        }
        finally
        {
            try
            {
            br.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException("Ошибка ввода-вывода",e);
            }
        }
    }
    public void copy_from_file(String fname)
    {
        try
        {
            InputStream stream_template = new FileInputStream(fname);
            copy_from_file(stream_template);
        } 
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("файл не найден:"+fname);
        }
    }
    void write(long l)
    {
        try
        {
            writer.write(String.valueOf(l));
        } catch (IOException e)
        {
            throw new RuntimeException("Ошибка ввода-вывода",e);
        }
    }
    public void write(int str)
    {
    	write(String.valueOf(str));
    }
    public void write(String str)
    {
        try
        {
            if (str==null) writer.write("null");
            else writer.write(str);
        } catch (IOException e)
        {
            throw new RuntimeException("Ошибка ввода-вывода",e);
        }
    }
    public void _add_tag_onstack(String tag)
    {
        tag_stack.add(tag);
    }
    public void startTag(String tag)
    {
        tag_stack.add(tag);
        try
        {
            writer.write("<");
            writer.write(tag);
            writer.write(">");
        } catch (IOException e)
        {
            throw new RuntimeException("Ошибка ввода-вывода",e);
        }
    }
    public void startTag(String tag,String attrs)
    {
        tag_stack.add(tag);
        try
        {
            writer.write("<");
            writer.write(tag);
            writer.write(" ");
            writer.write(attrs);
            writer.write(">");
        } catch (IOException e)
        {
            throw new RuntimeException("Ошибка ввода-вывода",e);
        }
    }
    public void endTag()
    {
        endTag(false);
    }
    public void endTag(boolean newline)
    {
        String tag = tag_stack.getLast();
        tag_stack.removeLast();
        try
        {
            writer.write("</");
            writer.write(tag);
            writer.write(">");
            if (newline) writer.write("\n");
        } catch (IOException e)
        {
            throw new RuntimeException("Ошибка ввода-вывода",e);
        }
    }
    public void close()
    {
        while(!tag_stack.isEmpty())
        {
            endTag();
        }
        try
        {
            writer.close();
        } catch (IOException e)
        {
            throw new RuntimeException("Ошибка ввода-вывода",e);
        }
    }

    public void writeTagOutputText(String text)
    {
        write("<h:outputText value=\"");
        write(text);
        write("\" />");
    }
}
