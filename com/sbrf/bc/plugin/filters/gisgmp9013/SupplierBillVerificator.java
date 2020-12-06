package com.sbrf.bc.plugin.filters.gisgmp9013;

import java.util.Map;
import java.util.Set;

public class SupplierBillVerificator {
    
    private Set<Character> reqsCheckCharsSet;
    private Map<Character, Integer> reqsCheckCrcCharsMap;
    
    public SupplierBillVerificator(Set<Character> reqsCheckCharsSet, Map<Character, Integer> reqsCheckCrcCharsMap) {
        this.reqsCheckCharsSet = reqsCheckCharsSet;
        this.reqsCheckCrcCharsMap = reqsCheckCrcCharsMap;
    }
    
    /**
     * Расчет контрольного разряда (описание приведенио в рекомендации для
     * тербанков)
     * 
     * @param str
     * @param charsMap
     * @return
     * @throws IllegalArgumentException  если содержится некорректный символ в строке
     */
    public int calculateControlChar(CharSequence str, Map<Character, Integer> charsMap)
            throws IllegalArgumentException {
        int weight = 1;
        int result = 0;
        int tryCount = 0;
        int num = 0;
        char c = 0;
        do {
            result = 0;
            if (tryCount > 0) {
                weight = 3;
            }
            for (int i = 0; i < str.length(); i++) {
                c = str.charAt(i);
                if (c < '0' || c > '9') {
                    try {
                        num = charsMap.get(c) % 10;
                    } catch (NullPointerException e) {
                        throw new IllegalArgumentException("Symbol code not found: " + c);
                    }
                } else {
                    num = c - '0';
                }
                result += weight * num;
                if (++weight == 11) {
                    weight = 1;
                }
            }
            result = result % 11;
        } while (tryCount++ < 1 && result == 10);
        return result%10;
    }

    public boolean isValidControlChar(CharSequence supplierBillId, Map<Character, Integer> charsMap) {
        try {
            int crc = calculateControlChar(supplierBillId.subSequence(0,
                    supplierBillId.length() - 1), charsMap);
            int currenctCrc = Integer.valueOf((String) supplierBillId.subSequence(supplierBillId
                    .length() - 1, supplierBillId.length()));
            return crc == currenctCrc;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Проверка УИН (ГИБДД/ФНС)
     * 
     * @param supplierBillId
     * @return
     */
    public boolean isSupplierBillValid(String supplierBillId, int len, String prefix,
            int[] letCharsPos) {
        boolean isSuccess = true;
        isSuccess = (supplierBillId.length() == len);
        if (isSuccess) {
            isSuccess = supplierBillId.startsWith(prefix);
        }
        if (isSuccess) {
            if (letCharsPos != null) {
                for (int n : letCharsPos) {
                    isSuccess = reqsCheckCharsSet.contains(supplierBillId.charAt(n));
                    if (!isSuccess) {
                        break;
                    }
                }
            }
        }
        if (isSuccess) {
            isSuccess = isValidControlChar(supplierBillId, reqsCheckCrcCharsMap);
        }
        return isSuccess;
    }
}
