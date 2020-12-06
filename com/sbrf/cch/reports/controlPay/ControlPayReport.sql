With A as (select P.transfer_date, P.state, P.osb,  P.sum, P.payer_commission as sum_payer, P.service_commission as sum_org, P.payment_order,
                  case when P.special_client_code = ''00847'' then ''��� ''
                       when P.settle_acc = ''40702810807000103751'' then ''���� ''
                       else '''' end as special_client_code
                  from pay.payments as P 
				  where P.transfer_date = ''{inputDate1,date,yyyy-MM-dd}'' and P.state <> ''REVERSED'' 
				  ) 
select A.transfer_date, 
 case 
   when A.state = ''TRANSFERRED''  then A.special_client_code || ''�� ������������'' 
   when A.state = ''TRANSFERRED_MANUALLY'' then A.special_client_code || ''���������� � ������'' 
   when A.state = ''RETRANSFERRED'' then A.special_client_code || ''�� ��������� ������������'' 
   when A.state = ''TO_ASCERTAIN'' then A.special_client_code || ''�� ���������'' 
   when A.state = ''RETURNED_TO_PAYER'' then A.special_client_code || ''���������� �����������'' 
   when A.state = ''RETURNED'' then A.special_client_code || ''����������'' 
   when A.state = ''READY_TO_TRANSFER'' then A.special_client_code || ''������ � ������������'' 
   else A.state end as STATE,
   A.osb, count(*) as kol, count(distinct A.payment_order) as kolPP, CAST(sum(A.sum)/100.00 as NUMERIC(12,2)) as sum, CAST(sum(A.sum_payer)/100.00 as NUMERIC(12,2)) as sum_payer, CAST(sum(A.sum_org)/100.00 as NUMERIC(12,2)) as sum_org  from A
   group by A.transfer_date, A.state, A.osb, A.special_client_code
 