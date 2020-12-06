-- uconf_status = 1 нормальное состояние пользователя
-- uconf_passst=1 необходимо сменить пароль при входе
update adm.uconf set uconf_pass = ''{0}'', uconf_status = 1, uconf_passst = 1,uconf_dstart=''{2,date,yyyy-MM-dd}'',uconf_dend=''{3,date,yyyy-MM-dd}'' where uconf_log = ''{1}''

