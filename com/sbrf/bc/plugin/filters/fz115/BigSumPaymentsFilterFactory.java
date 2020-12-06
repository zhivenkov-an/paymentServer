package com.sbrf.bc.plugin.filters.fz115;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.epam.sbrf.bc.data.BrakCode;
import com.epam.sbrf.bc.data.BrakValue;
import com.epam.sbrf.bc.data.PaymentData;
import com.epam.sbrf.bc.plugin.PaymentsFilter;
import com.epam.sbrf.bc.plugin.PaymentsFilterFactory;
import com.epam.sbrf.exception.FilterException;
import com.epam.sbrf.payorder.data.FormAlgorithmType;
import com.sberbank.sbclients.util.dao.J2EEContainerConnectionSource;
import com.sbrf.bc.dictionary.PaymentOrderAlgorithmCachingDictionary;
import com.sbrf.bc.plugin.PluginConfigurationHelper;

public class BigSumPaymentsFilterFactory implements PaymentsFilterFactory {

    private final Config config;

    public BigSumPaymentsFilterFactory(Properties properties) {
	PluginConfigurationHelper context = new PluginConfigurationHelper(properties);
	config = new Config(context);
    }

    public PaymentsFilter createPaymentsFilter() {
	return new PaymentsFilterImpl(config);
    }

    private static final class PaymentsFilterImpl implements PaymentsFilter {

	private PaymentOrderAlgorithmCachingDictionary algorithmsDictionary;

	Config config;

	PaymentsFilterImpl(Config config) {
	    this.config = config;

	}

	@Override
	public BrakValue[] check(PaymentData paymentData, BrakValue[] brakArray) throws FilterException {
	    List<BrakValue> braks = new ArrayList<BrakValue>(Arrays.asList(brakArray));
	    try {
		if ((paymentData.getSum() > config.sum)
		        && (algorithmsDictionary.getPaymentOrderAlgorithm(paymentData.getPayOrderAlgorithm())).getType() == FormAlgorithmType.SUMMARY_PAY_ORDER_ALGORIHTM
		                .getCode()) {

		    if (config.brakPayments) {
			braks.add(new BrakValue(config.brakCode, "Большая сумма и алгоритм формирования сводный",
			        "Большая сумма", false));
		    }
// если флаг замены алгоритма установлен и поля идентификации заполнены
		    if (config.changeAlgorithm && paymentData.getPayerInfo() != null && !paymentData.getPayerInfo().equals("") && paymentData.getPayerAddress() != null && !paymentData.getPayerAddress().equals("")) {
			if ((paymentData.getKbk() != null && !paymentData.getKbk().equals(""))
			        || (paymentData.getPersonalAccount() != null && !paymentData.getPersonalAccount().equals(""))) {
			    paymentData.setPayOrderAlgorithm(config.singleBudget);
			} else {
			    paymentData.setPayOrderAlgorithm(config.singleNotBudget);

			}

		    }

		}
	    } catch (SQLException e) {
		throw new FilterException(e);
	    }

	    return braks.toArray(brakArray);
	}

	@Override
	public void close() throws FilterException {
	    algorithmsDictionary.close();

	}

	@Override
	public void init() throws FilterException {
	    algorithmsDictionary = new PaymentOrderAlgorithmCachingDictionary(new J2EEContainerConnectionSource());
	    algorithmsDictionary.init();

	}

    }

    private static final class Config {
	final long sum;
	final boolean brakPayments;
	final int brakCode;

	final boolean changeAlgorithm;

	String singleNotBudget;
	String singleBudget;

	Config(PluginConfigurationHelper context) {
	    sum = context.getInteger("sum", 1500000);
	    brakPayments = context.getBoolean("brakPayments", false);
	    brakCode = context.getInteger("brakCode", BrakCode.CUSTOM_BRAK);
	    changeAlgorithm = context.getBoolean("changeAlgorithm", true);
	    singleNotBudget = context.getString("singleNotBudget", "SINGLE_9013");
	    singleBudget = context.getString("singleBudget", "FT296_BUDGET_9013");

	}

    }

}
