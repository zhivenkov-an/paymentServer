with
	T1(NAMEORG, COUNTER, PERCENT {2}) AS (
	select
		RECEIVER_NAME AS NAMEORG,
		count(RECEIVER_NAME) AS COUNTER,
		RATES_CODE AS PERCENT
		{3}
	from 
		PAY.SPECIFIC_CLIENTS 
	where 
		{4}
	group by 
		{5} ,RECEIVER_NAME, RATES_CODE 
	order by 
		COUNTER DESC fetch first 1 rows only
	),

	T2(BDEBT {2}) AS (
	select 
		sum(SUM)*0.01 AS BDEBT
		{3}
	from
		PAY.PAYMENTS
	where	
		{4}
		AND TRANSFER_DATE >= ''{0,date,yyyy-MM-dd}'' AND 
		TRANSFER_DATE <= ''{1,date,yyyy-MM-dd}'' AND 
		PAYMENT_DATE < ''{0,date,yyyy-MM-dd}''
		
	group by 
		{5}
	),

	T3(ALLPLAT, COUNTER {2}) AS (
	select
		sum(SUM)*0.01 AS ALLPLAT,
		count(*) AS COUNTER
		{3}
	from 
		PAY.PAYMENTS
	where 
		{4} 
		AND PAYMENT_DATE >= ''{0,date,yyyy-MM-dd}'' 
		AND PAYMENT_DATE <= ''{1,date,yyyy-MM-dd}''
	group by 
		{5}
	),

	T4(ALLTRANS, PRICE {2}) AS (
	select
		sum(SUM)*0.01 AS ALLTRANS,
		sum(SERVICE_COMMISSION)*0.01 AS PRICE
		{3}	
	from 
		PAY.PAYMENTS
	where 
		{4} AND
		TRANSFER_DATE >= ''{0,date,yyyy-MM-dd}'' 
		AND TRANSFER_DATE <= ''{1,date,yyyy-MM-dd}''
		
	group by 
		{5}
	),

	T5(EDEBT {2}) AS (
	select 
		sum(SUM)*0.01 AS EDEBT
		{3}
	from
		PAY.PAYMENTS
	where 
		{4}
		AND PAYMENT_DATE <= ''{1,date,yyyy-MM-dd}''
		AND PAYMENT_DATE >= ''{0,date,yyyy-MM-dd}''
		AND TRANSFER_DATE > ''{1,date,yyyy-MM-dd}''   	
	group by 
		{5}
	),

	J1(NAMEORG, PERCENT, BDEBT {2}) AS (
	select
		T1.NAMEORG AS NAMEORG,
		T1.PERCENT AS PERCENT,
		T2.BDEBT AS BDEBT,
		{10}
	from 
		T1 LEFT OUTER JOIN T2
	on
		{6}
	),

	J2(NAMEORG, PERCENT, BDEBT, ALLPLAT, COUNTER {2}) AS (
	select
		J1.NAMEORG AS NAMEORG,
		J1.PERCENT AS PERCENT,
		J1.BDEBT AS BDEBT,
		T3.ALLPLAT AS ALLPLAT,
		T3.COUNTER AS COUNTER,
		{11}
	from
		J1 LEFT OUTER JOIN T3
	on
		{7}
	),

	J3(NAMEORG, PERCENT, BDEBT, ALLPLAT, COUNTER, ALLTRANS, PRICE {2}) AS (
	select
		J2.NAMEORG AS NAMEORG,
		J2.PERCENT AS PERCENT,
		J2.BDEBT AS BDEBT,
		J2.ALLPLAT AS ALLPLAT,
		J2.COUNTER AS COUNTER,
		T4.ALLTRANS AS ALLTRANS,
		T4.PRICE AS PRICE,
		{12}
	from
		J2 LEFT OUTER JOIN T4
	on
		{8}
	)
select 
	J3.NAMEORG AS NAMEORG,
	J3.PERCENT AS PERCENT,
	J3.BDEBT AS BDEBT,
	J3.ALLPLAT AS ALLPLAT,
	J3.COUNTER AS COUNTER,
	J3.ALLTRANS AS ALLTRANS,
	J3.PRICE AS PRICE,
	T5.EDEBT AS EDEBT
from
	J3 LEFT OUTER JOIN T5
on
	{9}