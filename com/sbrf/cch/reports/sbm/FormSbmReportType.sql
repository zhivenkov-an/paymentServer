select
    LINUM,  
    PAYMENT_DATE,  
	OSB,
	CASHIER,
	FILIAL,	
    SUM,   
   (SUM-SERVICE_COMMISSION) as SUM_SERVICE, 	
    SPECIAL_CLIENT_CODE,
	substr(tel,16,5) as tel,
	BIK,
	CORR_ACC,
	SETTLE_ACC,	
	INN,
	(PAYER_INFO || ''@'' || PAYER_ADDRESS) as PAYER_INFO,
	PAYMENT_KINDS_CODE,
	RECEIVER_NUMBER,
	(FILIAL || DOCUMENT_NUMBER || ''00000000000000'' || PAYMENT_ORDER) as DOCUMENT_INFO,
	ADDITIONAL_REQUISITES
from PAY.PAYMENTS
where TRANSFER_DATE = ''{0,date,yyyy-MM-dd}''
and SPECIAL_CLIENT_CODE = ''{1}''        
