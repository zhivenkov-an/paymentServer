-- 0 логин 1 флаги назначенных прав. Передается значения 0,1,2
insert into ADM.URIGHT (UID, COD, TYP, ACTID)
    select UCONF_NUM, ADM.GRIGHT.COD, ''{1}'', 0
    from ADM.UCONF, ADM.GRIGHT
    where UCONF_LOG = ''{0}'' and CTRL1=''013'' and CTRL2=''0'' and CTRL3=''0'' and CTRL4=''0''
