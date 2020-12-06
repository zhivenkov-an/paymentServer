select ORDER_NUM from pay.r_maket left join pay.payments on pay.r_maket.order_num=pay.payments.payment_order and pay.r_maket.payment_date=pay.payments.transfer_date  where pay.r_maket.PAYMENT_DATE = ''{0,date,yyyy-MM-dd}'' and pay.payments.special_client_code in ({1}) group by pay.r_maket.ORDER_NUM 

