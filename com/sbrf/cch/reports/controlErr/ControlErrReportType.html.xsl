<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:xalan="http://xml.apache.org/xslt">
    
    <xsl:import href="/com/sbrf/report/types/xsl/ReportCommonFunctions.xsl" />
    <xsl:output method="xhtml"
                encoding="UTF-8"
                doctype-public="-//W3C//DTD XHTML 1.1//EN"
                doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
                indent="yes"/>
   
    <xsl:template match = "/root">
        <html xml:lang="ru">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
            <title>Отчет</title>
        </head>
        <body>
        <xsl:apply-templates select = "header"/>
        <xsl:apply-templates select = "items"/>
        <xsl:apply-templates select = "error"/>
        </body>
        </html>  
    </xsl:template>
    
    <xsl:template match="header">
        <h2>
            <xsl:value-of select="."/>
        </h2>
    </xsl:template>
    
    <xsl:template match="error">
        <h4>
            <xsl:text>Ошибка: </xsl:text>
        </h4>
        <div>
            <xsl:value-of select="message"/>
        </div>
    </xsl:template>
    
    <xsl:template match="items">
        <h4>
            <xsl:text>Найдено </xsl:text>
            <xsl:value-of select="summary/counter"/>
            <xsl:text> записей.</xsl:text>
            <br/>
            <xsl:if test="summary/message != ''">
                <xsl:value-of select="summary/message"/>
                <br/>
                <xsl:text>Максимальное количесво отображаемых записей </xsl:text>
                <xsl:value-of select="summary/maxrows"/>
                <xsl:text>.</xsl:text>
            </xsl:if>
        </h4>
        <table border="1" cellspacing="0" cellpadding="3" style="text-align:left; table-layout:fixed; width:1em;">
            <thead>
                <xsl:apply-templates select="columns"/>
            </thead>
            <tbody>
                <xsl:apply-templates select="item"/>
            </tbody>
        </table>    
    </xsl:template>
    
    <xsl:template match="columns">
        <tr>
            <xsl:for-each select="column">
                <td style="text-align:center; width:{@size}em;">
                    <h4>
                        <xsl:value-of select="."/>
                    </h4>    
                </td>
            </xsl:for-each>
        </tr>
    </xsl:template>

    <xsl:template match="item">
        <tr>
            <xsl:for-each select="item_elem">
                <td>
                    <xsl:call-template name="line_break_html">
                        <xsl:with-param name="str" select = "."/>
                        <xsl:with-param name="line_length" select = "50"/>
                    </xsl:call-template>
                    <!-- <xsl:value-of select="."/> -->
                </td>
            </xsl:for-each>
        </tr>
    </xsl:template>
    
</xsl:stylesheet>


