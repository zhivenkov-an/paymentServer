<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:xalan="http://xml.apache.org/xslt">
    
    <xsl:import href="/com/sbrf/report/types/xsl/ReportCommonFunctions.xsl" />
    <xsl:output method="text" encoding="cp1251"/> 
    
    <xsl:template match = "/root">
        <xsl:choose>
            <xsl:when test="count(/root/items) = 0">
                <xsl:text>Тестовый отчёт Информация, удовлетворяющая условиям поиска, отсутствует. </xsl:text>
                <xsl:text>&#x0D;</xsl:text>
                <xsl:text>Проверьте введенные параметры отчета </xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select = "items/item"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match = "item">
        <xsl:text>; СУММА: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "sum"/>            
        </xsl:call-template>
        <xsl:text>; ИНН: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "inn"/>            
        </xsl:call-template>
    </xsl:template>
</xsl:stylesheet>       