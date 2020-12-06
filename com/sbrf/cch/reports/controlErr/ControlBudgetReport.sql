With A as (
select distinct 
       case when ( (pay.r_maket.kbk is null or length(pay.r_maket.kbk)<20) and pay.r_maket.corr_acc = ''0''  and (pay.payments.special_client_code not in ({uslWhere})) and substr(pay.r_maket.settle_acc,1,5) != ''40302'' and substr(pay.r_maket.settle_acc,1,5) != ''40701'' ) then ''��� ���''
            when ( (length(pay.r_maket.okato)<>8 and length(pay.r_maket.okato)<>11) and pay.r_maket.corr_acc = ''0''  and (pay.payments.special_client_code not in ({uslWhere})) and substr(pay.r_maket.settle_acc,1,5) = ''40101'' ) then '' ��� �����''
            when ( (pay.r_maket.kpp is null or length(pay.r_maket.kpp)<9) and pay.r_maket.corr_acc = ''0''  and (pay.payments.special_client_code not in (''50330'')) and substr(pay.r_maket.settle_acc,1,5) != ''40302'' and substr(pay.r_maket.settle_acc,1,5) != ''40701'' ) then '' ��� ���''
            when ( pay.r_maket.payment_destination like  ''%'' || chr(9) || ''%'' ) then '' ����������� ������ ��������� � ����������''
            when ( pay.r_maket.payment_destination like  ''%'' || chr(10) || ''%'' ) then '' ����������� ������ �������� ������ � ����������''
            when ( pay.r_maket.payment_destination like  ''%'' || chr(13) || ''%'' ) then '' ����������� ������ �������� ������� � ����������''	
            when ( (pay.r_maket.taxes_reason like  ''%�%'') or (pay.r_maket.taxes_period like  ''%�%'') or (pay.r_maket.taxes_document_number like  ''%�%'') or (pay.r_maket.taxes_document_date like  ''%�%'') or (pay.r_maket.taxes_document_type like  ''%�%'')) then '' ����������� ������ � � ��������� �����''				
            when ( pay.r_maket.inn <> pay.payments.inn) then '' ��������! �� �������� ��� ���������� � ��������� ��������� '' || 	pay.r_maket.order_num || ''  '' ||	pay.r_maket.inn || '' � '' || pay.payments.inn		
            when ( pay.r_maket.kbk <> pay.payments.kbk) then '' ��������! �� �������� ��� ���������� � ��������� ��������� '' || 	pay.r_maket.order_num || ''  '' ||	pay.r_maket.kbk || '' � '' || pay.payments.kbk		
            when ( pay.r_maket.okato <> pay.payments.okato) then '' ��������! �� �������� ����� ���������� � ��������� ��������� '' || 	pay.r_maket.order_num || ''  '' ||	pay.r_maket.okato || '' � '' || pay.payments.okato
            when ( pay.r_maket.kpp <> pay.payments.kpp) then '' ��������! �� �������� ��� ���������� � ��������� ��������� '' || 	pay.r_maket.order_num || ''  '' ||	pay.r_maket.kpp || '' � '' || pay.payments.kpp			
            when ( pay.r_maket.payer_settle_acc  <> pay.payments.tel) then '' ��������! �� �������� ���������� ���� � ��������� ��������� '' || 	pay.r_maket.order_num  || ''  '' ||	pay.r_maket.payer_settle_acc || '' � '' || pay.payments.tel
            when ( ( pay.payments.pay_order_algorithm like ''COMMMON%BU%9013'' or pay.payments.pay_order_algorithm like ''TAXES%9013'' ) and ( pay.r_maket.creator_status <> ''15'' or pay.r_maket.creator_status is null) ) then '' ��������! ������ � ���������� ���� ������ ����������� ''
--            when ( pay.payments.sum > 100 and pay.payments.payer_commission = 0 and pay.payments.service_commission = 0 and pay.payments.corr_acc <> ''0'' and pay.payments.inn not in ({uslInn}) and pay.payments.special_client_code not in (''71400'')) then '' ��� �������� � ���������� ��� '' || pay.payments.inn || '' '' || pay.payments.receiver_name			
else '''' end as DESCRIPTION_ERR,
pay.r_maket.payer_info, 
pay.r_maket.payment_destination, 
pay.payments.osb, pay.payments.special_client_code ,
pay.r_maket.order_num
from pay.r_maket 
left join pay.payments on pay.r_maket.order_num=pay.payments.payment_order and pay.r_maket.payment_date=pay.payments.transfer_date 
where pay.r_maket.payment_date = ''{inputDate1,date,yyyy-MM-dd}''  
)
select A.* from A 
where length(DESCRIPTION_ERR)>0
order by DESCRIPTION_ERR