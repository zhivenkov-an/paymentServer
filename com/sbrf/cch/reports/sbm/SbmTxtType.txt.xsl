<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:xalan="http://xml.apache.org/xslt">
    
    <xsl:import href="/com/sbrf/report/types/xsl/ReportCommonFunctions.xsl" />
    <xsl:output method="text" encoding="cp1251"/> 
    
    <xsl:template match = "/root">
        <xsl:choose>
            <xsl:when test="count(/root/items) = 0">
                <xsl:text>Информация, удовлетворяющая условиям поиска, отсутствует. </xsl:text>
                <xsl:text>&#x0D;</xsl:text>
                <xsl:text>Проверьте введенные параметры отчета </xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select = "items/item"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match = "item">
	    <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "totalCount"/>            
        </xsl:call-template>
	    <xsl:text>| Дата платежа:</xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "payment_date"/>            
        </xsl:call-template>
		<xsl:text>|#</xsl:text>
		<xsl:text>| ОСБ:</xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "osb"/>            
        </xsl:call-template>
		<xsl:text>|00000</xsl:text>
		<xsl:text>| кассир:</xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "cashier"/>            
        </xsl:call-template>
		<xsl:text>| филиал:</xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "filial"/>            
        </xsl:call-template>
        <xsl:text>| СУММА 1:</xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "sum"/>            
        </xsl:call-template>
		<xsl:text>| СУММА 2:</xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "sum"/>            
        </xsl:call-template>
		<xsl:text>|000000000000000</xsl:text>
		<xsl:text>| СУММА к перечислению:</xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "sum_service"/>            
        </xsl:call-template>
		<xsl:text>|0|0</xsl:text>
		<xsl:text>| спецклиент: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "special_client_code"/>            
        </xsl:call-template>
		<xsl:text>| балансовый счет: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "tel"/>            
        </xsl:call-template>
		<xsl:text>| бик: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "bik"/>            
        </xsl:call-template>
		<xsl:text>| кор.счет: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "corr_acc"/>            
        </xsl:call-template>
		<xsl:text>     | расч.счет: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "settle_acc"/>            
        </xsl:call-template>
		<xsl:text>|06|1</xsl:text>
        <xsl:text>| ИНН: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "inn"/>            
        </xsl:call-template>
		<xsl:text>| идентификация: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "payer_info"/>            
        </xsl:call-template>		
		<xsl:text>| тип платежа: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "payment_kinds_code"/>            
        </xsl:call-template>		
		<xsl:text>|0000</xsl:text>
		<xsl:text>| Номер организации: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "receiver_number"/>            
        </xsl:call-template>
		<xsl:text>| составное поле с номером платежки: </xsl:text>        
		<xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "document_info"/>            
        </xsl:call-template>		
		<xsl:text>|000000000</xsl:text>
        <xsl:text>| Дополнительные реквизиты: </xsl:text>
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select = "additional_requisites"/>            
        </xsl:call-template>		
		<xsl:text>;</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
    </xsl:template>	
</xsl:stylesheet>       