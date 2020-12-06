-- 0 логин 1 хэш первоначального пароля  2 ФИО пользователя 3 начальная дата действия пароля 4 конечная дата действия пароля 5 дополнительная информация, должность сотрудника
insert into ADM.UCONF(UCONF_LOG, UCONF_PASS, UCONF_NAME, UCONF_STATUS, UCONF_DSTART, UCONF_DEND, UCONF_PTTZ, UCONF_PASSST, UCONF_INFORM, CTRL, ACTID)
 values (''{0}'',''{1}'',''{2}'',1, ''{3,date,yyyy-MM-dd}'', ''{4,date,yyyy-MM-dd}'' , 0, 1, ''{5}'',41,0)
