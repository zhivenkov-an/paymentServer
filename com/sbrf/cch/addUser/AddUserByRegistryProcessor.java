package com.sbrf.cch.addUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import java.util.logging.Logger;
import java.util.regex.Matcher;

import com.sberbank.sbclients.admin.HashValueMaker;
import com.sberbank.sbclients.util.dao.ConnectionSource;
import com.sberbank.sbclients.util.dao.DAOException;

import com.sbrf.bc.processor.FileMetadata;
import com.sbrf.bc.processor.IncomingRegistryProcessor;
import com.sbrf.bc.processor.ProcessorException;
import com.sbrf.bc.processor.RegistryContext;

import com.sbrf.util.classloader.ResourceHelper;
import com.sbrf.util.text.MessageFormat;

public class AddUserByRegistryProcessor implements IncomingRegistryProcessor {

	protected final ConnectionSource connectionSource;
	private final AddUserConfig config;
	private final Logger logger;
	private final FileMetadata input;
	OutputStream outErrors;

	public AddUserByRegistryProcessor(ConnectionSource connectionSource,
			AddUserConfig config, Logger logger, FileMetadata input) {
		this.connectionSource = connectionSource;
		this.config = config;
		this.logger = logger;
		this.input = input;
	}

	public boolean accepts() {
		return true;
	}

	public FileMetadata[] process(RegistryContext context)
			throws ProcessorException {

		String passwdUser = "";
		String sql = "";
		Connection connection = null;
		connection = connectionSource.getConnection();
		PreparedStatement statement = null;
		String sqlFormat = ResourceHelper.getResourceAsString(config.sqlQueryPath);
		String sqlFormat2 = ResourceHelper.getResourceAsString(config.sqlQueryPath2);
		String sqlFormat3 = ResourceHelper.getResourceAsString(config.sqlQueryPath3);
		FileMetadata fileMetadaErrors = null;
		String heshLine = "";
		LineNumberReader in = null;

		logger.info("\n\n  ==ZHAN==: Запуск генерации хэш-пароля!");
		try {
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
 		    Date d = new Date();
			String fileNameErros = "passwd-" + format1.format(d) + ".out";
			passwdUser = "zgxv1234";
			Integer iCount = 0;
			Integer iAdd = 0;
			File fileErrors = File.createTempFile(fileNameErros, ".txt");
			fileMetadaErrors = new FileMetadata(fileErrors, "", fileNameErros);
			outErrors = new FileOutputStream(fileErrors);
			in = new LineNumberReader(new InputStreamReader(
					new FileInputStream(input.getCurrentFile()),
					config.fileEncoding));
			Matcher informationalLine = config.informationalRepeat.matcher("");
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c2.add(Calendar.DATE, config.validity);

			for (String line = in.readLine(); line != null; line = in.readLine()) {
				informationalLine.reset(line);
				iCount++;
				// config.informationalRepeat + "\n").getBytes());
				if (informationalLine.matches()) {
					if (informationalLine.group(2).equals(config.roleUser)) {
						String idZND = informationalLine.group(1);
						String loginName = informationalLine.group(3);
						heshLine = HashValueMaker.getInstance().makePasswordHashValue(loginName, passwdUser);
						String fioName = informationalLine.group(4);
						String department = ("заявка " + idZND + " " + informationalLine.group(5) + " " + informationalLine.group(6)).toString();
						String nameGroup = config.roleUser;						
						logger.info("\n для логина " + loginName
								+ " пароль " + passwdUser
								+ " хэш пароля " + heshLine
								+ " ФИО " + fioName
								+ " Начало " + c1.getTime()
								+ " Конец " + c2.getTime()
								+ " Доп.инфо " + department
								+ " срок действия " + config.validity
								+ " дней " + "\n"
								+ " sqlFormat" + sqlFormat);						
						// выполняем запрос на добавления в таблицу Uconf
						sql = MessageFormat.format(sqlFormat,new Object[] {loginName,heshLine,fioName,c1.getTime(),c2.getTime(),department});						
						logger.info("\n\n  ==ZHAN==: SQL1:\n" + sql);
						try {
							statement = connection.prepareStatement(sql);
							statement.executeUpdate(); // выполняеться при insert
							outErrors.write(("Заведено по заявке" + idZND
									+ " ФИО " +  fioName
							        + " логин " + loginName																			
									+ " срок действия пароля " + config.validity
									+ " дней " + "\n").getBytes());
							iAdd ++;
							statement.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							outErrors.write((" Не возможно добавить пользователя " + fioName
							        + " логин " + loginName											
									+ "\n").getBytes());
						}
						// выполняем запрос на добавления в таблицу Ugrus
						sql = MessageFormat.format(sqlFormat2, new Object[] {
								loginName, nameGroup });
						logger.info("\n\n  ==ZHAN==: SQL2:\n" + sql);
						try {
							statement = connection.prepareStatement(sql);
							statement.executeUpdate(); // выполняеться при insert
							statement.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// выполняем 3 запроса на добавления в таблицу Uright
						sql = MessageFormat.format(sqlFormat3, new Object[] {
								loginName, 0 });
						logger.info("\n\n  ==ZHAN==: SQL3.0:\n" + sql);
						try {
							statement = connection.prepareStatement(sql);
							statement.executeUpdate(); // выполняеться при insert
							statement.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sql = MessageFormat.format(sqlFormat3, new Object[] {loginName, 1 });
						logger.info("\n\n  ==ZHAN==: SQL3.1:\n" + sql);
						try {
							statement = connection.prepareStatement(sql);
							statement.executeUpdate(); // выполняеться при insert
							statement.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						sql = MessageFormat.format(sqlFormat3, new Object[] { loginName,2 });
						logger.info("\n\n  ==ZHAN==: SQL3.2:\n" + sql);
						try {
							statement = connection.prepareStatement(sql);
							statement.executeUpdate(); // выполняеться при insert
							statement.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}
			}
			outErrors.write(("\n ============================= \n"
			        + " Обработано строк = " + iCount
					+ "\n Заведено пользователей " + iAdd					
					+ "\n").getBytes());
			in.close();
			outErrors.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collection<FileMetadata> fileMetadatas = new ArrayList<FileMetadata>();
		fileMetadatas.add(fileMetadaErrors);
		return fileMetadatas.toArray(new FileMetadata[0]);

	}

}
