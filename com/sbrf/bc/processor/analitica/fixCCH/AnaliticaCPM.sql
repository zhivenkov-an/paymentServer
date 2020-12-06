-- выгрузка cpm файла для УФО (см. условие CASHIER = 99995) если нужен весь ериб ЕРИБ (payment_type = 8)
-- 24.02.2015 для наличке через УС добавлено условия payment_means_alias = 'encash' и тип платежей 5
select 
(P.SUM + P.PAYER_COMMISSION) as SUM,
P.payment_date as PAYMENT_DATE,
R2.value as CARD,
R.value as CODAUTH,
'' AS NUMBER_US,
'' AS TIME,
(P.SETTLE_ACC || ' ID=' || P.LINUM) as COMMENT,
P.OSB as OSB
from pay.payments as P
 left outer join
 RG.PROPS as R 
 on P.LINUM = R.OBJECT_ID
 left outer join
 RG.PROPS as R2 
 on P.LINUM = R.OBJECT_ID 
 and R2.OBJECT_ID = R.OBJECT_ID 
where 
	P.TRANSFER_DATE = ?
--	and P.STATE = ?
--	and P.ROUTE <> 'MANUAL'
    and  R.name  = 'authcode' 
	and R2.name = 'cardinfo'
--	and ( P.CASHIER = '99995' or (P.payment_type=9 and P.tel like '30233%' and P.filial not in (90134,85924,85934,85924,85954,85964,99999)) )
--	and  P.CASHIER = '99995'
	and (( payment_type in (5,6,7,8,9) and payment_means_alias = 'bank_card' ) or P.CASHIER = '99995')
	and P.STATE <> 'REVERSED'
order by OSB