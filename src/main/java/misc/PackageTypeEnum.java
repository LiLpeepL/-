package misc;

import java.util.ArrayList;
import java.util.List;

public enum PackageTypeEnum {
    C("c"),
    Other("other"),
    Base("base"),
    Npm("npm"),
    Nuget("nuget"),
    Cargo("cargo"),
    Go("go"),
    Cocoapods("cocoapods"),
    Ruby("ruby"),
    Composer("composer"),
    Hex("hex"),
    Nvd("nvd"),
    Snyk("snyk"),
    Linux("linux");

    private final String packageType;

    PackageTypeEnum(String packageType) {
        this.packageType = packageType;
    }

    public String getPackageType() {
        return packageType;
    }

    public static List<String> getValues() {
        List<String> result = new ArrayList<>();
        for (PackageTypeEnum packageTypeEnum : values()) {
            result.add(packageTypeEnum.packageType);
        }
        return result;
    }


}
