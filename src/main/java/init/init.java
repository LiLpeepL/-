package init;

import VersionBase.BaseItem.Item;
import VersionBase.BaseVersion.Version;
import VersionBase.LjVersion;
import expression.Impl.VersionCExpression;
import expression.VersionExpression;
import misc.MapPackageTypeEnum;
import misc.PackageTypeEnum;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class init {

    private static final Map<String, Class<? extends Version>> Package_CLASSES;
    private static final Map<String, VersionExpression> EXPRESSION_CLASSES;

    private static final Map<String, Class<LjVersion>> LJ_VERSION_CLASS;


    static {
        EXPRESSION_CLASSES = new HashMap<>();
        EXPRESSION_CLASSES.put(PackageTypeEnum.C.getPackageType(), new VersionCExpression());

    }

    static {
        Package_CLASSES = new HashMap<>();
        Package_CLASSES.put(PackageTypeEnum.C.getPackageType(), Version.class);
        Package_CLASSES.put(PackageTypeEnum.Base.getPackageType(), Version.class);
        Package_CLASSES.put(PackageTypeEnum.Other.getPackageType(), Version.class);
//        Package_CLASSES.put(PackageTypeEnum.Npm.getPackageType(),Version.class);
    }

    static {
        LJ_VERSION_CLASS = new HashMap<>();
        LJ_VERSION_CLASS.put(PackageTypeEnum.C.getPackageType(), LjVersion.class);
        LJ_VERSION_CLASS.put(PackageTypeEnum.Other.getPackageType(), LjVersion.class);

    }

    public static Version getBaseVersion(String packageType, String versionName, boolean isSort) {
        String classPackageType = "";

        if (packageType != null && !packageType.isEmpty()) {
            packageType = MapPackageTypeEnum.getValueByKey(packageType);
            classPackageType = PackageTypeEnum.valueOf(packageType.substring(0,1).toUpperCase()+packageType.substring(1).toLowerCase()).getPackageType();
        } else {
            packageType = "base";
            classPackageType = PackageTypeEnum.valueOf(packageType.substring(0,1).toUpperCase()+packageType.substring(1).toLowerCase()).getPackageType();
        }

        assert classPackageType != null;
        try {
            return Package_CLASSES.get(classPackageType).getConstructor(String.class, String.class, boolean.class).newInstance(versionName, packageType.toLowerCase(), isSort);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public static Item changeToExpression(String packageType, String version_expression) {
        String expressionType;
        packageType = MapPackageTypeEnum.getValueByKey(packageType);
        if (PackageTypeEnum.getValues().contains(packageType)) {
            expressionType = PackageTypeEnum.valueOf(packageType.substring(0,1).toUpperCase()+packageType.substring(1).toLowerCase()).getPackageType();
        } else {
            expressionType = PackageTypeEnum.Other.getPackageType();
            packageType = "other";
        }
        assert EXPRESSION_CLASSES.get(expressionType) != null;
        return EXPRESSION_CLASSES.get(expressionType).change_to_expression(packageType.toLowerCase(), version_expression);
    }

    public static List[] sortVersions(List<String> versions, boolean removeNotFinal, String packageType) {
        if (packageType == null || packageType.isEmpty()) {
            packageType = "snyk";
        }
        PackageTypeEnum classPackageType = null;
        if (packageType != null) {
            packageType = MapPackageTypeEnum.getValueByKey(packageType);
            if (PackageTypeEnum.getValues().contains(packageType)) {
                classPackageType = PackageTypeEnum.valueOf(packageType.substring(0,1).toUpperCase()+packageType.substring(1).toLowerCase());
            } else {
                classPackageType = PackageTypeEnum.Other;
                packageType = "other";
            }
        }
        assert LJ_VERSION_CLASS.containsKey(classPackageType.getPackageType());
        try {
            return LJ_VERSION_CLASS.get(classPackageType.getPackageType()).getConstructor(String.class, String.class).newInstance(null, packageType.toLowerCase()).sortVersions2(versions, removeNotFinal);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Integer versionCompare(String packageType, String version1, String version2) {
        PackageTypeEnum classPackageType = null;
        packageType = MapPackageTypeEnum.getValueByKey(packageType);
        if (PackageTypeEnum.getValues().contains(packageType.toLowerCase())) {
            classPackageType = PackageTypeEnum.valueOf(packageType.substring(0,1).toUpperCase()+packageType.substring(1).toLowerCase());
        } else {
            classPackageType = PackageTypeEnum.Other;
        }
        try {
            return LJ_VERSION_CLASS.get(classPackageType.getPackageType()).getConstructor(String.class, String.class).newInstance(null, packageType.toLowerCase()).versionCompare(version1, version2);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
