-- 0 логин 1 наименование группы

insert into ADM.UGRUS (UGRUS_USER, UGRUS_GROUP, ACTID)
    select UCONF_NUM, UGR_ID, 0
    from ADM.UCONF, ADM.UGR
    where UCONF_LOG=''{0}'' and UGR_NAME=''{1}''
