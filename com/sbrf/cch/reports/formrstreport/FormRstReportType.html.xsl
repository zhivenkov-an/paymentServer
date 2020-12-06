<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0" xmlns:xalan="http://xml.apache.org/xslt" xmlns="http://www.w3.org/1999/xhtml">

    <xsl:import href="/com/sbrf/report/types/xsl/ReportCommonFunctions.xsl" />
    <xsl:output method="xhtml" encoding="UTF-8"
        doctype-public="-//W3C//DTD XHTML 1.1//EN" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
        indent="yes" />

	<xsl:template match="/root">
		<html xml:lang="ru">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<title>Форма *.rst</title>
			</head>
			<body>
				<xsl:choose>
					<xsl:when
						test="count(/root/items/bik) + count(/root/items/osb) + count(/root/items/settle_acc) + count(/root/items/payment_date) + count(/root/items/transfer_date) + count(/root/items/special_client_code) + count(/root/items/service_channel_name) + count(/root/items/total_summary) = 0">
						<xsl:text>Информация, удовлетворяющая условиям поиска, отсутствует. </xsl:text>
						<xsl:text>&#x0A;</xsl:text>
						<xsl:text>Проверьте введенные параметры отчета </xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<h4 style="margin-bottom: 3px;">
							<xsl:text>Отчет *.rst в разрезе  </xsl:text>
							<xsl:value-of select="grouped" />
							<br />
							<xsl:text>за период с </xsl:text>
							<xsl:value-of select="start" />
							<xsl:text> по </xsl:text>
							<xsl:value-of select="end" />
						</h4>
						<xsl:apply-templates select="items">
							<xsl:with-param name="start" select="start" />
							<xsl:with-param name="end" select="end" />
						</xsl:apply-templates>
					</xsl:otherwise>
				</xsl:choose>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="items">
		<xsl:param name="start" select="''" />
		<xsl:param name="end" select="''" />
		<xsl:variable name="specCount" select="count(//special_client_code)" />
		<xsl:variable name="osbCount" select="count(//osb)" />
		<xsl:variable name="serviceCount" select="count(//service_channel_name)" />
		<xsl:variable name="paymentCount" select="count(//payment_date)" />
		<xsl:variable name="transferCount" select="count(//transfer_date)" />
		<table border="0" cellspacing="0" cellpadding="0">
			<xsl:choose>
				<xsl:when test="count(bik) != 0">
					<xsl:apply-templates select="bik">
						<xsl:with-param name="start" select="$start" />
						<xsl:with-param name="end" select="$end" />
						<xsl:with-param name="osbCount" select="$osbCount" />
						<xsl:with-param name="serviceCount" select="$serviceCount" />
						<xsl:with-param name="paymentCount" select="$paymentCount" />
						<xsl:with-param name="transferCount" select="$transferCount" />
						<xsl:with-param name="specCount" select="$specCount" />
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="count(settle_acc) != 0">
					<xsl:apply-templates select="settle_acc">
						<xsl:with-param name="start" select="$start" />
						<xsl:with-param name="end" select="$end" />
						<xsl:with-param name="osbCount" select="$osbCount" />
						<xsl:with-param name="serviceCount" select="$serviceCount" />
						<xsl:with-param name="paymentCount" select="$paymentCount" />
						<xsl:with-param name="transferCount" select="$transferCount" />
						<xsl:with-param name="specCount" select="$specCount" />
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="$specCount != 0">
					<xsl:apply-templates select="special_client_code">
						<xsl:with-param name="start" select="$start" />
						<xsl:with-param name="end" select="$end" />
						<xsl:with-param name="osbCount" select="$osbCount" />
						<xsl:with-param name="serviceCount" select="$serviceCount" />
						<xsl:with-param name="paymentCount" select="$paymentCount" />
						<xsl:with-param name="transferCount" select="$transferCount" />
						<xsl:with-param name="specCount" select="$specCount" />
						<xsl:with-param name="bik" select="''" />
						<xsl:with-param name="settleAcc" select="''" />
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="$osbCount != 0">
					<xsl:apply-templates select="osb">
						<xsl:with-param name="start" select="$start" />
						<xsl:with-param name="end" select="$end" />
						<xsl:with-param name="bik" select="''" />
						<xsl:with-param name="settleAcc" select="''" />
						<xsl:with-param name="osbCount" select="$osbCount" />
						<xsl:with-param name="serviceCount" select="$serviceCount" />
						<xsl:with-param name="paymentCount" select="$paymentCount" />
						<xsl:with-param name="transferCount" select="$transferCount" />
						<xsl:with-param name="specCount" select="$specCount" />
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="$serviceCount != 0">
					<xsl:apply-templates select="service_channel_name">
						<xsl:with-param name="start" select="$start" />
						<xsl:with-param name="end" select="$end" />
						<xsl:with-param name="bik" select="''" />
						<xsl:with-param name="settleAcc" select="''" />
						<xsl:with-param name="osbCount" select="$osbCount" />
						<xsl:with-param name="serviceCount" select="$serviceCount" />
						<xsl:with-param name="paymentCount" select="$paymentCount" />
						<xsl:with-param name="transferCount" select="$transferCount" />
						<xsl:with-param name="specCount" select="$specCount" />
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="$paymentCount != 0">
					<xsl:apply-templates select="payment_date">
						<xsl:with-param name="start" select="$start" />
						<xsl:with-param name="end" select="$end" />
						<xsl:with-param name="osbCount" select="$osbCount" />
						<xsl:with-param name="serviceCount" select="$serviceCount" />
						<xsl:with-param name="paymentCount" select="$paymentCount" />
						<xsl:with-param name="transferCount" select="$transferCount" />
						<xsl:with-param name="specCount" select="$specCount" />
						<xsl:with-param name="bik" select="''" />
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="$transferCount != 0">
					<xsl:apply-templates select="transfer_date">
						<xsl:with-param name="bik" select="''" />
						<xsl:with-param name="settleAcc" select="''" />
						<xsl:with-param name="start" select="$start" />
						<xsl:with-param name="end" select="$end" />
						<xsl:with-param name="specCount" select="$specCount" />
						<xsl:with-param name="osbCount" select="$osbCount" />
						<xsl:with-param name="paymentCount" select="$paymentCount" />
						<xsl:with-param name="transferCount" select="$transferCount" />
						<xsl:with-param name="speccode" select="''" />
						<xsl:with-param name="osb" select="''" />
						<xsl:with-param name="service_channel_name" select="''" />
						<xsl:with-param name="payment_date" select="''" />

					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="count(total_summary) != 0">
					<tr>
						<td>
							<h4 style="margin-bottom: 3px;">
								<xsl:text>За период с </xsl:text>
								<xsl:value-of select="$start" />
								<xsl:text> по </xsl:text>
								<xsl:value-of select="$end" />
							</h4>
							<table border="1" cellspacing="0"
								style="text-align:right; table-layout:fixed; width:41em;"
								cellpadding="3">
								<tr>
									<td style="text-align:center; width:5em">Кол-во документов</td>
									<td style="text-align:center; width:12em">Сумма платежей</td>
									<td style="text-align:center; width:12em">Сумма комиссии</td>
									<td style="text-align:center; width:12em">Сумма к перечислению</td>
								</tr>
								<xsl:apply-templates select="total_summary">
									<xsl:with-param name="speccode" select="''" />
									<xsl:with-param name="osb" select="''" />
									<xsl:with-param name="service_channel_name"
										select="''" />
									<xsl:with-param name="payment_date" select="''" />
									<xsl:with-param name="transfer_date" select="''" />
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
								</xsl:apply-templates>
							</table>
						</td>
					</tr>
				</xsl:when>
			</xsl:choose>
		</table>
	</xsl:template>

	<xsl:template match="bik">
		<xsl:param name="start" select="''" />
		<xsl:param name="end" select="''" />
		<xsl:param name="specCount" select="''" />
		<xsl:param name="osbCount" select="''" />
		<xsl:param name="serviceCount" select="''" />
		<xsl:param name="paymentCount" select="''" />
		<xsl:param name="transferCount" select="''" />
		<xsl:apply-templates select="corr_acc">
			<xsl:with-param name="bik" select="@value" />
			<xsl:with-param name="osbCount" select="$osbCount" />
			<xsl:with-param name="serviceCount" select="$serviceCount" />
			<xsl:with-param name="paymentCount" select="$paymentCount" />
			<xsl:with-param name="transferCount" select="$transferCount" />
			<xsl:with-param name="specCount" select="$specCount" />
			<xsl:with-param name="start" select="$start" />
			<xsl:with-param name="end" select="$end" />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="corr_acc">
		<xsl:param name="start" select="''" />
		<xsl:param name="end" select="''" />
		<xsl:param name="specCount" select="''" />
		<xsl:param name="osbCount" select="''" />
		<xsl:param name="serviceCount" select="''" />
		<xsl:param name="paymentCount" select="''" />
		<xsl:param name="transferCount" select="''" />
		<xsl:param name="bik" select="''" />
		<xsl:choose>
			<xsl:when test="0 = count(settle_acc)">
				<tr>
					<td>
						<h4 style="margin-bottom: 3px;">
							<xsl:text>БИК </xsl:text>
							<xsl:value-of select="$bik" />
							<br />
							<xsl:text>Корр. счет </xsl:text>
							<xsl:call-template name="acc-format">
								<xsl:with-param name="acc" select="@value" />
							</xsl:call-template>
							<br />
							<xsl:text>За период с </xsl:text>
							<xsl:value-of select="$start" />
							<xsl:text> по </xsl:text>
							<xsl:value-of select="$end" />
						</h4>
						<table border="1" cellspacing="0"
							style="text-align:right; table-layout:fixed;" cellpadding="3">
							<thead>
								<tr>
									<xsl:if test="0 &lt; $specCount">
										<td style="text-align:center; width:6em">Код спец. клиента</td>
									</xsl:if>
									<xsl:if test="0 &lt; $osbCount">
										<td style="text-align:center; width:4em">ОСБ</td>
									</xsl:if>
									<xsl:if test="0 &lt; $serviceCount">
										<td style="text-align:center; width:20em">Канал обслуживания</td>
									</xsl:if>
									<xsl:if test="0 &lt; $paymentCount">
										<td style="text-align:center; width:7em">Дата платежа</td>
									</xsl:if>
									<xsl:if test="0 &lt; $transferCount">
										<td style="text-align:center; width:7em">Дата перечисления</td>
									</xsl:if>
									<td style="text-align:center; width:5em">Кол-во документов</td>
									<td style="text-align:center; width:12em">Сумма платежей</td>
									<td style="text-align:center; width:12em">Сумма комиссии</td>
									<td style="text-align:center; width:12em">Сумма к перечислению</td>
								</tr>
							</thead>
							<tbody>
								<xsl:choose>
									<xsl:when test="0 &lt; $specCount">
										<xsl:apply-templates select="special_client_code">
											<xsl:with-param name="start" select="$start" />
											<xsl:with-param name="end" select="$end" />
											<xsl:with-param name="specCount" select="$specCount" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
											<xsl:with-param name="bik" select="$bik" />
											<xsl:with-param name="settleAcc" select="''" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; $osbCount">
										<xsl:apply-templates select="osb">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="specCount" select="$specCount" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
											<xsl:with-param name="bik" select="$bik" />
											<xsl:with-param name="settleAcc" select="''" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; $serviceCount">
										<xsl:apply-templates select="service_channel_name">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="specCount" select="$specCount" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
											<xsl:with-param name="bik" select="$bik" />
											<xsl:with-param name="settleAcc" select="''" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; $paymentCount">
										<xsl:apply-templates select="payment_date">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="service_channel_name"
												select="''" />
											<xsl:with-param name="specCount" select="$specCount" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
											<xsl:with-param name="bik" select="$bik" />
											<xsl:with-param name="settleAcc" select="''" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; $transferCount">
										<xsl:apply-templates select="transfer_date">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="service_channel_name"
												select="''" />
											<xsl:with-param name="payment_date" select="''" />
											<xsl:with-param name="specCount" select="$specCount" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
											<xsl:with-param name="bik" select="$bik" />

											<xsl:with-param name="settleAcc" select="''" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; count(item)">
										<xsl:apply-templates select="item">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="service_channel_name"
												select="''" />
											<xsl:with-param name="payment_date" select="''" />
											<xsl:with-param name="transfer_date" select="''" />
											<xsl:with-param name="specCount" select="$specCount" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
											<xsl:with-param name="bik" select="$bik" />
											<xsl:with-param name="settleAcc" select="''" />
										</xsl:apply-templates>
									</xsl:when>
								</xsl:choose>
								<xsl:apply-templates select="total_summary">
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
								</xsl:apply-templates>
							</tbody>
						</table>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="settle_acc">
					<xsl:with-param name="bik" select="$bik" />
					<xsl:with-param name="osbCount" select="$osbCount" />
					<xsl:with-param name="serviceCount" select="$serviceCount" />
					<xsl:with-param name="paymentCount" select="$paymentCount" />
					<xsl:with-param name="transferCount" select="$transferCount" />
					<xsl:with-param name="specCount" select="$specCount" />
					<xsl:with-param name="start" select="$start" />
					<xsl:with-param name="end" select="$end" />
					<xsl:with-param name="corrAcc" select="@value" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="settle_acc">
		<xsl:param name="start" select="''" />
		<xsl:param name="end" select="''" />
		<xsl:param name="specCount" select="''" />
		<xsl:param name="osbCount" select="''" />
		<xsl:param name="serviceCount" select="''" />
		<xsl:param name="paymentCount" select="''" />
		<xsl:param name="transferCount" select="''" />
		<xsl:param name="bik" select="''" />
		<xsl:param name="corrAcc" select="''" />
		<tr>
			<td>
				<h4 style="margin-bottom: 3px;">
					<xsl:if test="$bik != ''">
						<xsl:text>БИК </xsl:text>
						<xsl:value-of select="$bik" />
						<br />
						<xsl:text>Корр.счет </xsl:text>
						<xsl:call-template name="acc-format">
							<xsl:with-param name="acc" select="$corrAcc" />
						</xsl:call-template>
						<br />
					</xsl:if>
					<xsl:text>Расч.счет </xsl:text>
					<xsl:call-template name="acc-format">
						<xsl:with-param name="acc" select="@value" />
					</xsl:call-template>
					<br />
					<xsl:text>За период с </xsl:text>
					<xsl:value-of select="$start" />
					<xsl:text> по </xsl:text>
					<xsl:value-of select="$end" />
				</h4>
				<table border="1" cellspacing="0"
					style="text-align:right; table-layout:fixed;" cellpadding="3">
					<thead>
						<tr>
							<xsl:if test="0 &lt; $specCount">
								<td style="text-align:center; width:6em">Код спец. клиента</td>
							</xsl:if>
							<xsl:if test="0 &lt; $osbCount">
								<td style="text-align:center; width:4em">ОСБ</td>
							</xsl:if>
							<xsl:if test="0 &lt; $serviceCount">
								<td style="text-align:center; width:20em">Канал обслуживания</td>
							</xsl:if>
							<xsl:if test="0 &lt; $paymentCount">
								<td style="text-align:center; width:7em">Дата платежа</td>
							</xsl:if>
							<xsl:if test="0 &lt; $transferCount">
								<td style="text-align:center; width:7em">Дата перечисления</td>
							</xsl:if>
							<td style="text-align:center; width:5em">Кол-во документов</td>
							<td style="text-align:center; width:12em">Сумма платежей</td>
							<td style="text-align:center; width:12em">Сумма комиссии</td>
							<td style="text-align:center; width:12em">Сумма к перечислению</td>
						</tr>
					</thead>
					<tbody>
						<xsl:choose>
							<xsl:when test="0 &lt; $specCount">
								<xsl:apply-templates select="special_client_code">
									<xsl:with-param name="start" select="$start" />
									<xsl:with-param name="end" select="$end" />
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
									<xsl:with-param name="bik" select="$bik" />
									<xsl:with-param name="settleAcc" select="@value" />
								</xsl:apply-templates>
							</xsl:when>
							<xsl:when test="0 &lt; $osbCount">
								<xsl:apply-templates select="osb">
									<xsl:with-param name="speccode" select="''" />
									<xsl:with-param name="service_channel_name"
										select="''" />
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
									<xsl:with-param name="bik" select="$bik" />
									<xsl:with-param name="settleAcc" select="@value" />
								</xsl:apply-templates>
							</xsl:when>
							<xsl:when test="0 &lt; $serviceCount">
								<xsl:apply-templates select="service_channel_name">
									<xsl:with-param name="speccode" select="''" />
									<xsl:with-param name="service_channel_name"
										select="''" />
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
									<xsl:with-param name="bik" select="$bik" />
									<xsl:with-param name="settleAcc" select="@value" />
								</xsl:apply-templates>
							</xsl:when>
							<xsl:when test="0 &lt; $paymentCount">
								<xsl:apply-templates select="payment_date">
									<xsl:with-param name="speccode" select="''" />
									<xsl:with-param name="osb" select="''" />
									<xsl:with-param name="service_channel_name"
										select="''" />
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
									<xsl:with-param name="bik" select="$bik" />
									<xsl:with-param name="settleAcc" select="@value" />
								</xsl:apply-templates>
							</xsl:when>
							<xsl:when test="0 &lt; $transferCount">
								<xsl:apply-templates select="transfer_date">
									<xsl:with-param name="speccode" select="''" />
									<xsl:with-param name="osb" select="''" />
									<xsl:with-param name="service_channel_name"
										select="''" />
									<xsl:with-param name="payment_date" select="''" />
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
									<xsl:with-param name="bik" select="$bik" />
									<xsl:with-param name="settleAcc" select="@value" />
								</xsl:apply-templates>
							</xsl:when>
							<xsl:when test="0 &lt; count(item)">
								<xsl:apply-templates select="item">
									<xsl:with-param name="speccode" select="''" />
									<xsl:with-param name="osb" select="''" />
									<xsl:with-param name="service_channel_name"
										select="''" />
									<xsl:with-param name="payment_date" select="''" />
									<xsl:with-param name="transfer_date" select="''" />
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
								</xsl:apply-templates>
							</xsl:when>
						</xsl:choose>
						<xsl:apply-templates select="summary">
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
						<xsl:apply-templates select="total_summary">
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</tbody>
				</table>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="special_client_code">
		<xsl:param name="start" select="''" />
		<xsl:param name="end" select="''" />
		<xsl:param name="bik" select="''" />
		<xsl:param name="settleAcc" select="''" />
		<xsl:param name="specCount" select="''" />
		<xsl:param name="osbCount" select="''" />
		<xsl:param name="serviceCount" select="''" />
		<xsl:param name="paymentCount" select="''" />
		<xsl:param name="transferCount" select="''" />
		<xsl:choose>
			<xsl:when test="($bik = '') and ($settleAcc = '')">
				<tr>
					<td>
						<h4 style="margin-bottom: 3px;">
							<xsl:text>Код спец. клиента </xsl:text>
							<xsl:value-of select="@value" />
							<br />
							<xsl:text>За период с </xsl:text>
							<xsl:value-of select="$start" />
							<xsl:text> по </xsl:text>
							<xsl:value-of select="$end" />
						</h4>
						<table border="1" cellspacing="0"
							style="text-align:right; table-layout:fixed;" cellpadding="3">
							<thead>
								<tr>
									<xsl:if test="0 &lt; $osbCount">
										<td style="text-align:center; width:4em">ОСБ</td>
									</xsl:if>
									<xsl:if test="0 &lt; $serviceCount">
										<td style="text-align:center; width:20em">Канал обслуживания</td>
									</xsl:if>
									<xsl:if test="0 &lt; $paymentCount">
										<td style="text-align:center; width:7em">Дата платежа</td>
									</xsl:if>
									<xsl:if test="0 &lt; $transferCount">
										<td style="text-align:center; width:7em">Дата перечисления</td>
									</xsl:if>
									<td style="text-align:center; width:5em">Кол-во документов</td>
									<td style="text-align:center; width:12em">Сумма платежей</td>
									<td style="text-align:center; width:12em">Сумма комиссии</td>
									<td style="text-align:center; width:12em">Сумма к перечислению</td>
								</tr>
							</thead>
							<tbody>
								<xsl:choose>
									<xsl:when test="0 &lt; $osbCount">
										<xsl:apply-templates select="osb">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="specCount" select="-1" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
											<xsl:with-param name="bik" select="$bik" />
											<xsl:with-param name="settleAcc" select="$settleAcc" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; $serviceCount">
										<xsl:apply-templates select="service_channel_count">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="specCount" select="-1" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
											<xsl:with-param name="bik" select="$bik" />
											<xsl:with-param name="settleAcc" select="$settleAcc" />
										</xsl:apply-templates>
									</xsl:when>

									<xsl:when test="0 &lt; $paymentCount">
										<xsl:apply-templates select="payment_date">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="service_channel_count"
												select="''" />
											<xsl:with-param name="specCount" select="-1" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; $transferCount">
										<xsl:apply-templates select="transfer_date">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="service_channel_count"
												select="''" />
											<xsl:with-param name="payment_date" select="''" />
											<xsl:with-param name="specCount" select="-1" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; count(item)">
										<xsl:apply-templates select="item">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="service_channel_count"
												select="''" />
											<xsl:with-param name="payment_date" select="''" />
											<xsl:with-param name="transfer_date" select="''" />
											<xsl:with-param name="specCount" select="-1" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
										</xsl:apply-templates>
									</xsl:when>
								</xsl:choose>
								<xsl:apply-templates select="summary">
									<xsl:with-param name="speccode" select="''" />
									<xsl:with-param name="osb" select="''" />
									<xsl:with-param name="service_channel_count"
										select="''" />
									<xsl:with-param name="payment_date" select="''" />
									<xsl:with-param name="transfer_date" select="''" />
									<xsl:with-param name="specCount" select="-1" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
								</xsl:apply-templates>
								<xsl:apply-templates select="total_summary">
									<xsl:with-param name="specCount" select="-1" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
								</xsl:apply-templates>
							</tbody>
						</table>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="0 &lt; $osbCount">
						<xsl:apply-templates select="osb">
							<xsl:with-param name="speccode" select="@value" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
							<xsl:with-param name="bik" select="$bik" />
							<xsl:with-param name="settleAcc" select="$settleAcc" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="0 &lt; $serviceCount">
						<xsl:apply-templates select="service_channel_count">
							<xsl:with-param name="speccode" select="@value" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
							<xsl:with-param name="bik" select="$bik" />
							<xsl:with-param name="settleAcc" select="$settleAcc" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="0 &lt; $paymentCount">
						<xsl:apply-templates select="payment_date">
							<xsl:with-param name="speccode" select="@value" />
							<xsl:with-param name="osb" select="''" />
							<xsl:with-param name="serviceCount" select="''" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="0 &lt; $transferCount">
						<xsl:apply-templates select="transfer_date">
							<xsl:with-param name="speccode" select="@value" />
							<xsl:with-param name="osb" select="''" />
							<xsl:with-param name="serviceCount" select="''" />
							<xsl:with-param name="payment_date" select="''" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="0 &lt; count(item)">
						<xsl:apply-templates select="item">
							<xsl:with-param name="speccode" select="@value" />
							<xsl:with-param name="osb" select="''" />
							<xsl:with-param name="payment_date" select="''" />
							<xsl:with-param name="transfer_date" select="''" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</xsl:when>
				</xsl:choose>
				<xsl:apply-templates select="summary">
					<xsl:with-param name="speccode" select="@value" />
					<xsl:with-param name="osb" select="''" />
					<xsl:with-param name="service_channel_count" select="''" />
					<xsl:with-param name="payment_date" select="''" />
					<xsl:with-param name="transfer_date" select="''" />
					<xsl:with-param name="specCount" select="$specCount" />
					<xsl:with-param name="osbCount" select="$osbCount" />
					<xsl:with-param name="serviceCount" select="$serviceCount" />
					<xsl:with-param name="paymentCount" select="$paymentCount" />
					<xsl:with-param name="transferCount" select="$transferCount" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="osb">
		<xsl:param name="bik" select="''" />
		<xsl:param name="settleAcc" select="''" />
		<xsl:param name="start" select="''" />
		<xsl:param name="end" select="''" />
		<xsl:param name="specCount" select="''" />
		<xsl:param name="osbCount" select="''" />
		<xsl:param name="serviceCount" select="''" />
		<xsl:param name="paymentCount" select="''" />
		<xsl:param name="transferCount" select="''" />
		<xsl:param name="speccode" select="''" />
		<xsl:choose>
			<xsl:when test="($bik = '') and ($settleAcc = '') and ($specCount = 0)">
				<tr>
					<td>
						<h4 style="margin-bottom: 3px;">
							<xsl:text>ОСБ </xsl:text>
							<xsl:value-of select="@value" />
							<br />
							<xsl:text>За период с </xsl:text>
							<xsl:value-of select="$start" />
							<xsl:text> по </xsl:text>
							<xsl:value-of select="$end" />
						</h4>
						<table border="1" cellspacing="0"
							style="text-align:right; table-layout:fixed;" cellpadding="3">
							<thead>
								<tr>
									<xsl:if test="0 &lt; $paymentCount">
										<td style="text-align:center; width:7em">Дата платежа</td>
									</xsl:if>
									<xsl:if test="0 &lt; $transferCount">
										<td style="text-align:center; width:7em">Дата перечисления</td>
									</xsl:if>
									<td style="text-align:center; width:5em">Кол-во документов</td>
									<td style="text-align:center; width:12em">Сумма платежей</td>
									<td style="text-align:center; width:12em">Сумма комиссии</td>
									<td style="text-align:center; width:12em">Сумма к перечислению</td>
								</tr>
							</thead>
							<tbody>
								<xsl:choose>
									<xsl:when test="0 &lt; $paymentCount">
										<xsl:apply-templates select="payment_date">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="specCount" select="-1" />
											<xsl:with-param name="osbCount" select="-1" />
											<xsl:with-param name="serviceCount" select="-1" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; $transferCount">
										<xsl:apply-templates select="transfer_date">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="payment_date" select="''" />
											<xsl:with-param name="specCount" select="-1" />
											<xsl:with-param name="osbCount" select="-1" />
											<xsl:with-param name="serviceCount" select="-1" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; count(item)">
										<xsl:apply-templates select="item">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="payment_date" select="''" />
											<xsl:with-param name="transfer_date" select="''" />
											<xsl:with-param name="specCount" select="-1" />
											<xsl:with-param name="osbCount" select="-1" />
											<xsl:with-param name="serviceCount" select="-1" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
										</xsl:apply-templates>
									</xsl:when>
								</xsl:choose>
								<xsl:apply-templates select="summary">
									<xsl:with-param name="speccode" select="''" />
									<xsl:with-param name="osb" select="''" />
									<xsl:with-param name="payment_date" select="''" />
									<xsl:with-param name="transfer_date" select="''" />
									<xsl:with-param name="specCount" select="0" />
									<xsl:with-param name="osbCount" select="0" />
									<xsl:with-param name="serviceCount" select="0" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
								</xsl:apply-templates>
								<xsl:apply-templates select="total_summary">
									<xsl:with-param name="speccode" select="''" />
									<xsl:with-param name="osb" select="''" />
									<xsl:with-param name="payment_date" select="''" />
									<xsl:with-param name="transfer_date" select="''" />
									<xsl:with-param name="specCount" select="0" />
									<xsl:with-param name="osbCount" select="0" />
									<xsl:with-param name="serviceCount" select="0" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
								</xsl:apply-templates>
							</tbody>
						</table>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="0 &lt; $serviceCount">
						<xsl:apply-templates select="service_channel_name">
							<xsl:with-param name="speccode" select="$speccode" />
							<xsl:with-param name="osb" select="@value" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
							<xsl:with-param name="bik" select="$bik" />
							<xsl:with-param name="settleAcc" select="''" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="0 &lt; $paymentCount">
						<xsl:apply-templates select="payment_date">
							<xsl:with-param name="speccode" select="$speccode" />
							<xsl:with-param name="osb" select="@value" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="0 &lt; $transferCount">
						<xsl:apply-templates select="transfer_date">
							<xsl:with-param name="speccode" select="$speccode" />
							<xsl:with-param name="osb" select="@value" />
							<xsl:with-param name="payment_date" select="''" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="0 &lt; count(item)">
						<xsl:apply-templates select="item">
							<xsl:with-param name="speccode" select="$speccode" />
							<xsl:with-param name="osb" select="@value" />
							<xsl:with-param name="payment_date" select="''" />
							<xsl:with-param name="transfer_date" select="''" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</xsl:when>
				</xsl:choose>
				<xsl:apply-templates select="total_summary">
					<xsl:with-param name="speccode" select="$speccode" />
					<xsl:with-param name="osb" select="@value" />
					<xsl:with-param name="service_channel_count" select="''" />
					<xsl:with-param name="payment_date" select="''" />
					<xsl:with-param name="transfer_date" select="''" />
					<xsl:with-param name="specCount" select="$specCount" />
					<xsl:with-param name="osbCount" select="$osbCount" />
					<xsl:with-param name="serviceCount" select="$serviceCount" />
					<xsl:with-param name="paymentCount" select="$paymentCount" />
					<xsl:with-param name="transferCount" select="$transferCount" />
				</xsl:apply-templates>
				<xsl:apply-templates select="summary">
					<xsl:with-param name="speccode" select="$speccode" />
					<xsl:with-param name="osb" select="@value" />
					<xsl:with-param name="service_channel_count" select="''" />
					<xsl:with-param name="payment_date" select="''" />
					<xsl:with-param name="transfer_date" select="''" />
					<xsl:with-param name="specCount" select="$specCount" />
					<xsl:with-param name="osbCount" select="$osbCount" />
					<xsl:with-param name="serviceCount" select="$serviceCount" />
					<xsl:with-param name="paymentCount" select="$paymentCount" />
					<xsl:with-param name="transferCount" select="$transferCount" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="service_channel_name">
		<xsl:param name="bik" select="''" />
		<xsl:param name="settleAcc" select="''" />
		<xsl:param name="start" select="''" />
		<xsl:param name="osb" select="''" />
		<xsl:param name="end" select="''" />
		<xsl:param name="specCount" select="''" />
		<xsl:param name="osbCount" select="''" />
		<xsl:param name="serviceCount" select="''" />
		<xsl:param name="paymentCount" select="''" />
		<xsl:param name="transferCount" select="''" />
		<xsl:param name="speccode" select="''" />
		<xsl:choose>
			<xsl:when test="($bik = '') and ($settleAcc = '') and ($specCount = 0)">
				<tr>
					<td>
						<h4 style="margin-bottom: 3px;">
							<xsl:text>ОСБ </xsl:text>
							<xsl:value-of select="@value" />
							<br />
							<xsl:text>За период с </xsl:text>
							<xsl:value-of select="$start" />
							<xsl:text> по </xsl:text>
							<xsl:value-of select="$end" />
						</h4>
						<table border="1" cellspacing="0"
							style="text-align:right; table-layout:fixed;" cellpadding="3">
							<thead>
								<tr>
									<xsl:if test="0 &lt; $paymentCount">
										<td style="text-align:center; width:7em">Дата платежа</td>
									</xsl:if>
									<xsl:if test="0 &lt; $transferCount">
										<td style="text-align:center; width:7em">Дата перечисления</td>
									</xsl:if>
									<td style="text-align:center; width:5em">Кол-во документов</td>
									<td style="text-align:center; width:12em">Сумма платежей</td>
									<td style="text-align:center; width:12em">Сумма комиссии</td>
									<td style="text-align:center; width:12em">Сумма к перечислению</td>
								</tr>
							</thead>
							<tbody>
								<xsl:choose>
									<xsl:when test="0 &lt; $paymentCount">
										<xsl:apply-templates select="payment_date">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="specCount" select="-1" />
											<xsl:with-param name="osbCount" select="-1" />
											<xsl:with-param name="serviceCount" select="-1" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; $transferCount">
										<xsl:apply-templates select="transfer_date">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="payment_date" select="''" />
											<xsl:with-param name="specCount" select="-1" />
											<xsl:with-param name="osbCount" select="-1" />
											<xsl:with-param name="serviceCount" select="-1" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; count(item)">
										<xsl:apply-templates select="item">
											<xsl:with-param name="speccode" select="''" />
											<xsl:with-param name="osb" select="''" />
											<xsl:with-param name="payment_date" select="''" />
											<xsl:with-param name="transfer_date" select="''" />
											<xsl:with-param name="specCount" select="-1" />
											<xsl:with-param name="osbCount" select="-1" />
											<xsl:with-param name="serviceCount" select="-1" />
											<xsl:with-param name="paymentCount" select="$paymentCount" />
											<xsl:with-param name="transferCount" select="$transferCount" />
										</xsl:apply-templates>
									</xsl:when>
								</xsl:choose>
								<xsl:apply-templates select="summary">
									<xsl:with-param name="speccode" select="''" />
									<xsl:with-param name="osb" select="''" />
									<xsl:with-param name="payment_date" select="''" />
									<xsl:with-param name="transfer_date" select="''" />
									<xsl:with-param name="specCount" select="0" />
									<xsl:with-param name="osbCount" select="0" />
									<xsl:with-param name="serviceCount" select="0" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
								</xsl:apply-templates>
								<xsl:apply-templates select="total_summary">
									<xsl:with-param name="speccode" select="''" />
									<xsl:with-param name="osb" select="''" />
									<xsl:with-param name="payment_date" select="''" />
									<xsl:with-param name="transfer_date" select="''" />
									<xsl:with-param name="specCount" select="0" />
									<xsl:with-param name="osbCount" select="0" />
									<xsl:with-param name="serviceCount" select="0" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="$transferCount" />
								</xsl:apply-templates>
							</tbody>
						</table>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="0 &lt; $paymentCount">
						<xsl:apply-templates select="payment_date">
							<xsl:with-param name="speccode" select="$speccode" />
							<xsl:with-param name="service_channel_name"
								select="@value" />
							<xsl:with-param name="osb" select="$osb" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="0 &lt; $transferCount">
						<xsl:apply-templates select="transfer_date">
							<xsl:with-param name="speccode" select="$speccode" />
							<xsl:with-param name="service_channel_name"
								select="@value" />
							<xsl:with-param name="osb" select="$osb" />
							<xsl:with-param name="payment_date" select="''" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="0 &lt; count(item)">
						<xsl:apply-templates select="item">
							<xsl:with-param name="speccode" select="$speccode" />
							<xsl:with-param name="service_channel_name"
								select="@value" />
							<xsl:with-param name="osb" select="$osb" />
							<xsl:with-param name="payment_date" select="''" />
							<xsl:with-param name="transfer_date" select="''" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</xsl:when>
				</xsl:choose>
				<xsl:apply-templates select="total_summary">
					<xsl:with-param name="speccode" select="$speccode" />
					<xsl:with-param name="service_channel_name" select="@value" />
					<xsl:with-param name="osb" select="$osb" />
					<xsl:with-param name="payment_date" select="''" />
					<xsl:with-param name="transfer_date" select="''" />
					<xsl:with-param name="specCount" select="$specCount" />
					<xsl:with-param name="osbCount" select="$osbCount" />
					<xsl:with-param name="serviceCount" select="$serviceCount" />
					<xsl:with-param name="paymentCount" select="$paymentCount" />
					<xsl:with-param name="transferCount" select="$transferCount" />
				</xsl:apply-templates>
				<xsl:apply-templates select="summary">
					<xsl:with-param name="speccode" select="$speccode" />
					<xsl:with-param name="service_channel_name" select="@value" />
					<xsl:with-param name="osb" select="$osb" />
					<xsl:with-param name="payment_date" select="''" />
					<xsl:with-param name="transfer_date" select="''" />
					<xsl:with-param name="specCount" select="$specCount" />
					<xsl:with-param name="osbCount" select="$osbCount" />
					<xsl:with-param name="serviceCount" select="$serviceCount" />
					<xsl:with-param name="paymentCount" select="$paymentCount" />
					<xsl:with-param name="transferCount" select="$transferCount" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="payment_date">
		<xsl:param name="bik" select="''" />
		<xsl:param name="settleAcc" select="''" />
		<xsl:param name="start" select="''" />
		<xsl:param name="end" select="''" />
		<xsl:param name="specCount" select="''" />
		<xsl:param name="osbCount" select="''" />
		<xsl:param name="serviceCount" select="''" />
		<xsl:param name="paymentCount" select="''" />
		<xsl:param name="transferCount" select="''" />
		<xsl:param name="speccode" select="''" />
		<xsl:param name="osb" select="''" />
		<xsl:param name="service_channel_name" select="''" />
		<xsl:choose>
			<xsl:when
				test="($bik = '') and ($settleAcc = '') and ($specCount = 0) and ($osbCount = 0)">
				<tr>
					<td>
						<h4 style="margin-bottom: 3px;">
							<xsl:text>Дата платежа </xsl:text>
							<xsl:value-of select="@value" />
						</h4>
						<table border="1" cellspacing="0"
							style="text-align:right; table-layout:fixed;" cellpadding="3">
							<thead>
								<tr>
									<xsl:if test="0 &lt; $transferCount">
										<td style="text-align:center; width:7em">Дата перечисления</td>
									</xsl:if>
									<td style="text-align:center; width:5em">Кол-во документов</td>
									<td style="text-align:center; width:12em">Сумма платежей</td>
									<td style="text-align:center; width:12em">Сумма комиссии</td>
									<td style="text-align:center; width:12em">Сумма к перечислению</td>
								</tr>
							</thead>
							<tbody>
								<xsl:choose>
									<xsl:when test="0 &lt; $transferCount">
										<xsl:apply-templates select="transfer_date">
											<xsl:with-param name="speccode" select="$speccode" />
											<xsl:with-param name="osb" select="$osb" />
											<xsl:with-param name="payment_date" select="''" />
											<xsl:with-param name="specCount" select="$specCount" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="-1" />
											<xsl:with-param name="transferCount" select="$transferCount" />
										</xsl:apply-templates>
									</xsl:when>
									<xsl:when test="0 &lt; count(item)">
										<xsl:apply-templates select="item">
											<xsl:with-param name="speccode" select="$speccode" />
											<xsl:with-param name="osb" select="$osb" />
											<xsl:with-param name="payment_date" select="''" />
											<xsl:with-param name="transfer_date" select="''" />
											<xsl:with-param name="specCount" select="$specCount" />
											<xsl:with-param name="osbCount" select="$osbCount" />
											<xsl:with-param name="serviceCount" select="$serviceCount" />
											<xsl:with-param name="paymentCount" select="-1" />
											<xsl:with-param name="transferCount" select="$transferCount" />
										</xsl:apply-templates>
									</xsl:when>
								</xsl:choose>
								<xsl:apply-templates select="summary">
									<xsl:with-param name="speccode" select="$speccode" />
									<xsl:with-param name="osb" select="$osb" />
									<xsl:with-param name="service_channel_name" select="$service_channel_name" />
									<xsl:with-param name="payment_date" select="''" />
									<xsl:with-param name="transfer_date" select="''" />
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="-1" />
									<xsl:with-param name="transferCount" select="$transferCount" />
								</xsl:apply-templates>
								<xsl:apply-templates select="total_summary">
									<xsl:with-param name="speccode" select="$speccode" />
									<xsl:with-param name="osb" select="$osb" />
									<xsl:with-param name="service_channel_name" select="$service_channel_name" />
									<xsl:with-param name="payment_date" select="''" />
									<xsl:with-param name="transfer_date" select="''" />
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="serviceCount" select="$serviceCount" />
									<xsl:with-param name="paymentCount" select="-1" />
									<xsl:with-param name="transferCount" select="$transferCount" />
								</xsl:apply-templates>
							</tbody>
						</table>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="0 &lt; $transferCount">
						<xsl:apply-templates select="transfer_date">
							<xsl:with-param name="speccode" select="$speccode" />
							<xsl:with-param name="osb" select="$osb" />
							<xsl:with-param name="service_channel_name"
								select="$service_channel_name" />
							<xsl:with-param name="payment_date" select="@value" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="0 &lt; count(item)">
						<xsl:apply-templates select="item">
							<xsl:with-param name="speccode" select="$speccode" />
							<xsl:with-param name="osb" select="$osb" />
							<xsl:with-param name="service_channel_name"
								select="$service_channel_name" />
							<xsl:with-param name="payment_date" select="@value" />
							<xsl:with-param name="transfer_date" select="''" />
							<xsl:with-param name="specCount" select="$specCount" />
							<xsl:with-param name="osbCount" select="$osbCount" />
							<xsl:with-param name="serviceCount" select="$serviceCount" />
							<xsl:with-param name="paymentCount" select="$paymentCount" />
							<xsl:with-param name="transferCount" select="$transferCount" />
						</xsl:apply-templates>
					</xsl:when>
				</xsl:choose>
				<xsl:apply-templates select="summary">
					<xsl:with-param name="speccode" select="$speccode" />
					<xsl:with-param name="osb" select="$osb" />
					<xsl:with-param name="service_channel_name" select="$service_channel_name" />
					<xsl:with-param name="payment_date" select="@value" />
					<xsl:with-param name="transfer_date" select="''" />
					<xsl:with-param name="specCount" select="$specCount" />
					<xsl:with-param name="osbCount" select="$osbCount" />
					<xsl:with-param name="serviceCount" select="$serviceCount" />
					<xsl:with-param name="paymentCount" select="$paymentCount" />
					<xsl:with-param name="transferCount" select="$transferCount" />
				</xsl:apply-templates>
				<xsl:apply-templates select="total_summary">
					<xsl:with-param name="speccode" select="$speccode" />
					<xsl:with-param name="osb" select="$osb" />
					<xsl:with-param name="service_channel_name" select="$service_channel_name" />
					<xsl:with-param name="payment_date" select="@value" />
					<xsl:with-param name="transfer_date" select="''" />
					<xsl:with-param name="specCount" select="$specCount" />
					<xsl:with-param name="osbCount" select="$osbCount" />
					<xsl:with-param name="serviceCount" select="$serviceCount" />
					<xsl:with-param name="paymentCount" select="$paymentCount" />
					<xsl:with-param name="transferCount" select="$transferCount" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="transfer_date">
		<xsl:param name="bik" select="''" />
		<xsl:param name="settleAcc" select="''" />
		<xsl:param name="start" select="''" />
		<xsl:param name="end" select="''" />
		<xsl:param name="specCount" select="''" />
		<xsl:param name="osbCount" select="''" />
		<xsl:param name="serviceCount" select="''" />
		<xsl:param name="paymentCount" select="''" />
		<xsl:param name="transferCount" select="''" />
		<xsl:param name="speccode" select="''" />
		<xsl:param name="osb" select="''" />
		<xsl:param name="service_channel_name" select="''" />
		<xsl:param name="payment_date" select="''" />
		<xsl:choose>
			<xsl:when
				test="($bik = '') and ($settleAcc = '') and ($specCount = 0) and ($osbCount = 0) and ($serviceCount = 0) and ($paymentCount = 0)">
				<tr>
					<td>
						<h4 style="margin-bottom: 3px;">
							<xsl:text>Дата перечисления </xsl:text>
							<xsl:value-of select="@value" />
						</h4>
						<table border="1" cellspacing="0"
							style="text-align:right; table-layout:fixed;" cellpadding="3">
							<thead>
								<tr>
									<td style="text-align:center; width:5em">Кол-во документов</td>
									<td style="text-align:center; width:12em">Сумма платежей</td>
									<td style="text-align:center; width:12em">Сумма комиссии</td>
									<td style="text-align:center; width:12em">Сумма к перечислению</td>
								</tr>
							</thead>
							<tbody>
								<xsl:apply-templates select="item">
									<xsl:with-param name="speccode" select="$speccode" />
									<xsl:with-param name="osb" select="$osb" />
									<xsl:with-param name="service_channel_name"
										select="$service_channel_name" />
									<xsl:with-param name="payment_date" select="$payment_date" />
									<xsl:with-param name="transfer_date" select="''" />
								</xsl:apply-templates>
								<xsl:apply-templates select="summary">
									<xsl:with-param name="speccode" select="$speccode" />
									<xsl:with-param name="osb" select="$osb" />
									<xsl:with-param name="service_channel_name"
										select="$service_channel_name" />
									<xsl:with-param name="payment_date" select="$payment_date" />
									<xsl:with-param name="transfer_date" select="''" />
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="paymentCount" select="$paymentCount" />
									<xsl:with-param name="transferCount" select="-1" />
								</xsl:apply-templates>
								<xsl:apply-templates select="total_summary">
									<xsl:with-param name="speccode" select="$speccode" />
									<xsl:with-param name="osb" select="$osb" />
									<xsl:with-param name="service_channel_name"
										select="$service_channel_name" />
									<xsl:with-param name="payment_date" select="$payment_date" />
									<xsl:with-param name="transfer_date" select="''" />
									<xsl:with-param name="specCount" select="$specCount" />
									<xsl:with-param name="osbCount" select="$osbCount" />
									<xsl:with-param name="paymentCount" select="-1" />
									<xsl:with-param name="transferCount" select="-1" />
								</xsl:apply-templates>
							</tbody>
						</table>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="item">
					<xsl:with-param name="speccode" select="$speccode" />
					<xsl:with-param name="osb" select="$osb" />
					<xsl:with-param name="service_channel_name" select="$service_channel_name" />
					<xsl:with-param name="payment_date" select="$payment_date" />
					<xsl:with-param name="transfer_date" select="@value" />
				</xsl:apply-templates>
				<xsl:apply-templates select="summary">
					<xsl:with-param name="speccode" select="$speccode" />
					<xsl:with-param name="osb" select="$osb" />
					<xsl:with-param name="service_channel_name" select="$service_channel_name" />
					<xsl:with-param name="payment_date" select="$payment_date" />
					<xsl:with-param name="transfer_date" select="@value" />
					<xsl:with-param name="specCount" select="$specCount" />
					<xsl:with-param name="osbCount" select="$osbCount" />
					<xsl:with-param name="serviceCount" select="$serviceCount" />
					<xsl:with-param name="paymentCount" select="$paymentCount" />
					<xsl:with-param name="transferCount" select="$transferCount" />
				</xsl:apply-templates>
				<xsl:apply-templates select="total_summary">
					<xsl:with-param name="speccode" select="$speccode" />
					<xsl:with-param name="osb" select="$osb" />
					<xsl:with-param name="service_channel_name" select="$service_channel_name" />
					<xsl:with-param name="payment_date" select="$payment_date" />
					<xsl:with-param name="transfer_date" select="@value" />
					<xsl:with-param name="specCount" select="$specCount" />
					<xsl:with-param name="osbCount" select="$osbCount" />
					<xsl:with-param name="serviceCount" select="$serviceCount" />
					<xsl:with-param name="paymentCount" select="$paymentCount" />
					<xsl:with-param name="transferCount" select="$transferCount" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="item">
		<xsl:param name="speccode" select="''" />
		<xsl:param name="osb" select="''" />
		<xsl:param name="service_channel_name" select="''" />
		<xsl:param name="payment_date" select="''" />
		<xsl:param name="transfer_date" select="''" />
		<tr>
			<xsl:choose>
				<xsl:when test="$speccode = ''">
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="position() = 1">
							<td
								style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
								<xsl:value-of select="$speccode" />
							</td>
						</xsl:when>
						<xsl:otherwise>
							<td
								style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;"></td>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$osb = ''">
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="position() = 1">
							<td
								style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
								<xsl:value-of select="$osb" />
							</td>
						</xsl:when>
						<xsl:otherwise>
							<td
								style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;"></td>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$service_channel_name = ''">
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="position() = 1">
							<td
								style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
								<xsl:value-of select="$service_channel_name" />
							</td>
						</xsl:when>
						<xsl:otherwise>
							<td
								style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;"></td>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$payment_date = ''">
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="position() = 1">
							<td
								style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
								<xsl:value-of select="$payment_date" />
							</td>
						</xsl:when>
						<xsl:otherwise>
							<td
								style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;"></td>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="$transfer_date = ''">
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="position() = 1">
							<td
								style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
								<xsl:value-of select="$transfer_date" />
							</td>
						</xsl:when>
						<xsl:otherwise>
							<td
								style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;"></td>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			<td
				style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="name-format">
					<xsl:with-param name="n" select="counter" />
					<xsl:with-param name="pad" select="10" />
				</xsl:call-template>
			</td>
			<td
				style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="s-format">
					<xsl:with-param name="s" select="sum" />
					<xsl:with-param name="pad" select="17" />
				</xsl:call-template>
			</td>
			<td
				style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="s-format">
					<xsl:with-param name="s" select="serviceComm" />
					<xsl:with-param name="pad" select="17" />
				</xsl:call-template>
			</td>
			<td
				style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="s-format">
					<xsl:with-param name="s" select="sum - serviceComm" />
					<xsl:with-param name="pad" select="17" />
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="summary">
		<xsl:param name="specCount" select="''" />
		<xsl:param name="osbCount" select="''" />
		<xsl:param name="serviceCount" select="''" />
		<xsl:param name="paymentCount" select="''" />
		<xsl:param name="transferCount" select="''" />
		<xsl:param name="speccode" select="''" />
		<xsl:param name="osb" select="''" />
		<xsl:param name="service_channel_name" select="''" />
		<xsl:param name="payment_date" select="''" />
		<xsl:param name="transfer_date" select="''" />
		<tr>
			<xsl:choose>
				<xsl:when test="($specCount &lt; 0) or ($specCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($osbCount &lt; 0) or ($osbCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($serviceCount &lt; 0) or ($serviceCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($paymentCount &lt; 0) or ($paymentCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($transferCount &lt; 0) or ($transferCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<td
				style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="name-format">
					<xsl:with-param name="n" select="counter" />
					<xsl:with-param name="pad" select="10" />
				</xsl:call-template>
			</td>
			<td
				style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="s-format">
					<xsl:with-param name="s" select="sum" />
					<xsl:with-param name="pad" select="17" />
				</xsl:call-template>
			</td>
			<td
				style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="s-format">
					<xsl:with-param name="s" select="serviceComm" />
					<xsl:with-param name="pad" select="17" />
				</xsl:call-template>
			</td>
			<td
				style="border-bottom-width:0px; border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="s-format">
					<xsl:with-param name="s" select="sum - serviceComm" />
					<xsl:with-param name="pad" select="17" />
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="total_summary">
		<xsl:param name="specCount" select="''" />
		<xsl:param name="osbCount" select="''" />
		<xsl:param name="serviceCount" select="''" />
		<xsl:param name="paymentCount" select="''" />
		<xsl:param name="transferCount" select="''" />
		<tr>
			<td
				style="border-bottom-width:0px; border-left-width:0px; border-right-width:0px; text-align:left; padding-left:1em;"
				colspan="4">
				<xsl:text>Итого </xsl:text>
			</td>
			<xsl:choose>
				<xsl:when test="($specCount &lt; 0) or ($specCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-bottom-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($osbCount &lt; 0) or ($osbCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-bottom-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($serviceCount &lt; 0) or ($serviceCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-bottom-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($paymentCount &lt; 0) or ($paymentCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-bottom-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($transferCount &lt; 0) or ($transferCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-bottom-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
		</tr>
		<tr>
			<xsl:choose>
				<xsl:when test="($specCount &lt; 0) or ($specCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($osbCount &lt; 0) or ($osbCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($serviceCount &lt; 0) or ($serviceCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($paymentCount &lt; 0) or ($paymentCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="($transferCount &lt; 0) or ($transferCount = 0)">
				</xsl:when>
				<xsl:otherwise>
					<td
						style="border-top-width:0px; border-left-width:0px; border-right-width:0px; text-align:center;">
					</td>
				</xsl:otherwise>
			</xsl:choose>
			<td
				style="border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="name-format">
					<xsl:with-param name="n" select="counter" />
					<xsl:with-param name="pad" select="10" />
				</xsl:call-template>
			</td>
			<td
				style="border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="s-format">
					<xsl:with-param name="s" select="sum" />
					<xsl:with-param name="pad" select="17" />
				</xsl:call-template>
			</td>
			<td
				style="border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="s-format">
					<xsl:with-param name="s" select="serviceComm" />
					<xsl:with-param name="pad" select="17" />
				</xsl:call-template>
			</td>
			<td
				style="border-top-width:0px; border-left-width:0px; border-right-width:0px;">
				<xsl:call-template name="s-format">
					<xsl:with-param name="s" select="sum - serviceComm" />
					<xsl:with-param name="pad" select="17" />
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>