<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
    
    <xsl:import href="/com/sbrf/report/types/xsl/ReportCommonFunctions.xsl" />
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:template match="root">
    <xsl:processing-instruction name="mso-application">progid="Excel.Sheet"</xsl:processing-instruction>
       <ss:Workbook> 
       
         <ss:Styles>
          <ss:Style ss:ID="Default" ss:Name="Normal">
            <ss:NumberFormat ss:Format="Standard"/>
          </ss:Style>
          <ss:Style ss:ID="header">
            <ss:Font ss:Size="12" ss:Bold="1"/>
          </ss:Style>
        </ss:Styles>
        
        <ss:Worksheet ss:Name="Отчет">
            <ss:Table>
                <xsl:apply-templates select = "header"/>
                <xsl:apply-templates select = "items"/>
                <xsl:apply-templates select = "error"/>
            </ss:Table>
        </ss:Worksheet>
        
      </ss:Workbook>
    </xsl:template>
    
    <xsl:template match="header">
        <ss:Column ss:Width="200"/>
        <ss:Row>
            <ss:Cell ss:StyleID="header"><ss:Data ss:Type="String"><xsl:value-of select="."/></ss:Data></ss:Cell>
        </ss:Row>
    </xsl:template>
    
    <xsl:template match="error">
        <ss:Row>
            <ss:Cell ss:StyleID="header"><ss:Data ss:Type="String"><xsl:text>Ошибка: </xsl:text></ss:Data></ss:Cell>
        </ss:Row>
        <ss:Row>
            <ss:Cell><ss:Data ss:Type="String"><xsl:value-of select="message"/></ss:Data></ss:Cell>
        </ss:Row>
    </xsl:template>
    
    <xsl:template match="items">
        <ss:Row>
            <ss:Cell ss:StyleID="header">
                <ss:Data ss:Type="String">
                    <xsl:text>Найдено </xsl:text>
                    <xsl:value-of select="summary/counter"/>
                    <xsl:text> записей.</xsl:text>
                </ss:Data>
            </ss:Cell>
        </ss:Row>
        <xsl:if test="summary/message != ''">
            <ss:Row>
                <ss:Cell ss:StyleID="header">
                    <ss:Data ss:Type="String">            
                            <xsl:value-of select="summary/message"/>
                    </ss:Data>
                </ss:Cell>
            </ss:Row>
            
            <ss:Row>
                <ss:Cell ss:StyleID="header">
                    <ss:Data ss:Type="String">            
                            <xsl:text>Максимальное количесво отображаемых записей </xsl:text>
                            <xsl:value-of select="summary/maxrows"/>
                            <xsl:text>.</xsl:text>
            	   </ss:Data>
                </ss:Cell>
            </ss:Row>
        </xsl:if>
        <ss:Row></ss:Row>    
        <xsl:apply-templates select="columns"/>
        <xsl:apply-templates select="item"/>
    </xsl:template>
    
    <xsl:template match="columns">
        <ss:Row>
            <xsl:for-each select="column">
                <ss:Cell ss:StyleID="header">
                    <ss:Data ss:Type="String">    
                         <xsl:value-of select="."/>
                    </ss:Data>    
                </ss:Cell>
            </xsl:for-each>
        </ss:Row>
    </xsl:template>

    <xsl:template match="item">
        <ss:Row>
            <xsl:for-each select="item_elem">
                <ss:Cell>
                    <ss:Data ss:Type="String">
                        <xsl:value-of select = "."/>
                    </ss:Data>
                </ss:Cell>
            </xsl:for-each>
        </ss:Row>
    </xsl:template>
</xsl:stylesheet>
