<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:xalan="http://xml.apache.org/xslt"
    xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">
    
<xsl:import href="/com/sbrf/report/types/xsl/ReportCommonFunctions.xsl" />
<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

<xsl:template match="/root">
    <xsl:processing-instruction name="mso-application">progid="Excel.Sheet"</xsl:processing-instruction>
    <ss:Workbook> 
     <ss:Styles>
         <ss:Style ss:ID="Default" ss:Name="Normal">
            <ss:NumberFormat ss:Format="Standard"/>
         </ss:Style>
         <ss:Style ss:ID="header">
            <ss:Font ss:Size="12" ss:Bold="1"/>
            <ss:NumberFormat ss:Format="@"/>
         </ss:Style>
         <ss:Style ss:ID="bigHeader">
            <ss:Font ss:Size="14" ss:Bold="1"/>
            <ss:NumberFormat ss:Format="@"/>
         </ss:Style>
         <ss:Style ss:ID="integer">
            <ss:NumberFormat ss:Format="0"/>
         </ss:Style>
         <ss:Style ss:ID="bottomThin">
            <ss:Borders>
                <ss:Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
            </ss:Borders>
         </ss:Style>
         <ss:Style ss:ID="integerBottomThin">
            <ss:Borders>
                <ss:Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="1"/>
            </ss:Borders>
            <ss:NumberFormat ss:Format="0"/>
         </ss:Style>
         <ss:Style ss:ID="bottom">
            <ss:Borders>
                <ss:Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="2"/>
            </ss:Borders>
         </ss:Style>
         <ss:Style ss:ID="integerBottom">
            <ss:Borders>
                <ss:Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="2"/>
            </ss:Borders>
            <ss:NumberFormat ss:Format="0"/>
         </ss:Style>
         <ss:Style ss:ID="theader">
            <ss:Alignment ss:Horizontal="Left" ss:Vertical="Center" ss:WrapText="1"/>
            <ss:Borders>
                <ss:Border ss:Position="Bottom" ss:LineStyle="Continuous" ss:Weight="2"/>
                <ss:Border ss:Position="Top" ss:LineStyle="Continuous" ss:Weight="2"/>
            </ss:Borders>
            <ss:Font ss:Size="12" ss:Bold="1"/>
            <ss:NumberFormat ss:Format="@"/>
         </ss:Style>
         <ss:Style ss:ID="bold">
            <ss:Font ss:Size="10" ss:Bold="1"/>
            <ss:NumberFormat ss:Format="@"/>
         </ss:Style>
         <ss:Style ss:ID="string">
            <ss:NumberFormat ss:Format="@"/>
         </ss:Style>
     </ss:Styles>
     <ss:Worksheet ss:Name="rst">
         <xsl:apply-templates select="items">
                <xsl:with-param name="startDate" select="start"/>
                <xsl:with-param name="endDate" select="end"/>
         </xsl:apply-templates>
     </ss:Worksheet>
     </ss:Workbook>
</xsl:template>

<xsl:template match="items">
    <xsl:param name="startDate" select="''"/>
    <xsl:param name="endDate" select="''"/>
    <ss:Table>
        <xsl:choose>
            <xsl:when test="count(descendant::*) = 0">
                <ss:Row>
                    <ss:Cell ss:StyleID="header">
                        <ss:Data ss:Type="String">
                            <xsl:text>Информация, удовлетворяющая условиям поиска, отсутствует.</xsl:text>
                        </ss:Data>
                    </ss:Cell>
                </ss:Row>
                <ss:Row>
                    <ss:Cell ss:StyleID="header">
                        <ss:Data ss:Type="String">
                            <xsl:text>Проверьте введенные параметры отчета.</xsl:text>
                        </ss:Data>
                    </ss:Cell>
                </ss:Row>             
            </xsl:when>
            <xsl:otherwise>
            
                <xsl:apply-templates select="descendant::item[position() = 1]" mode="spacing"/>
                <ss:Column ss:AutoFitWidth="0" ss:Width="80"/>
                <ss:Column ss:AutoFitWidth="0" ss:Width="120"/>
                <ss:Column ss:AutoFitWidth="0" ss:Width="120"/>
                <ss:Column ss:AutoFitWidth="0" ss:Width="150"/>
                <ss:Row>
                    <ss:Cell>
                        <ss:Data ss:Type="String">
                            <xsl:text>Отчет *.rst</xsl:text>
                        </ss:Data>
                    </ss:Cell>
                </ss:Row>
                <ss:Row>
                    <ss:Cell>
                        <ss:Data ss:Type="String">
                            <xsl:text>в разрезе /БИК/Номер корр.счета/Номер расч.счета/Код спец. клиента/Номер Отделения/Дата платежа/Дата перечисления</xsl:text>
                        </ss:Data>
                    </ss:Cell>
                </ss:Row>
                <ss:Row>
                    <ss:Cell>
                        <ss:Data ss:Type="String">
                            <xsl:text>За период с </xsl:text>
                            <xsl:value-of select="$startDate"/>
                            <xsl:text>г. по </xsl:text>
                            <xsl:value-of select="$endDate"/>
                            <xsl:text>г.</xsl:text>
                        </ss:Data>
                    </ss:Cell>
                </ss:Row>
                <ss:Row>
                    <xsl:apply-templates select="descendant::item[position() = 1]" mode="head">
                    </xsl:apply-templates>
                    <ss:Cell  ss:StyleID="theader">
                        <ss:Data ss:Type="String">
                            <xsl:text>Кол-во документов</xsl:text>
                        </ss:Data>
                    </ss:Cell>
                    <ss:Cell ss:StyleID="theader">
                        <ss:Data ss:Type="String">
                            <xsl:text>Сумма платежей</xsl:text>
                        </ss:Data>
                    </ss:Cell>
                    <ss:Cell  ss:StyleID="theader">
                        <ss:Data ss:Type="String">
                            <xsl:text>Сумма комиссии с получателя</xsl:text>
                        </ss:Data>
                    </ss:Cell>
					<ss:Cell  ss:StyleID="theader">
                        <ss:Data ss:Type="String">
                            <xsl:text>Сумма комиссии с плательщика</xsl:text>
                        </ss:Data>
                    </ss:Cell>
                    <ss:Cell ss:StyleID="theader">
                        <ss:Data ss:Type="String">
                            <xsl:text>Сумма к перечислению</xsl:text>
                        </ss:Data>
                    </ss:Cell>
                </ss:Row>
                <xsl:apply-templates select="child::*" mode="print"/>
           </xsl:otherwise>
        </xsl:choose>
    </ss:Table>
</xsl:template>

<xsl:template match="item" mode="head">
    <xsl:if test="position() = 1">
        <xsl:apply-templates select="ancestor::*[name() !='root' and name() !='items']" mode="head"></xsl:apply-templates>            
    </xsl:if>
</xsl:template>

<xsl:template match="item" mode="spacing">
    <xsl:if test="position() = 1">
        <xsl:apply-templates select="ancestor::*[name() !='root' and name() !='items']" mode="spacing"></xsl:apply-templates>            
    </xsl:if>
</xsl:template>

<xsl:template match="bik|corr_acc|settle_acc|special_client_code|osb|filial|cashier|service_channel_name|payment_means_name|transfer_date|payment_date" mode="spacing">
        <xsl:choose>
            <xsl:when test="name() = 'bik'">
                <ss:Column ss:AutoFitWidth="0" ss:Width="200"/>
            </xsl:when>
            <xsl:when test="name() = 'settle_acc'">
                <ss:Column ss:AutoFitWidth="0" ss:Width="150"/>
            </xsl:when>
            <xsl:when test="name() = 'payment_date'">
                <ss:Column ss:AutoFitWidth="0" ss:Width="100"/>
            </xsl:when>
            <xsl:when test="name() = 'transfer_date'">
                <ss:Column ss:AutoFitWidth="0" ss:Width="100"/>
            </xsl:when>
            <xsl:when test="name() = 'osb'">
                <ss:Column ss:AutoFitWidth="0" ss:Width="40"/>
            </xsl:when>
			<xsl:when test="name() = 'filial'">
                <ss:Column ss:AutoFitWidth="0" ss:Width="60"/>
            </xsl:when>
			<xsl:when test="name() = 'cashier'">
                <ss:Column ss:AutoFitWidth="0" ss:Width="100"/>
            </xsl:when>
            <xsl:when test="name() = 'service_channel_name'">
                <ss:Column ss:AutoFitWidth="0" ss:Width="160"/>
            </xsl:when>
            <xsl:when test="name() = 'payment_means_name'">
                <ss:Column ss:AutoFitWidth="0" ss:Width="140"/>
            </xsl:when>
            <xsl:when test="name() = 'special_client_code'">
                <ss:Column ss:AutoFitWidth="0" ss:Width="70"/>
            </xsl:when>
        </xsl:choose>
</xsl:template>

<xsl:template match="bik|corr_acc|settle_acc|special_client_code|osb|filial|cashier|service_channel_name|payment_means_name|transfer_date|payment_date" mode="head">
        <xsl:choose>
            <xsl:when test="name() = 'bik'">
                <ss:Cell ss:StyleID="theader">
                    <ss:Data ss:Type="String">
                        <xsl:text>БИК\Корр.счет</xsl:text>
                    </ss:Data>
                </ss:Cell>
            </xsl:when>
            <xsl:when test="name() = 'settle_acc'">
                <ss:Cell ss:StyleID="theader">
                    <ss:Data ss:Type="String">
                        <xsl:text>Расчетный счет</xsl:text>
                    </ss:Data>
                </ss:Cell>
            </xsl:when>
            <xsl:when test="name() = 'payment_date'">
                <ss:Cell ss:StyleID="theader">
                    <ss:Data ss:Type="String">
                        <xsl:text>Дата платежа</xsl:text>
                    </ss:Data>
                </ss:Cell>
            </xsl:when>
            <xsl:when test="name() = 'transfer_date'">
                <ss:Cell ss:StyleID="theader">
                    <ss:Data ss:Type="String">
                        <xsl:text>Дата перечисления</xsl:text>
                    </ss:Data>
                </ss:Cell>
            </xsl:when>
            <xsl:when test="name() = 'osb'">
                <ss:Cell ss:StyleID="theader">
                    <ss:Data ss:Type="String">
                        <xsl:text>ОСБ</xsl:text>
                    </ss:Data>
                </ss:Cell>
            </xsl:when>
			<xsl:when test="name() = 'filial'">
                <ss:Cell ss:StyleID="theader">
                    <ss:Data ss:Type="String">
                        <xsl:text>ФИЛИАЛ</xsl:text>
                    </ss:Data>
                </ss:Cell>
            </xsl:when>
			<xsl:when test="name() = 'cashier'">
                <ss:Cell ss:StyleID="theader">
                    <ss:Data ss:Type="String">
                        <xsl:text>ОПЕРАТОР</xsl:text>
                    </ss:Data>
                </ss:Cell>
            </xsl:when>
            <xsl:when test="name() = 'service_channel_name'">
                <ss:Cell ss:StyleID="theader">
                    <ss:Data ss:Type="String">
                        <xsl:text>Канал обслуживания</xsl:text>
                    </ss:Data>
                </ss:Cell>
            </xsl:when>
            <xsl:when test="name() = 'payment_means_name'">
                <ss:Cell ss:StyleID="theader">
                    <ss:Data ss:Type="String">
                        <xsl:text>Платежное средство</xsl:text>
                    </ss:Data>
                </ss:Cell>
            </xsl:when>
            <xsl:when test="name() = 'special_client_code'">
                <ss:Cell ss:StyleID="theader">
                    <ss:Data ss:Type="String">
                        <xsl:text>Код спец. клиента</xsl:text>
                    </ss:Data>
                </ss:Cell>
            </xsl:when>
        </xsl:choose>
</xsl:template>

<xsl:template match="bik" mode="print">
    <xsl:apply-templates select="corr_acc" mode="print">
        <xsl:with-param name="bik" select="@value"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="corr_acc" mode="print">
    <xsl:param name="bik" select="''"/>

    <xsl:apply-templates select="child::*" mode="print">
        <xsl:with-param name="bik">
            <xsl:value-of select="$bik"/>
              <xsl:text>\</xsl:text>
            <xsl:call-template name="acc-format">
                <xsl:with-param name="acc" select="@value"/>
            </xsl:call-template>
        </xsl:with-param>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="settle_acc" mode="print">
    <xsl:param name="bik" select="''"/>
    
    <xsl:apply-templates select="child::*" mode="print">
        <xsl:with-param name="bik" select="$bik"/>
        <xsl:with-param name="settle_acc" select="@value"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="special_client_code" mode="print">
    <xsl:param name="bik" select="''"/>
    <xsl:param name="settle_acc" select="''"/>
    
    <xsl:choose>
        <xsl:when test="position() = 1">
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="bik" select="$bik"/>
                <xsl:with-param name="settle_acc" select="$settle_acc"/>
                <xsl:with-param name="special_client_code" select="@value"/>
            </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="special_client_code" select="@value"/>
            </xsl:apply-templates>
        </xsl:otherwise>
    </xsl:choose>    
</xsl:template>

<xsl:template match="osb" mode="print">
    <xsl:param name="bik" select="''"/>
    <xsl:param name="settle_acc" select="''"/>
    <xsl:param name="special_client_code" select="''"/>
    
    <xsl:choose>
        <xsl:when test="position() = 1">
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="bik" select="$bik"/>
                <xsl:with-param name="settle_acc" select="$settle_acc"/>
                <xsl:with-param name="special_client_code" select="$special_client_code"/>
                <xsl:with-param name="osb" select="@value"/>
            </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="osb" select="@value"/>
            </xsl:apply-templates>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template match="filial" mode="print">
    <xsl:param name="bik" select="''"/>
    <xsl:param name="settle_acc" select="''"/>
    <xsl:param name="special_client_code" select="''"/>
	<xsl:param name="osb" select="''"/>
    
    <xsl:choose>
        <xsl:when test="position() = 1">
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="bik" select="$bik"/>
                <xsl:with-param name="settle_acc" select="$settle_acc"/>
                <xsl:with-param name="special_client_code" select="$special_client_code"/>
                <xsl:with-param name="osb" select="$osb"/>
				<xsl:with-param name="filial" select="@value"/>
            </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="filial" select="@value"/>
            </xsl:apply-templates>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template match="cashier" mode="print">
    <xsl:param name="bik" select="''"/>
    <xsl:param name="settle_acc" select="''"/>
    <xsl:param name="special_client_code" select="''"/>
	<xsl:param name="osb" select="''"/>
	<xsl:param name="filial" select="''"/>
    
    <xsl:choose>
        <xsl:when test="position() = 1">
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="bik" select="$bik"/>
                <xsl:with-param name="settle_acc" select="$settle_acc"/>
                <xsl:with-param name="special_client_code" select="$special_client_code"/>
                <xsl:with-param name="osb" select="$osb"/>
				<xsl:with-param name="filial" select="$filial"/>
				<xsl:with-param name="cashier" select="@value"/>
            </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="cashier" select="@value"/>
            </xsl:apply-templates>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<xsl:template match="service_channel_name" mode="print">
    <xsl:param name="bik" select="''"/>
    <xsl:param name="settle_acc" select="''"/>
    <xsl:param name="special_client_code" select="''"/>
    <xsl:param name="osb" select="''"/>
	<xsl:param name="filial" select="''"/>
	<xsl:param name="cashier" select="''"/>
    
    <xsl:choose>
        <xsl:when test="position() = 1">
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="bik" select="$bik"/>
                <xsl:with-param name="settle_acc" select="$settle_acc"/>
                <xsl:with-param name="special_client_code" select="$special_client_code"/>
                <xsl:with-param name="osb" select="$osb"/>
				<xsl:with-param name="filial" select="$filial"/>
				<xsl:with-param name="cashier" select="$cashier"/>
                <xsl:with-param name="service_channel_name" select="@value"/>
            </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="service_channel_name" select="@value"/>
            </xsl:apply-templates>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<xsl:template match="payment_means_name" mode="print">
    <xsl:param name="bik" select="''"/>
    <xsl:param name="settle_acc" select="''"/>
    <xsl:param name="special_client_code" select="''"/>
    <xsl:param name="osb" select="''"/>
	<xsl:param name="filial" select="''"/>
	<xsl:param name="cashier" select="''"/>
    <xsl:param name="service_channel_name" select="''"/>
    
    <xsl:choose>
        <xsl:when test="position() = 1">
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="bik" select="$bik"/>
                <xsl:with-param name="settle_acc" select="$settle_acc"/>
                <xsl:with-param name="special_client_code" select="$special_client_code"/>
                <xsl:with-param name="osb" select="$osb"/>
				<xsl:with-param name="filial" select="$filial"/>
				<xsl:with-param name="cashier" select="$cashier"/>
                <xsl:with-param name="service_channel_name" select="$service_channel_name"/>
                <xsl:with-param name="payment_means_name" select="@value"/>
                
            </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="payment_means_name" select="@value"/>
            </xsl:apply-templates>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>



<xsl:template match="payment_date" mode="print">
    <xsl:param name="bik" select="''"/>
    <xsl:param name="settle_acc" select="''"/>
    <xsl:param name="special_client_code" select="''"/>
    <xsl:param name="osb" select="''"/>
	<xsl:param name="filial" select="''"/>
	<xsl:param name="cashier" select="''"/>
    <xsl:param name="service_channel_name" select="''"/>
    <xsl:param name="payment_means_name" select="''"/>
    
    <xsl:choose>
        <xsl:when test="position() = 1">
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="bik" select="$bik"/>
                <xsl:with-param name="settle_acc" select="$settle_acc"/>
                <xsl:with-param name="special_client_code" select="$special_client_code"/>
                <xsl:with-param name="osb" select="$osb"/>
				<xsl:with-param name="filial" select="$filial"/>
				<xsl:with-param name="cashier" select="$cashier"/>
                <xsl:with-param name="service_channel_name" select="$service_channel_name"/>
                <xsl:with-param name="payment_means_name" select="$payment_means_name"/>
                <xsl:with-param name="payment_date" select="@value"/>
            </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="payment_date" select="@value"/>
            </xsl:apply-templates>
        </xsl:otherwise>
    </xsl:choose>    
    
</xsl:template>

<xsl:template match="transfer_date" mode="print">
    <xsl:param name="bik" select="''"/>
    <xsl:param name="settle_acc" select="''"/>
    <xsl:param name="special_client_code" select="''"/>
    <xsl:param name="osb" select="''"/>
	<xsl:param name="filial" select="''"/>
	<xsl:param name="cashier" select="''"/>
    <xsl:param name="service_channel_name" select="''"/>
    <xsl:param name="payment_date" select="''"/>
    <xsl:param name="payment_means_name" select="''"/>
    
    <xsl:choose>
        <xsl:when test="position() = 1">
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="bik" select="$bik"/>
                <xsl:with-param name="settle_acc" select="$settle_acc"/>
                <xsl:with-param name="special_client_code" select="$special_client_code"/>
                <xsl:with-param name="osb" select="$osb"/>
				<xsl:with-param name="filial" select="$filial"/>
				<xsl:with-param name="cashier" select="$cashier"/>
                <xsl:with-param name="service_channel_name" select="$service_channel_name"/>
                <xsl:with-param name="payment_means_name" select="$payment_means_name"/>
                <xsl:with-param name="payment_date" select="$payment_date"/>
                <xsl:with-param name="transfer_date" select="@value"/>
            </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="child::*" mode="print">
                <xsl:with-param name="transfer_date" select="@value"/>
            </xsl:apply-templates>
        </xsl:otherwise>
    </xsl:choose>    
</xsl:template>

<xsl:template match="item" mode="print">
    <xsl:param name="bik" select="''"/>
    <xsl:param name="settle_acc" select="''"/>
    <xsl:param name="special_client_code" select="''"/>
    <xsl:param name="osb" select="''"/>
	<xsl:param name="filial" select="''"/>
	<xsl:param name="cashier" select="''"/>
    <xsl:param name="service_channel_name" select="''"/>
    <xsl:param name="payment_means_name" select="''"/>
    <xsl:param name="payment_date" select="''"/>
    <xsl:param name="transfer_date" select="''"/>
    <ss:Row>
        <xsl:if test="count(ancestor::bik) != 0">
            <ss:Cell ss:StyleID="string">
                <ss:Data ss:Type="String">
                    <xsl:value-of select="$bik"/>
                </ss:Data>
            </ss:Cell>
        </xsl:if>
        <xsl:if test="count(ancestor::settle_acc) != 0">
            <ss:Cell ss:StyleID="string">
                <ss:Data ss:Type="String">
                    <xsl:call-template name="acc-format">
                        <xsl:with-param name="acc" select="$settle_acc"/>
                    </xsl:call-template>
                </ss:Data>
            </ss:Cell>
        </xsl:if>
        <xsl:if test="count(ancestor::special_client_code) != 0">
            <ss:Cell ss:StyleID="string">
                <ss:Data ss:Type="String">
                    <xsl:value-of select="$special_client_code"/>
                </ss:Data>
            </ss:Cell>
        </xsl:if>
        <xsl:if test="count(ancestor::osb) != 0">
            <ss:Cell ss:StyleID="string">
                <ss:Data ss:Type="String">
                    <xsl:value-of select="$osb"/>
                </ss:Data>
            </ss:Cell>
        </xsl:if>
		<xsl:if test="count(ancestor::filial) != 0">
            <ss:Cell ss:StyleID="string">
                <ss:Data ss:Type="String">
                    <xsl:value-of select="$filial"/>
                </ss:Data>
            </ss:Cell>
        </xsl:if>
		<xsl:if test="count(ancestor::cashier) != 0">
            <ss:Cell ss:StyleID="string">
                <ss:Data ss:Type="String">
                    <xsl:value-of select="$cashier"/>
                </ss:Data>
            </ss:Cell>
        </xsl:if>
        <xsl:if test="count(ancestor::service_channel_name) != 0">
            <ss:Cell ss:StyleID="string">
                <ss:Data ss:Type="String">
                    <xsl:value-of select="$service_channel_name"/>
                </ss:Data>
            </ss:Cell>
        </xsl:if>
        <xsl:if test="count(ancestor::payment_means_name) != 0">
            <ss:Cell ss:StyleID="string">
                <ss:Data ss:Type="String">
                    <xsl:value-of select="$payment_means_name"/>
                </ss:Data>
            </ss:Cell>
        </xsl:if>
        <xsl:if test="count(ancestor::payment_date) != 0">
            <ss:Cell ss:StyleID="string">
                <ss:Data ss:Type="String">
                    <xsl:value-of select="$payment_date"/>
                </ss:Data>
            </ss:Cell>
        </xsl:if>
        <xsl:if test="count(ancestor::transfer_date) != 0">
            <ss:Cell>
                <ss:Data ss:Type="String">
                    <xsl:value-of select="$transfer_date"/>
                </ss:Data>
            </ss:Cell>
        </xsl:if>
        <ss:Cell ss:StyleID="integer">
            <ss:Data ss:Type="Number">
                <xsl:value-of select="counter"/>
            </ss:Data>
        </ss:Cell>
        <ss:Cell>
          <ss:Data ss:Type="Number">
            <xsl:call-template name="sum-format-nospace">
                <xsl:with-param name="s" select="sum"/>
                <xsl:with-param name="pad" select="1"/>
            </xsl:call-template>
          </ss:Data>  
        </ss:Cell>
        <ss:Cell>
          <ss:Data ss:Type="Number">
            <xsl:call-template name="sum-format-nospace">
                <xsl:with-param name="s" select="serviceComm"/>
                <xsl:with-param name="pad" select="1"/>
            </xsl:call-template>
          </ss:Data>  
        </ss:Cell>
		<ss:Cell>
          <ss:Data ss:Type="Number">
            <xsl:call-template name="sum-format-nospace">
                <xsl:with-param name="s" select="payerComm"/>
                <xsl:with-param name="pad" select="1"/>
            </xsl:call-template>
          </ss:Data>  
        </ss:Cell>
        <ss:Cell>
          <ss:Data ss:Type="Number">
            <xsl:call-template name="sum-format-nospace">
                <xsl:with-param name="s" select="sum - serviceComm"/>
                <xsl:with-param name="pad" select="1"/>
            </xsl:call-template>
          </ss:Data>  
        </ss:Cell>
    </ss:Row>
</xsl:template>

<xsl:template match="total_summary" mode="print">
    <ss:Row>
        <xsl:choose>
            <xsl:when test="name() = 'total_summary'">
                <xsl:if test="count(parent::items) = 0">
                    <ss:Cell ss:StyleID="bottomThin">
                      <ss:Data ss:Type="String">
                        <xsl:text>Итого</xsl:text>
                      </ss:Data>  
                    </ss:Cell> 
                </xsl:if>
                <xsl:apply-templates select="parent::*" mode="summary_spacing">
                    <xsl:with-param name="padding" select="1"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:when test="name() = 'summary'">
                <ss:Cell ss:StyleID="bottomThin">
                  <ss:Data ss:Type="String">
                  </ss:Data>  
                </ss:Cell> 
                <xsl:apply-templates select="parent::*" mode="summary_spacing">
                    <xsl:with-param name="padding" select ="0"/>
                </xsl:apply-templates>
            </xsl:when>
        </xsl:choose>
        <xsl:if test="count(ancestor::settle_acc) > 0 and  count(ancestor::corr_acc) > 0">
              <ss:Cell ss:StyleID="bottomThin">
                  <ss:Data ss:Type="String">
                  </ss:Data>  
              </ss:Cell> 
        </xsl:if>
        <ss:Cell ss:StyleID="integerBottomThin">
            <ss:Data ss:Type="Number">
                <xsl:value-of select="counter"/>
            </ss:Data>
        </ss:Cell>
        <ss:Cell ss:StyleID="bottomThin">
          <ss:Data ss:Type="Number">
            <xsl:call-template name="sum-format-nospace">
                <xsl:with-param name="s" select="sum"/>
                <xsl:with-param name="pad" select="1"/>
            </xsl:call-template>
          </ss:Data>  
        </ss:Cell>
        <ss:Cell ss:StyleID="bottomThin">
          <ss:Data ss:Type="Number">
            <xsl:call-template name="sum-format-nospace">
                <xsl:with-param name="s" select="serviceComm"/>
                <xsl:with-param name="pad" select="1"/>
            </xsl:call-template>
          </ss:Data>  
        </ss:Cell>
		<ss:Cell ss:StyleID="bottomThin">
          <ss:Data ss:Type="Number">
            <xsl:call-template name="sum-format-nospace">
                <xsl:with-param name="s" select="payerComm"/>
                <xsl:with-param name="pad" select="1"/>
            </xsl:call-template>
          </ss:Data>  
        </ss:Cell>
        <ss:Cell ss:StyleID="bottomThin">
          <ss:Data ss:Type="Number">
            <xsl:call-template name="sum-format-nospace">
                <xsl:with-param name="s" select="sum - serviceComm"/>
                <xsl:with-param name="pad" select="1"/>
            </xsl:call-template>
          </ss:Data>  
        </ss:Cell>
    </ss:Row>
</xsl:template>

<xsl:template match="items|bik|corr_acc|settle_acc|special_client_code|osb|filial|cashier|service_channel_name|payment_means_name|payment_date|transfer_date" mode="summary_spacing">
    <xsl:param name="padding" select="''"/>
    <xsl:if test="$padding &lt; 1">
        <ss:Cell ss:StyleID="bottomThin">
           <ss:Data ss:Type="String">
           </ss:Data>  
        </ss:Cell>
    </xsl:if>
    <xsl:apply-templates select="child::*[name() != 'item' and position() = 1]" mode="summary_spacing">
        <xsl:with-param name="padding" select="$padding - 1"/>
    </xsl:apply-templates>        
    
</xsl:template>

<xsl:template match="*">
</xsl:template>
    
</xsl:stylesheet>