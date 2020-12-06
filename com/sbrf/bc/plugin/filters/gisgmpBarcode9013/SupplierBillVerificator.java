package com.sbrf.bc.plugin.filters.gisgmpBarcode9013;

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
    /* Раскодировка Воронежского ШК
     * 
     */
    char getCharByCode(int code) {

        switch (code) {
            case 1040:
                return 'А';
            case 1072:
                return 'А';
            case 1041:
                return 'Б';
            case 1073:
                return 'Б';
            case 1042:
                return 'В';
            case 1074:
                return 'В';
            case 1043:
                return 'Г';
            case 1075:
                return 'Г';
            case 1044:
                return 'Д';
            case 1076:
                return 'Д';
            case 1045:
                return 'Е';
            case 1077:
                return 'Е';
            case 1025:
                return 'Ё';
            case 1105:
                return 'Ё';
            case 1046:
                return 'Ж';
            case 1078:
                return 'Ж';
            case 1047:
                return 'З';
            case 1079:
                return 'З';
            case 1048:
                return 'И';
            case 1080:
                return 'И';
            case 1049:
                return 'Й';
            case 1081:
                return 'Й';
            case 1050:
                return 'К';
            case 1082:
                return 'К';
            case 1051:
                return 'Л';
            case 1083:
                return 'Л';
            case 1052:
                return 'М';
            case 1084:
                return 'М';
            case 1053:
                return 'Н';
            case 1085:
                return 'Н';
            case 1054:
                return 'О';
            case 1086:
                return 'О';
            case 1055:
                return 'П';
            case 1087:
                return 'П';
            case 1056:
                return 'Р';
            case 1088:
                return 'Р';
            case 1057:
                return 'С';
            case 1089:
                return 'С';
            case 1058:
                return 'Т';
            case 1090:
                return 'Т';
            case 1059:
                return 'У';
            case 1091:
                return 'У';
            case 1060:
                return 'Ф';
            case 1092:
                return 'Ф';
            case 1061:
                return 'Х';
            case 1093:
                return 'Х';
            case 1062:
                return 'Ц';
            case 1094:
                return 'Ц';
            case 1063:
                return 'Ч';
            case 1095:
                return 'Ч';
            case 1064:
                return 'Ш';
            case 1096:
                return 'Ш';
            case 1065:
                return 'Щ';
            case 1097:
                return 'Щ';
            case 1066:
                return 'Ъ';
            case 1098:
                return 'Ъ';
            case 1067:
                return 'Ы';
            case 1099:
                return 'Ы';
            case 1068:
                return 'Ь';
            case 1100:
                return 'Ь';
            case 1069:
                return 'Э';
            case 1101:
                return 'Э';
            case 1070:
                return 'Ю';
            case 1102:
                return 'Ю';
            case 1071:
                return 'Я';
            case 1103:
                return 'Я';
        }
        return 'X';
    }

    
}
