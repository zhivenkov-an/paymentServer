package com.sbrf.cch.zrp;

import java.io.File;
import java.io.FileInputStream;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import com.sberbank.sbclients.util.dao.ConnectionSource;


import com.sbrf.bc.processor.FileMetadata;
import com.sbrf.bc.processor.IncomingRegistryProcessor;
import com.sbrf.bc.processor.ProcessorException;
import com.sbrf.bc.processor.RegistryContext;

import com.sbrf.util.classloader.ResourceHelper;
import com.sbrf.util.text.MessageFormat;

public class ZrpByRegistryProcessor implements IncomingRegistryProcessor {

	protected final ConnectionSource connectionSource;
	private final ZrpConfig config;
	private final Logger logger;
	private final FileMetadata input;
	OutputStream outErrors;	
	private DocumentBuilder builder;
	

	public ZrpByRegistryProcessor(ConnectionSource connectionSource,
			ZrpConfig config, Logger logger, FileMetadata input) {
		this.connectionSource = connectionSource;
		this.config = config;
		this.logger = logger;
		this.input = input;
	}

	public boolean accepts() {
		return true;
	}

/*   public Document buildDocument()
	   {

	   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	       try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      Document doc = builder.newDocument();
	      Element svgElement = doc.createElement("svg");
	      doc.appendChild(svgElement);
	      svgElement.setAttribute("width", "" + 111);
	      svgElement.setAttribute("height", "" + 222);   



	         Element rectElement = doc.createElement("rect");
	         rectElement.setAttribute("x", "" + "perviy");
	         rectElement.setAttribute("y", "" + "vtoroy");
	         rectElement.setAttribute("width", "" + "tretiy");
	         rectElement.setAttribute("height", "" + "thetvertiy");
	         rectElement.setAttribute("fill", "pytiy");
	         svgElement.appendChild(rectElement);

	      return doc;
	   }*/

      
	public FileMetadata[] process(RegistryContext context)
			throws ProcessorException {


		String sql = "";
		Connection connection = null;
		connection = connectionSource.getConnection();
		PreparedStatement statement = null;
		String sqlFormat = ResourceHelper.getResourceAsString(config.sqlQueryPath);		
		FileMetadata fileMetadaErrors = null;
		FileMetadata fileMetadaXml = null;

		LineNumberReader in = null;

		logger.info("\n\n  ==ZHAN==: Запуск добавления строки по ЗРП!");
		try {
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat format3 = new SimpleDateFormat("MMyyyy");
 		    Date d = new Date();
			String fileNameErros = "kvit-" + format1.format(d) + ".out";			
			Integer iCount = 0;
			Integer iAdd = 0;
			File fileErrors = File.createTempFile(fileNameErros, ".txt");
			fileMetadaErrors = new FileMetadata(fileErrors, "", fileNameErros);
			outErrors = new FileOutputStream(fileErrors);
			in = new LineNumberReader(new InputStreamReader(
					new FileInputStream(input.getCurrentFile()),
					config.fileEncoding));
			Matcher informationalLine = config.informationalRepeat.matcher("");
			//Calendar c1 = Calendar.getInstance();
            // создание документа для DOM модели
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		       try {
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      Document doc = builder.newDocument();

		      Element rootElement = doc.createElement("СчетаПК");
			  doc.appendChild(rootElement);
			  rootElement.setAttribute("ДатаФормирования", "" + format2.format(d));
			  rootElement.setAttribute("НомерДоговора", "" + 123);
			  rootElement.setAttribute("НаименованиеОрганизации", "ООО Организация ИП" );
			  rootElement.setAttribute("ИдПервичногоДокумента", "" + 111);
			  rootElement.setAttribute("xmlns:uec", "http://www.uecard.ru/");
			  rootElement.setAttribute("xmlns", "create.claims.sbrf.ru");
			  Element openAccountElement = doc.createElement("ОткрытиеСчетов");
			  rootElement.appendChild(openAccountElement);
			  

			for (String line = in.readLine(); line != null; line = in.readLine()) {
				informationalLine.reset(line);
				iCount++;
				
				if (informationalLine.matches()) {	// разбираем входной файл регулярным выражением
						iAdd ++;
						String stSurname = informationalLine.group(1);
						String stName = informationalLine.group(2);
						String stMiddleName = informationalLine.group(3);
						String stOsb = informationalLine.group(4);
						String stFilial = informationalLine.group(5);
						String stKodVida = informationalLine.group(6);
						String stKodPodVida = informationalLine.group(7);
						String stKodValuta = informationalLine.group(8);						
						String stDul = informationalLine.group(9);
						String stSeriyaDul = informationalLine.group(10);
						String stNumberDul = informationalLine.group(11);
						String stDateDul = informationalLine.group(12);
						String stRowdDul = informationalLine.group(13);						
						String stKodRowdDul = informationalLine.group(14);						
						String stBirthDay = informationalLine.group(15);
						String stSex = informationalLine.group(16);						
						String stSityAddressPlaceOfBirth = informationalLine.group(17);
						String stIndexRegistration = informationalLine.group(18);
						String stCountryRegistration = informationalLine.group(19);
						String stNameRegionRegistration = informationalLine.group(20);
						String stNameClusterRegistration  = informationalLine.group(21);
						String stNameStreetRegistration = informationalLine.group(22);
						String stHomeRegistration = informationalLine.group(23);
						String stFrameRegistration = informationalLine.group(24);
						String stApartamentRegistration = informationalLine.group(25);
						String stFild1 = informationalLine.group(26);
						String stFild2 = informationalLine.group(27);
						String stRezident = informationalLine.group(28);
						String stNationality = informationalLine.group(29);
						String stContactMobilePhone = informationalLine.group(30);						
						String stContactInformation = informationalLine.group(31);
						String stCategory = informationalLine.group(32);
						String stMobilePhone = informationalLine.group(33);
						String stOperatorPhone = informationalLine.group(34);
						String stMobileBank = informationalLine.group(35);
						
						/*logger.info("\n для Фамилия " + stSurname		
								+ " Имя " + stName
								+ " Отчество " + stMiddleName								
								+ c1.getTime() + "\n"
								+ " sqlFormat" + sqlFormat);*/						
						// выполняем запрос на добавления в таблицу Employee
						sql = MessageFormat.format(sqlFormat,new Object[] {stSurname,stName,stMiddleName});
						// ====== добавляем элемент в DOM модель ==========================================================
				         Element employeeElement = doc.createElement("Сотрудник");
				         employeeElement.setAttribute("Нпп", "" + iAdd);
				         openAccountElement.appendChild(employeeElement);
				         
				         Element surnameElement = doc.createElement("Фамилия");
				         surnameElement.setTextContent(stSurname);
				         employeeElement.appendChild(surnameElement);
				         
				         Element nameElement = doc.createElement("Имя");
				         nameElement.setTextContent(stName);
				         employeeElement.appendChild(nameElement);
				         
				         Element middleNameElement = doc.createElement("Отчество");
				         middleNameElement.setTextContent(stMiddleName);
				         employeeElement.appendChild(middleNameElement);
				         
				         Element osbElement = doc.createElement("ОтделениеБанка");
				         osbElement.setTextContent(stOsb);
				         employeeElement.appendChild(osbElement);
				         
				         Element filialElement = doc.createElement("ФилиалОтделенияБанка");
				         filialElement.setTextContent(stFilial);
				         employeeElement.appendChild(filialElement);
				         
				         Element depositElement = doc.createElement("ВидВклада");
				         depositElement.setAttribute("КодВидаВклада", stKodVida);
				         depositElement.setAttribute("КодПодвидаВклада", stKodPodVida);
				         depositElement.setAttribute("КодВалюты", stKodValuta);
				         employeeElement.appendChild(depositElement);
				         
				         Element identityElement = doc.createElement("УдостоверениеЛичности");
				         	Element dulElement = doc.createElement("ВидДокумента");
				         	osbElement.setTextContent(stDul);
				         	identityElement.appendChild(dulElement);
				         	Element seriyaDulElement = doc.createElement("Серия");
				         	seriyaDulElement.setTextContent(stSeriyaDul);
				         	identityElement.appendChild(seriyaDulElement);
				         	Element numberDulElement = doc.createElement("Номер");
				         	numberDulElement.setTextContent(stNumberDul);
				         	identityElement.appendChild(numberDulElement);
				         	Element dateDulElement = doc.createElement("ДатаВыдачи");
				         	dateDulElement.setTextContent(stDateDul);
				         	identityElement.appendChild(dateDulElement);
				         	Element rowdDulElement = doc.createElement("КемВыдан");
				         	rowdDulElement.setTextContent(stRowdDul);
				         	identityElement.appendChild(rowdDulElement);
				         	Element kodRowdDulElement = doc.createElement("КодПодразделения");
				         	kodRowdDulElement.setTextContent(stKodRowdDul);
				         	identityElement.appendChild(kodRowdDulElement);
				         employeeElement.appendChild(identityElement);
				         
				         
				         Element birthDayElement = doc.createElement("ДатаРождения");
				         birthDayElement.setTextContent(stBirthDay);
				         employeeElement.appendChild(birthDayElement);
				         
				         Element sexElement = doc.createElement("Пол");
				         sexElement.setTextContent(stSex);
				         employeeElement.appendChild(sexElement);
				         
				         Element addressWorkElement = doc.createElement("АдресМестаРаботы");				         
				         employeeElement.appendChild(addressWorkElement);
				         
				         Element addressPlaceOfBirthElement = doc.createElement("МестоРождения");
				         	Element sityAddressPlaceOfBirthElement = doc.createElement("Город");
					         	Element nameSityAddressPlaceOfBirthElement = doc.createElement("ГородНазвание");
					         	nameSityAddressPlaceOfBirthElement.setTextContent(stSityAddressPlaceOfBirth);
					         	sityAddressPlaceOfBirthElement.appendChild(nameSityAddressPlaceOfBirthElement);
				         	addressPlaceOfBirthElement.appendChild(sityAddressPlaceOfBirthElement);				         	
				         employeeElement.appendChild(addressPlaceOfBirthElement);
				         
				         Element registrationElement = doc.createElement("АдресПрописки");
				         
				         	Element indexRegistrationElement = doc.createElement("Индекс");
				         	indexRegistrationElement.setTextContent(stIndexRegistration);
				         	registrationElement.appendChild(indexRegistrationElement);
				         	
				         	Element countryRegistrationElement = doc.createElement("Страна");
					         	Element NameCountryRegistrationElement = doc.createElement("СтранаНазвание");
					         	NameCountryRegistrationElement.setTextContent(stCountryRegistration);
					         	countryRegistrationElement.appendChild(NameCountryRegistrationElement);
				         	registrationElement.appendChild(countryRegistrationElement);
				         	
					         Element regionRegistrationElement = doc.createElement("Регион");
					         	Element NameRegionRegistrationElement = doc.createElement("РегионНазвание");
					         	NameRegionRegistrationElement.setTextContent(stNameRegionRegistration);
					         	regionRegistrationElement.appendChild(NameRegionRegistrationElement);				        
				         	registrationElement.appendChild(regionRegistrationElement);
			         	
				         	Element sityRegistrationElement = doc.createElement("Город");
				         		Element nameSityRegistrationElement = doc.createElement("ГородНазвание");
				         		nameSityRegistrationElement.setTextContent(stSityAddressPlaceOfBirth);
				         		sityRegistrationElement.appendChild(nameSityRegistrationElement);
				         	registrationElement.appendChild(sityRegistrationElement);				 
				         	
				         	Element clusterRegistrationElement = doc.createElement("НаселенныйПункт");
				         		Element nameClusterRegistrationElement = doc.createElement("НаселенныйПунктНазвание");
				         		nameClusterRegistrationElement.setTextContent(stNameClusterRegistration);
				         		clusterRegistrationElement.appendChild(nameClusterRegistrationElement);
			         		registrationElement.appendChild(clusterRegistrationElement);
			         		
			         		Element streetRegistrationElement = doc.createElement("Улица");
				         		Element nameStreetRegistrationElement = doc.createElement("УлицаНазвание");
				         		nameStreetRegistrationElement.setTextContent(stNameStreetRegistration);
				         		streetRegistrationElement.appendChild(nameStreetRegistrationElement);
			         		registrationElement.appendChild(streetRegistrationElement);
			         		
				         	Element homeRegistrationElement = doc.createElement("Дом");
				         	homeRegistrationElement.setTextContent(stHomeRegistration);
				         	registrationElement.appendChild(homeRegistrationElement);
				         	
				         	Element frameRegistrationElement = doc.createElement("Корпус");
				         	frameRegistrationElement.setTextContent(stFrameRegistration);
				         	registrationElement.appendChild(frameRegistrationElement);

				         	Element apartamentRegistrationElement = doc.createElement("Квартира");
				         	apartamentRegistrationElement.setTextContent(stApartamentRegistration);
				         	registrationElement.appendChild(apartamentRegistrationElement);
				         	
				         employeeElement.appendChild(registrationElement);
				        
				         Element embossirovannyiElement = doc.createElement("ЭмбоссированныйТекст");
				         embossirovannyiElement.setAttribute("Поле1", stFild1);				         
				         embossirovannyiElement.setAttribute("Поле2", stFild2);
				         employeeElement.appendChild(embossirovannyiElement);

				         Element rezidentElement = doc.createElement("Резидент");
				         rezidentElement.setTextContent(stRezident);
				         employeeElement.appendChild(rezidentElement);
				         
				         Element nationalityElement = doc.createElement("Гражданство");
				         nationalityElement.setTextContent(stNationality);
				         employeeElement.appendChild(nationalityElement);

				         Element contactMobilePhoneElement = doc.createElement("КонтактныйМобильныйТелефон");
				         contactMobilePhoneElement.setTextContent(stContactMobilePhone);
				         employeeElement.appendChild(contactMobilePhoneElement);
				         
				         Element contactInformationElement = doc.createElement("КонтрольнаяИнформация");
				         contactInformationElement.setTextContent(stContactInformation);
				         employeeElement.appendChild(contactInformationElement);

				         Element categoryElement = doc.createElement("КатегорияНаселения");
				         categoryElement.setTextContent(stCategory);
				         employeeElement.appendChild(categoryElement);
				         
				         Element bioElement = doc.createElement("НаличиеБИО");
				         bioElement.setTextContent("1");
				         employeeElement.appendChild(bioElement);
				         
				         Element pinElement = doc.createElement("НаличиеПИН");
				         pinElement.setTextContent("2");
				         employeeElement.appendChild(pinElement);

				         Element kodEmissionElement = doc.createElement("КодПричиныПеревыпуска");
				         kodEmissionElement.setTextContent("1");
				         employeeElement.appendChild(kodEmissionElement);


				         Element durationElement = doc.createElement("СрокДействия");
				         durationElement.setTextContent(format3.format(d));
				         employeeElement.appendChild(durationElement);

				         Element mobilePhoneElement = doc.createElement("МобильныйТелефон");
				         mobilePhoneElement.setTextContent(stMobilePhone);
				         employeeElement.appendChild(mobilePhoneElement);
				         
				         Element operatorPhoneElement = doc.createElement("ОператорСвязи");
				         operatorPhoneElement.setTextContent(stOperatorPhone);
				         employeeElement.appendChild(operatorPhoneElement);

				         Element mobileBankElement = doc.createElement("МобильныйБанк");
				         mobileBankElement.setTextContent(stMobileBank);
				         employeeElement.appendChild(mobileBankElement);
				         
				         
				         
						logger.info("\n\n  ==ZHAN==: SQL1:\n" + sql);
						try {
							statement = connection.prepareStatement(sql);
							statement.executeUpdate(); // выполняеться при insert
							outErrors.write(("\n Обработана строка " + 	iCount 
									+ " Фамилия " + stSurname		
									+ " Имя " + stName
									+ " Отчество " + stMiddleName + "\n").getBytes());
							
							statement.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							outErrors.write(("\n Не возможно добавить работника для Фамилия " + stSurname		
									+ " Имя " + stName
									+ " Отчество " + stMiddleName + "\n").getBytes());
						}				

				}
				else {
					outErrors.write(("\n Строка №  " + 	iCount							
							+ " не подходит по регулярному выражению " + config.informationalRepeat + "\n").getBytes());
				}
			}
			outErrors.write(("\n ============================= \n"
			        + " Обработано строк = " + iCount
					+ "\n Добавлено сотрудников " + iAdd					
					+ "\n").getBytes());
			in.close();
			outErrors.close();
			// ======================= добавляем поток выгрузки в DOM Дерева xml документа =================			  
			  String fileNameXml = "xml-" + format1.format(d) + ".xml";
			  File fileXml = File.createTempFile(fileNameXml, ".tmp");			  
			  fileMetadaXml = new FileMetadata(fileXml, "", fileNameXml);
		      FileOutputStream fos = new FileOutputStream(fileXml);
		      StreamResult result = new StreamResult(fos);
			  
		      Transformer t;			  
			try {
				  t = TransformerFactory.newInstance().newTransformer();
			      //t.setOutputProperty("doctype-system", "http://www.w3.org/TR/2000/CR-SVG-20000802/DTD/svg-20000802.dtd");
			      //t.setOutputProperty("doctype-public", "-//W3C//DTD SVG 20000802//EN");
			      DOMSource source  = new DOMSource(doc);			      
			      t.transform(source , result);
			      fos.close();
			      logger.info("\n\n  ==ZHAN==: трансформировали DOM модель в XML файл:\n" + fileNameXml);
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block			
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      
		      
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collection<FileMetadata> fileMetadatas = new ArrayList<FileMetadata>();
		fileMetadatas.add(fileMetadaErrors);
		logger.info("\n\n  ==ZHAN==: Добавили протокол загрузки:\n" + fileMetadaErrors);		
		fileMetadatas.add(fileMetadaXml);
		logger.info("\n\n  ==ZHAN==: Добавили xml файл :\n" + fileMetadaXml);
		return fileMetadatas.toArray(new FileMetadata[1]);

	}

}
