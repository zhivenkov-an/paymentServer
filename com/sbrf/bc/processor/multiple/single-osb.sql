select
    {0}, {1}
from
    RG.DOWNLOADS_PROPS as A
left outer join
    PAY.PAYMENTS as B
on
    A.CSPEC = B.SPECIAL_CLIENT_CODE
    and A.SERVICE_KIND = B.SERVICE_KIND
    and B.TRANSFER_DATE = ?
    and B.STATE = ?
    and B.GROUP_ID = ?
    and B.ROUTE <> ?
where
    A.TYPE = ?
order by
    A.PROVIDER_CODE,
    A.PROVIDER_DEPARTMENT,
    A.SERVICE_KIND,
    B.OSB,
    B.PAYMENT_DATE,
    B.FILIAL,
    B.PAYMENT_TYPE,
    B.DOCUMENT_NUMBER