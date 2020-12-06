select
    {0}, {1}, {2}
from
    RG.DOWNLOADS_PROPS as A
left outer join
    PAY.PAYMENTS as B
on
    B.SPECIAL_CLIENT_CODE = A.CSPEC
    and B.SERVICE_KIND = A.SERVICE_KIND
    and B.TRANSFER_DATE = ?
    and B.PAYMENT_ORDER is not null
    and B.STATE = ?
    and B.ROUTE <> ?
    and B.GROUP_ID = ?
left outer join
    PAY.R_MAKET as C
on
    C.PAYMENT_DATE = B.TRANSFER_DATE
    and C.ORDER_NUM = B.PAYMENT_ORDER
    and C.ORDER_SOURCE = B.PAYMENT_ORDER_SOURCE
where
    TYPE = ?
order by
    A.PROVIDER_CODE,
    A.PROVIDER_DEPARTMENT,
    B.TRANSFER_DATE,
    B.PAYMENT_ORDER_SOURCE,
    B.PAYMENT_ORDER,
    B.SERVICE_KIND,
    B.OSB,
    B.PAYMENT_DATE,
    B.FILIAL,
    B.DOCUMENT_NUMBER,
    B.CASHIER