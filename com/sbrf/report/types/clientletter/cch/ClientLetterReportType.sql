select
    P.LINUM,
    P.TRANSFER_DATE,
    P.PAYMENT_TYPE,
    P.PAYMENT_DATE,
    P.OSB,
    coalesce(SPR.SUBNAME, ''Отделение'') as OSB_NAME,
    coalesce(FIL.BRANCH_OLD, P.OSB) as OSB_OLD,
    P.FILIAL,
    P.CASHIER,
    P.DOCUMENT_NUMBER,
    P.PAYER_INFO,
    P.PAYER_ADDRESS,
    P.BIK,
    P.CORR_ACC,
    P.SETTLE_ACC,
    P.SUM,
    P.SERVICE_COMMISSION,
    P.INN,
    P.OKATO,
    P.KBK,
    P.RECEIVER_NAME
 from PAY.PAYMENTS as P
 left outer join PAY.FILIAL_TO_OSB as FIL
   on FIL.FILIAL = P.FILIAL
 left outer join NSI.SP as SPR
   on FIL.BRANCH_OLD = SPR.BRANCH
  and SPR.TERBANK = ?
  and SPR.SUBBRANCH = ''''
 where P.LINUM = ?    
 union all       
 select
    B.LINUM,
    B.TRANSFER_DATE,
    B.PAYMENT_TYPE,
    B.PAYMENT_DATE,
    B.OSB,
    coalesce(SPR.SUBNAME, ''Отделение'') as OSB_NAME,
    coalesce(FIL.BRANCH_OLD, B.OSB) as OSB_OLD,
    B.FILIAL,
    B.CASHIER,
    B.DOCUMENT_NUMBER,
    B.PAYER_INFO,
    B.PAYER_ADDRESS,
    B.BIK,
    B.CORR_ACC,
    B.SETTLE_ACC,
    B.SUM,
    B.SERVICE_COMMISSION,
    B.INN,
    B.OKATO,
    B.KBK,
    B.RECEIVER_NAME
 from PAY.BRAK as B
 left outer join PAY.FILIAL_TO_OSB as FIL
   on FIL.FILIAL = B.FILIAL
 left outer join NSI.SP as SPR
   on FIL.BRANCH_OLD = SPR.BRANCH
  and SPR.TERBANK = ?
  and SPR.SUBBRANCH = ''''
 where B.LINUM = ?
    
    