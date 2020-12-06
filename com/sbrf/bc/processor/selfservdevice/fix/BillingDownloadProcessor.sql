select 
    P.LINUM, 
    P.LINPU, 
    P.DOPID, 
    P.DATEP, 
    P.TIMEP, 
    P.BILLING_DATE_TIME,     
    case when P.NOOSB in (''9013'',''3869'',''0193'',''3825'',''3777'',''0382'',''0989'',''3836'',''3872'',''3854'',''3793'')  then ''9013''      
		 when P.NOOSB in (''8594'',''3912'',''3826'',''3773'',''0141'',''3884'') then ''8594''
		 when P.NOOSB in (''8592'') then ''8592''
		 when P.NOOSB in (''8593'') then ''8593''
		 when P.NOOSB in (''8595'') then ''8595''
		 when P.NOOSB in (''8596'') then ''8596''
         else ''9013'' end as NOOSB,
    P.NOFIL, 
    P.NOCAS, 
    P.NPDOC, 
    ''3'' as TYPLA, 
    P.STATUS, 
    P.SUMPL, 
    P.SUMPU, 
    P.SUPEN, 
    P.PAYER_COMMISSION as SUMAG, 
    P.SUMAG as PAYER_COMMISSION, 
    P.RNUM, 
    P.RLINE,	
	cast(
          substr(plinf,
            case
              when LOCATE(''@'', plinf, LOCATE(''@'', plinf) + 1) + 1 < 0 then length(plinf)
              else LOCATE(''@'', plinf, LOCATE(''@'', plinf) + 1) + 1
            end
        ) as varchar(1024))  as PLINF,	
    P.NOMPU, 
    P.NOMDP, 
    ''0'' as NOMUS, 
    P.BIK, 
    P.CORR_ACC, 
    P.SETTLE_ACC, 
    P.PROVIDER_INFO, 
    P.IN_REG_ID, 
    P.IN_REG_LINE, 
    P.TRANSFER_DATE, 
    P.INN, 
    P.KPP,     
	'''' as KBK,
	'''' as OKATO,
    P.PAYMENT_ORDER, 
    D.RFNAM as RECEIVER_NAME,
    CAST(substr(P.DOPID,8,9) as integer) as RECEIVER_NUMBER,   
    D.CSPEC as SPECIAL_CLIENT_CODE,
    PROPS.NAME, PROPS.VALUE
from RG.PLA as P
left join RG.PROPS as PROPS
  on P.LINUM = PROPS.OBJECT_ID
 and PROPS.OBJECT_TYPE = 3
left join RG.DPU as D
  on D.NOMPU = P.NOMPU
 and D.NOMDP = P.NOMUS
left join RG.SPU as S
  on S.NOMPU = P.NOMPU
where 
    (P.IN_REG_LINE = -44) and     
    (P.NOMPU <> 9000000) and 
	P.NOOSB in (''9013'',''3869'',''0193'',''3825'',''3777'',''0382'',''0989'',''3836'',''3872'',''3854'',''3793'') and	
P.DATEP = ''{0,date,yyyy-MM-dd}''
  {1}
order by P.NOOSB, P.LINUM 