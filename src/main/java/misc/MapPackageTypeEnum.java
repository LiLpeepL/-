package misc;

import java.util.ArrayList;
import java.util.List;

public enum MapPackageTypeEnum {
    C("c"),
    Other("other"),
    ;

    private final String packageType;

    MapPackageTypeEnum(String packageType) {
        this.packageType = packageType;
    }

    public String getPackageType() {
        return packageType;
    }

    public static List<String> getValues() {
        List<String> result = new ArrayList<>();
        for (MapPackageTypeEnum mapPackageTypeEnum : values()) {
            result.add(mapPackageTypeEnum.packageType);
        }
        return result;
    }


    // 静态方法通过key获取对应的value
    public static String getValueByKey(String key) {
        try {
            return MapPackageTypeEnum.valueOf(key.substring(0,1).toUpperCase()+key.substring(1).toLowerCase()).getPackageType();
        } catch (IllegalArgumentException e) {
            return MapPackageTypeEnum.Other.getPackageType();
        }
    }


}
