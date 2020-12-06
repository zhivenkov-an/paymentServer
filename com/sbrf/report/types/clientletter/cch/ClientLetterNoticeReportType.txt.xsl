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
                <xsl:text> </xsl:text>
            </xsl:when>
            <xsl:when test="@type='sbol'">
                <xsl:text>по системе Сбербанк Онл@йн </xsl:text>
            </xsl:when>
            <xsl:when test="@type='selfServ'">
                <xsl:text>через терминал </xsl:text>
            </xsl:when>
        </xsl:choose>
        
        <xsl:text> по следующим реквизитам: БИК </xsl:text>
        <xsl:value-of select="bik"/>
        <xsl:text>}, расчетный счет </xsl:text>
        <xsl:value-of select="settle_acc"/>
        <xsl:text>, наименование получателя –  </xsl:text>
        <xsl:value-of select="receiver_name"/>
        <xsl:text> были отнесены на счет невыясненных платежей, по причине неверных реквизитов получателя в платежном документе.</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#9;Просим Вас обратиться в ближайшее подразделение Московского банка ОАО “Сбербанк России” для уточнения реквизитов или возврата данной суммы. При себе иметь паспорт, квитанцию об оплате, уточненные реквизиты.</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#9;В случае отсутствия обращения с Вашей стороны в течение двух месяцев от даты регистрации данного письма, Банк оставляет за собой право на перечисление вышеуказанных денежных средств по самостоятельно уточненным реквизитам. При невозможности самостоятельного уточнения реквизитов и отсутствии Вашего обращения денежные средства будут списаны в доход Банка.</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:value-of select="assignerPosition"/>
        <xsl:text>&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;</xsl:text>
        <xsl:value-of select="assigner"/>
     </xsl:template>
</xsl:stylesheet>
