select
    TRANSFER_DATE,
    sum(SUM) as SUM,
    sum(SERVICE_COMMISSION) as SERVICE_COMMISSION,
	sum(PAYER_COMMISSION) as PAYER_COMMISSION,
    count(*) as COUNTER
    {0}
from PAY.PAYMENTS P
{6}
where TRANSFER_DATE >= ''{2,date,yyyy-MM-dd}''
    and TRANSFER_DATE <= ''{3,date,yyyy-MM-dd}''
    {4}
group by TRANSFER_DATE {5}
{1} 
          