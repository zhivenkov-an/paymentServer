<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:xalan="http://xml.apache.org/xslt">
    
    <xsl:import href="/com/sbrf/report/types/xsl/ReportCommonFunctions.xsl" />
    <xsl:output method="text" encoding="cp1251"/> 
    
    <xsl:template match = "/root">
        <xsl:choose>
            <xsl:when test="count(/root/items/item) = 0">
                <xsl:text>Информация, удовлетворяющая условиям поиска, отсутствует. </xsl:text>
                <xsl:text>&#x0A;</xsl:text>
                <xsl:text>Проверьте введенные параметры отчета </xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="items/item"/>
            </xsl:otherwise>
        </xsl:choose>
     </xsl:template>       
     
     <xsl:template match="item">
        <xsl:value-of select="payer_info"/>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:value-of select="payer_address"/>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>О подтверждении перечисления</xsl:text>
        <xsl:text>&#x0A;</xsl:text> 
        <xsl:text>денежных средств</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>Уважаемый плательщик!</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#9;Настоящим сообщаем, что денежные средства в размере </xsl:text> 
        <xsl:value-of select="sum_rub"/>
        <xsl:text> руб. </xsl:text>
        <xsl:value-of select="sum_cent"/>
        <xsl:text> коп. по платежу, совершенному </xsl:text>
        <xsl:value-of select="payment_date"/>
        <xsl:text>г. </xsl:text>
        <xsl:choose>
            <xsl:when test="@type='osb'">
                <xsl:text>в ДО </xsl:text>
                <xsl:value-of select="osb_old"/>
                <xsl:text>/</xsl:text>
                <xsl:value-of select="filial"/>
                <xsl:text>, оператор </xsl:text>
                <xsl:value-of select="cashier"/>
                <xsl:text>, операция </xsl:text>
                <xsl:value-of select="document_number"/>
                <xsl:text>, </xsl:text>
            </xsl:when>
            <xsl:when test="@type='sbol'">
                <xsl:text>по системе Сбербанк Онл@йн, </xsl:text>
            </xsl:when>
            <xsl:when test="@type='selfServ'">
                <xsl:text>через терминал, </xsl:text>
            </xsl:when>
        </xsl:choose>
        
        <xsl:text>были перечислены </xsl:text>
        <xsl:value-of select="transfer_date"/>
        <xsl:text>г. платежным поручением № </xsl:text>
        <xsl:value-of select="order_num"/>
        <xsl:if test="single_pay_order = 'no'">
            <xsl:text> на общую сумму </xsl:text>
            <xsl:value-of select="pay_order_sum_rub"/>
            <xsl:text> руб. </xsl:text>
            <xsl:value-of select="pay_order_sum_cent"/>
            <xsl:text> коп.</xsl:text>
        </xsl:if>
        <xsl:text> по следующим реквизитам:</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#9;БИК&#9;</xsl:text>
        <xsl:value-of select="bik"/>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#9;к/с&#9;</xsl:text>
        <xsl:value-of select="corr_acc"/>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#9;р/с&#9;</xsl:text>
        <xsl:value-of select="settle_acc"/>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:if test="inn != '' or kpp != ''">
            <xsl:text>&#9;</xsl:text>
        </xsl:if>
        <xsl:if test="inn != ''">
            <xsl:text>ИНН</xsl:text>
        </xsl:if>
        <xsl:if test="inn != '' and kpp != ''">
            <xsl:text>/</xsl:text>
        </xsl:if>
        <xsl:if test="kpp != ''">
            <xsl:text>КПП</xsl:text>
        </xsl:if>
        <xsl:if test="inn != '' or kpp != ''">
            <xsl:text>&#9;</xsl:text>
        </xsl:if>
        <xsl:if test="inn != ''">
            <xsl:value-of select="inn"/>
        </xsl:if>
        <xsl:if test="inn != '' and kpp != ''">
            <xsl:text>/</xsl:text>
        </xsl:if>
        <xsl:if test="kpp != ''">
            <xsl:value-of select="kpp"/>
        </xsl:if>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:if test="kbk != ''">
            <xsl:text>&#9;КБК&#9;</xsl:text>
            <xsl:value-of select="kbk"/>
        </xsl:if>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:if test="okato != ''">
            <xsl:text>&#9;ОКТО&#9;</xsl:text>
            <xsl:value-of select="okato"/>
        </xsl:if>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#9;Наименование получателя: </xsl:text>
        <xsl:value-of select="receiver_name"/>
        <xsl:text>&#x0A;</xsl:text>
        
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:value-of select="assignerPosition"/>
        <xsl:text>&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;</xsl:text>
        <xsl:value-of select="assigner"/>
     </xsl:template>
</xsl:stylesheet>
