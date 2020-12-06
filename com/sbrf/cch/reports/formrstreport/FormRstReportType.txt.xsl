<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" xmlns:xalan="http://xml.apache.org/xslt">

    <xsl:import href="/com/sbrf/report/types/xsl/ReportCommonFunctions.xsl" />
    <xsl:output method="text" encoding="cp866" />
        <xsl:template match="/root">
        <xsl:choose>
            <xsl:when test="count(/root/items/bik) + count(/root/items/osb) + count(/root/items/settle_acc) + count(/root/items/payment_date) + count(/root/items/transfer_date) + count(/root/items/special_client_code) + count(/root/items/service_channel_name) + count(/root/items/payment_means_name) + count(/root/items/total_summary) = 0">
                <xsl:text>Информация, удовлетворяющая условиям поиска, отсутствует. </xsl:text>
                <xsl:text>&#x0A;</xsl:text>
                <xsl:text>Проверьте введенные параметры отчета </xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>Отчет *.rst в разрезе </xsl:text>
                <xsl:value-of select="grouped" />
                <xsl:text>&#x0A;&#x0A;</xsl:text>
                <xsl:text>за период с </xsl:text>
                <xsl:value-of select="start" />
                <xsl:text> по </xsl:text>
                <xsl:value-of select="end" />
                <xsl:text>&#x0A;</xsl:text>
                <xsl:apply-templates select="items">
                            <xsl:with-param name="start"
                                select="start" />
                            <xsl:with-param name="end"
                                select="end" />
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Определение какие элементы присутствуют -->
    
    <xsl:variable name="bikCount" select="count(//bik)" />
    <xsl:variable name="settleCount" select="count(//settle_acc)" />
    <xsl:variable name="specCount" select="count(//special_client_code)" />
    <xsl:variable name="osbCount" select="count(//osb)" />
    <xsl:variable name="serviceCount" select="count(//service_channel_name)" />
    <xsl:variable name="paymeansCount" select="count(//payment_means_name)" />
    <xsl:variable name="paymentCount" select="count(//payment_date)" />
    <xsl:variable name="transferCount" select="count(//transfer_date)" />
    
     <!-- Ширины полей форматирования -->

      <xsl:variable name="bikPad" select="9"/>
      <xsl:variable name="corrPad" select="24"/>
      <xsl:variable name="settlePad" select="23"/>
      <xsl:variable name="specPad" select="9"/>
      <xsl:variable name="osbPad" select="4"/>
      <xsl:variable name="servicePad" select="27"/>
      <xsl:variable name="paymeansPad" select="17"/>
      <xsl:variable name="paymentPad" select="12"/>
      <xsl:variable name="operPad" select="12"/>
      <xsl:variable name="countPad" select="9"/>
      <xsl:variable name="sumPad" select="19"/>
      <xsl:variable name="commissionPad" select="19"/>
      <xsl:variable name="transferPad" select="24"/>
    	
    
    <xsl:template match="items">
        <xsl:param name="start" select="''" />
        <xsl:param name="end" select="''" />
        <!-- Рисование таблички -->
        
            <xsl:if test="$bikCount!=0">
                <xsl:text><![CDATA[┌───────────────────────────────────┬]]></xsl:text>
            </xsl:if>
            <xsl:if test="$settleCount!=0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0">
                        <xsl:text><![CDATA[┌────────────────────────┬]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[────────────────────────┬]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$specCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0">
                        <xsl:text><![CDATA[┌──────────┬]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[──────────┬]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$osbCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0">
                        <xsl:text><![CDATA[┌─────┬]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[─────┬]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$serviceCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0">
                        <xsl:text><![CDATA[┌────────────────────────────┬]]></xsl:text>
                      </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[────────────────────────────┬]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$paymeansCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount!= 0">
                        <xsl:text><![CDATA[┌──────────────────┬]]></xsl:text>
                      </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[──────────────────┬]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$paymentCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0">
                        <xsl:text><![CDATA[┌─────────────┬]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[─────────────┬]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$transferCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0 and $paymentCount=0">
                        <xsl:text><![CDATA[┌─────────────┬]]></xsl:text>
                      </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[─────────────┬]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $paymentCount=0 and $transferCount=0 and $serviceCount=0 and $paymeansCount=0">
                        <xsl:text><![CDATA[┌───────────┬──────────────────┬──────────────────┬──────────────────────┐]]>&#x0A;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                        <xsl:text><![CDATA[───────────┬──────────────────┬──────────────────┬──────────────────────┐]]>&#x0A;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="$bikCount!=0">
                <xsl:text><![CDATA[│           БИК/Корр.счет           │]]></xsl:text>
            </xsl:if>
            <xsl:if test="$settleCount!=0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0">
                        <xsl:text><![CDATA[│      Расчетный счет    │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[      Расчетный счет    │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$specCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0">
                        <xsl:text><![CDATA[│ Код спец.│]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[ Код спец.│]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$osbCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0">
                        <xsl:text><![CDATA[│ ОСБ │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[ ОСБ │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$serviceCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0">
                        <xsl:text><![CDATA[│            Канал           │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[            Канал           │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$paymeansCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0">
                        <xsl:text><![CDATA[│     Платежное    │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[     Платежное    │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            
            <xsl:if test="$paymentCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0">
                        <xsl:text><![CDATA[│    Дата     │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[    Дата     │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$transferCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0 and $paymentCount=0">
                        <xsl:text><![CDATA[│    Дата     │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[    Дата     │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0 and $paymentCount=0 and $transferCount=0">
                        <xsl:text><![CDATA[│   Кол-во  │  Сумма платежей  │  Сумма комиссии  │ Сумма к перечислению │]]>&#x0A;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                        <xsl:text><![CDATA[   Кол-во  │  Сумма платежей  │  Сумма комиссии  │ Сумма к перечислению │]]>&#x0A;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="$bikCount!=0">
                <xsl:text><![CDATA[│                                   │]]></xsl:text>
            </xsl:if>
            <xsl:if test="$settleCount!=0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0">
                        <xsl:text><![CDATA[│                        │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[                        │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$specCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0">
                        <xsl:text><![CDATA[│  клиента │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[  клиента │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$osbCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0">
                        <xsl:text><![CDATA[│     │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[     │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$serviceCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0">
                        <xsl:text><![CDATA[│        обслуживания        │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[        обслуживания        │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$paymeansCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0">
                        <xsl:text><![CDATA[│     средство     │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[     средство     │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            
            <xsl:if test="$paymentCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0">
                        <xsl:text><![CDATA[│   платежа   │]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[   платежа   │]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$transferCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0 and $paymentCount=0">
                        <xsl:text><![CDATA[│ перечисления│]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[ перечисления│]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0 and $paymentCount=0 and $transferCount=0">
                        <xsl:text><![CDATA[│ документов│                  │                  │                      │]]>&#x0A;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                        <xsl:text><![CDATA[ документов│                  │                  │                      │]]>&#x0A;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="$bikCount!=0">
                <xsl:text><![CDATA[├───────────────────────────────────┴]]></xsl:text>
            </xsl:if>
            <xsl:if test="$settleCount!=0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0">
                        <xsl:text><![CDATA[├────────────────────────┴]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[────────────────────────┴]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$specCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0">
                        <xsl:text><![CDATA[├──────────┴]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[──────────┴]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$osbCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0">
                        <xsl:text><![CDATA[├─────┴]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[─────┴]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$serviceCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0">
                        <xsl:text><![CDATA[├────────────────────────────┴]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[────────────────────────────┴]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$paymeansCount!=0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0">
                        <xsl:text><![CDATA[├──────────────────┴]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[──────────────────┴]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$paymentCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount!=0">
                        <xsl:text><![CDATA[├─────────────┴]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[─────────────┴]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$transferCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount!=0 and $paymentCount=0">
                        <xsl:text><![CDATA[├─────────────┴]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[─────────────┴]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount!=0 and $paymentCount=0 and $transferCount=0">
                        <xsl:text><![CDATA[├───────────┴──────────────────┴──────────────────┴──────────────────────┤]]>&#x0A;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                        <xsl:text><![CDATA[───────────┴──────────────────┴──────────────────┴──────────────────────┤]]>&#x0A;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <!-- Собственно печать -->
            
            <xsl:apply-templates select="bik|settle_acc|special_client_code|osb|service_channel_name|payment_means_name|payment_date|transfer_date|summary|total_summary|item">
                <xsl:with-param name="spacing" select="1"/>
            </xsl:apply-templates>
            
            <!-- Рисование нижней черты таблицы -->
            <xsl:if test="$bikCount!=0">
                <xsl:text><![CDATA[└────────────────────────────────────]]></xsl:text>
            </xsl:if>
            <xsl:if test="$settleCount!=0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0">
                        <xsl:text><![CDATA[└─────────────────────────]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[─────────────────────────]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$specCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0">
                        <xsl:text><![CDATA[└───────────]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[───────────]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$osbCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0">
                        <xsl:text><![CDATA[└──────]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[──────]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$serviceCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0">
                        <xsl:text><![CDATA[└─────────────────────────────]]></xsl:text>
                      </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[─────────────────────────────]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$paymeansCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0">
                        <xsl:text><![CDATA[└───────────────────]]></xsl:text>
                      </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[───────────────────]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            
            <xsl:if test="$paymentCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount = 0">
                        <xsl:text><![CDATA[└──────────────]]></xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[──────────────]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$transferCount!= 0">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0 and $paymentCount=0">
                        <xsl:text><![CDATA[└──────────────]]></xsl:text>
                      </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[──────────────]]></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0 and $paymentCount=0 and $transferCount=0">
                        <xsl:text><![CDATA[└────────────────────────────────────────────────────────────────────────┘]]>&#x0A;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                        <xsl:text><![CDATA[────────────────────────────────────────────────────────────────────────┘]]>&#x0A;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
     </xsl:template>
   
     <xsl:template match="bik">
        <xsl:param name="spacing" select="''"/>
                
        <xsl:call-template name="border-print">
             <xsl:with-param name="symbol" select="'│'"/>
             <xsl:with-param name="name" select="name()"/>
        </xsl:call-template>

        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select="@value"/>
            <xsl:with-param name="pad" select="$bikPad"/>   
        </xsl:call-template>
        <xsl:apply-templates select="corr_acc">
                <xsl:with-param name="countPad" select="9"/>
                <xsl:with-param name="sumPad" select="18"/>
                <xsl:with-param name="commissionPad" select="18"/>
                <xsl:with-param name="transferPad" select="20"/>
                <xsl:with-param name="spacing" select="$bikPad + 1"/>                
        </xsl:apply-templates>
     </xsl:template>   
     
     <xsl:template match="corr_acc">
        <xsl:param name="spacing" select="''"/>
        <xsl:text>/</xsl:text>        
        <xsl:call-template name="name-format-left">
            <xsl:with-param name="n">
                <xsl:call-template name="acc-format">
                    <xsl:with-param name="acc" select="@value"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="pad" select="$corrPad"/>   
        </xsl:call-template>
        
        <xsl:apply-templates select="settle_acc|special_client_code|osb|service_channel_name|payment_means_name|payment_date|transfer_date|summary|total_summary|item">
                <xsl:with-param name="spacing" select="$spacing + $corrPad + 3"/>
        </xsl:apply-templates>
     </xsl:template>
     
     <xsl:template match="settle_acc">
        <xsl:param name="spacing" select="''"/>
        <xsl:choose>
            <xsl:when test="position() = 1">
                <xsl:call-template name="border-print">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="name" select="name()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="space">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="pad" select="$spacing"/>
                </xsl:call-template>            
            </xsl:otherwise>
        </xsl:choose>        
        <xsl:call-template name="name-format">
            <xsl:with-param name="n">
                <xsl:call-template name="acc-format">
                    <xsl:with-param name="acc" select="@value"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="pad" select="$settlePad"/>   
        </xsl:call-template>
        
        <xsl:apply-templates select="special_client_code|osb|service_channel_name|payment_means_name|payment_date|transfer_date|summary|total_summary|item">
                <xsl:with-param name="spacing" select="$spacing + $settlePad + 2"/>
        </xsl:apply-templates>
     </xsl:template>
     
     <xsl:template match="special_client_code">

        <xsl:param name="spacing" select="''"/>
        <xsl:choose>
            <xsl:when test="position() = 1">
                <xsl:call-template name="border-print">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="name" select="name()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="space">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="pad" select="$spacing"/>
                </xsl:call-template>            
            </xsl:otherwise>
        </xsl:choose>        
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select="@value"/>
            <xsl:with-param name="pad" select="$specPad"/>   
        </xsl:call-template>
        
        <xsl:apply-templates select="osb|service_channel_name|payment_means_name|payment_date|transfer_date|summary|total_summary|item">
                <xsl:with-param name="spacing" select="$spacing + $specPad + 2"/>
        </xsl:apply-templates>
     </xsl:template>
     
     <xsl:template match="osb">
        <xsl:param name="spacing" select="''"/>
        <xsl:choose>
            <xsl:when test="position() = 1">
                <xsl:call-template name="border-print">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="name" select="name()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="space">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="pad" select="$spacing"/>
                </xsl:call-template>            
            </xsl:otherwise>
        </xsl:choose>        
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select="@value"/>
            <xsl:with-param name="pad" select="$osbPad"/>   
        </xsl:call-template>
        
        <xsl:apply-templates select="service_channel_name|payment_means_name|payment_date|transfer_date|summary|total_summary|item">
                <xsl:with-param name="spacing" select="$spacing + $osbPad + 2"/>
        </xsl:apply-templates>
     </xsl:template>
     
     <xsl:template match="service_channel_name">
        <xsl:param name="spacing" select="''"/>
        <xsl:choose>
            <xsl:when test="position() = 1">
                <xsl:call-template name="border-print">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="name" select="name()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="space">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="pad" select="$spacing"/>
                </xsl:call-template>            
            </xsl:otherwise>
        </xsl:choose>        
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select="@value"/>
            <xsl:with-param name="pad" select="$servicePad"/>   
        </xsl:call-template>
        
        <xsl:apply-templates select="payment_means_name|payment_date|transfer_date|summary|total_summary|item">
                <xsl:with-param name="spacing" select="$spacing + $servicePad + 2"/>
        </xsl:apply-templates>
     </xsl:template>
     
     <xsl:template match="payment_means_name">
        <xsl:param name="spacing" select="''"/>
        <xsl:choose>
            <xsl:when test="position() = 1">
                <xsl:call-template name="border-print">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="name" select="name()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="space">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="pad" select="$spacing"/>
                </xsl:call-template>            
            </xsl:otherwise>
        </xsl:choose>        
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select="@value"/>
            <xsl:with-param name="pad" select="$paymeansPad"/>   
        </xsl:call-template>
        
        <xsl:apply-templates select="payment_date|transfer_date|summary|total_summary|item">
                <xsl:with-param name="spacing" select="$spacing + $paymeansPad + 2"/>
        </xsl:apply-templates>
     </xsl:template>
          
     <xsl:template match="payment_date">
        <xsl:param name="spacing" select="''"/>
        <xsl:choose>
            <xsl:when test="position() = 1">
                <xsl:call-template name="border-print">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="name" select="name()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="space">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="pad" select="$spacing "/>
                </xsl:call-template>            
            </xsl:otherwise>
        </xsl:choose>        
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select="@value"/>
            <xsl:with-param name="pad" select="$paymentPad"/>   
        </xsl:call-template>
        
        <xsl:apply-templates select="transfer_date|summary|total_summary|item">
                <xsl:with-param name="spacing" select="$spacing + $paymentPad + 2"/>
        </xsl:apply-templates>
     </xsl:template>
     
     <xsl:template match="transfer_date">
        <xsl:param name="spacing" select="''"/>
        <xsl:choose>
            <xsl:when test="position() = 1">
                <xsl:call-template name="border-print">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="name" select="name()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="space">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="pad" select="$spacing "/>
                </xsl:call-template>           
            </xsl:otherwise>
        </xsl:choose>        
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select="@value"/>
            <xsl:with-param name="pad" select="$operPad"/>   
        </xsl:call-template>
        
        <xsl:apply-templates select="summary|total_summary|item">
			<xsl:with-param name="spacing" select="$spacing + $transferPad + 2"/>
        </xsl:apply-templates>
     </xsl:template>
     
     <xsl:template match="item">
        <!-- Отступ от левой границы таблицы-->
        <xsl:param name="spacing" select="''"/>
        <xsl:choose>
            <xsl:when test="position() = 1">
                <xsl:call-template name="border-print">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="name" select="name()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="space">
                    <xsl:with-param name="symbol" select="'│'"/>
                    <xsl:with-param name="pad" select="$spacing"/>
                </xsl:call-template>           
            </xsl:otherwise>
        </xsl:choose>        
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select="counter"/>
            <xsl:with-param name="pad" select="$countPad"/>   
        </xsl:call-template>
        <xsl:call-template name="s-format">
            <xsl:with-param name="s" select="sum"/>
            <xsl:with-param name="pad" select="$sumPad"/>   
        </xsl:call-template>
        <xsl:call-template name="s-format">
            <xsl:with-param name="s" select="serviceComm"/>
            <xsl:with-param name="pad" select="$commissionPad"/>   
        </xsl:call-template>
        <xsl:call-template name="s-format">
            <xsl:with-param name="s" select="sum - serviceComm"/>
            <xsl:with-param name="pad" select="$transferPad"/>   
        </xsl:call-template>
        <xsl:text>│&#x0A;</xsl:text>
     </xsl:template>
     
     
     <xsl:template match="total_summary">
        <xsl:param name="spacing" select="''"/>
        
        <xsl:variable name="width">
            <xsl:call-template name="width-count">
            </xsl:call-template>
        </xsl:variable>
        <xsl:call-template name="space">
            <xsl:with-param name="symbol">
                <xsl:choose>
                    <xsl:when test="$width > 5">
                        <xsl:value-of select="'│Итого'"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="pad" select="$width"/>
        </xsl:call-template>
        
        <xsl:call-template name="name-format">
            <xsl:with-param name="n" select="counter"/>
            <xsl:with-param name="pad" select="$countPad"/>   
        </xsl:call-template>
        <xsl:call-template name="s-format">
            <xsl:with-param name="s" select="sum"/>
            <xsl:with-param name="pad" select="$sumPad"/>   
        </xsl:call-template>
        <xsl:call-template name="s-format">
            <xsl:with-param name="s" select="serviceComm"/>
            <xsl:with-param name="pad" select="$commissionPad"/>   
        </xsl:call-template>
        <xsl:call-template name="s-format">
            <xsl:with-param name="s" select="sum - serviceComm"/>
            <xsl:with-param name="pad" select="$transferPad"/>   
        </xsl:call-template>
        <xsl:text>│&#x0A;</xsl:text>
        
        
     </xsl:template>
     
     
     
     <xsl:template name="space">
        <xsl:param name="symbol" select="''"/>
        <xsl:param name="pad" select="''"/>
        <xsl:call-template name="name-format-left">
             <xsl:with-param name="n" select="$symbol"/>
             <xsl:with-param name="pad" select="$pad"/>
        </xsl:call-template> 
     </xsl:template>
     
     <!-- Печать левой границы таблицы либо отступа для первых элементов -->
     
     <xsl:template name="border-print">
        <!-- Имя столбца и символ, с которого начинать строку -->   
        <xsl:param name="name" select="''"/>
        <xsl:param name="symbol" select="''"/>
        <xsl:choose>
            <xsl:when test="$name='bik'">
                <xsl:value-of select="$symbol"/>
            </xsl:when>
            <xsl:when test="$name='settle_acc'">
                <xsl:choose>
                    <xsl:when test="$bikCount=0">
                        <xsl:value-of select="$symbol"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[ ]]> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>    
            </xsl:when>
            <xsl:when test="$name='special_client_code'">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0">
                        <xsl:value-of select="$symbol"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[ ]]> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$name='osb'">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0">
                        <xsl:value-of select="$symbol"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[ ]]> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$name='service_channel_name'">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0">
                        <xsl:value-of select="$symbol"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[ ]]> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$name='payment_means_name'">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0">
                        <xsl:value-of select="$symbol"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[ ]]> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$name='payment_date'">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0">
                        <xsl:value-of select="$symbol"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[ ]]> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>    
            </xsl:when>
            <xsl:when test="$name='transfer_date'">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $serviceCount=0 and $paymeansCount=0 and $paymentCount=0">
                        <xsl:value-of select="$symbol"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[ ]]> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$name='item'">
                <xsl:choose>
                    <xsl:when test="$bikCount=0 and $settleCount=0 and $specCount=0 and $osbCount=0 and $paymentCount=0 and $transferCount=0 and $serviceCount=0 and $paymeansCount=0">
                        <xsl:value-of select="$symbol"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text><![CDATA[  ]]> </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
        </xsl:choose>
        
     </xsl:template>
        
        
     <xsl:template name="width-count">
        <xsl:variable name="wd_1">
            <xsl:choose>
                <xsl:when test="$bikCount=0">
                    <xsl:value-of select="2"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$bikPad + $corrPad + 5"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="wd_2">
            <xsl:choose>
                <xsl:when test="$settleCount=0">
                    <xsl:value-of select="$wd_1"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$wd_1 + $settlePad + 2"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="wd_3">
            <xsl:choose>
                <xsl:when test="$specCount=0">
                    <xsl:value-of select="$wd_2"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$wd_2 + $specPad + 2"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="wd_4">
            <xsl:choose>
                <xsl:when test="$osbCount=0">
                    <xsl:value-of select="$wd_3"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$wd_3 + $osbPad + 2"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="wd_5">
            <xsl:choose>
                <xsl:when test="$serviceCount=0">
                    <xsl:value-of select="$wd_4"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$wd_4 + $servicePad + 2"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="wd_6">
            <xsl:choose>
                <xsl:when test="$paymeansCount=0">
                    <xsl:value-of select="$wd_5"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$wd_5 + $paymeansPad + 2"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="wd_7">
            <xsl:choose>
                <xsl:when test="$paymentCount=0">
                    <xsl:value-of select="$wd_6"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$wd_6 + $paymentPad + 2"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="wd_8">
            <xsl:choose>
                <xsl:when test="$transferCount=0">
                    <xsl:value-of select="$wd_7"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$wd_7 + $operPad + 2"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$wd_8"/>
     </xsl:template>

     <xsl:template match="*">
     </xsl:template>
</xsl:stylesheet>