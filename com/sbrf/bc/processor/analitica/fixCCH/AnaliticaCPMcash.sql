-- �������� cpm ����� ��� ��� �� �������� �� ��������
-- 24.02.2015 ��� ������� ����� �� ��������� ������� payment_means_alias = 'encash' � ��� �������� 5
select 
(P.SUM + P.PAYER_COMMISSION) as SUM,
P.payment_date as PAYMENT_DATE,
'' as CARD,
'' as CODAUTH,
P.CASHIER AS NUMBER_US,
P.PAYMENT_TIME AS TIME,
(P.SETTLE_ACC || ' ID=' || P.LINUM) as COMMENT,
P.OSB as OSB
from pay.payments as P
where 
	P.TRANSFER_DATE = ?
	and ( payment_type = 5 and payment_means_alias = 'encash' )
	and P.STATE <> 'REVERSED'
order by OSB