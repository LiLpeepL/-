package expression;

import VersionBase.BaseItem.Item;
import VersionBase.BaseVersion.Version;
import misc.PackageTypeEnum;
import misc.VersionSortEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static VersionBase.BaseVersion.Version.maxVersionCalculation;
import static init.init.getBaseVersion;

public class BaseMethod {
    public static List<String> sortValues = new ArrayList<>();

    static {
        for (VersionSortEnum versionSort : VersionSortEnum.values()) {
            sortValues.add(versionSort.getLabel()); // or versionSort.getValue() based on your need
        }
    }

    public static String chinese_to_english_version(String version) {
        version = version.replace("＝", "=");
        version = version.replace("～", "~");
        version = version.replace("ˆ", "^");
        version = version.replace("⁰", "0");
        version = version.replace("¹", "1");
        version = version.replace("²", "2");
        version = version.replace("³", "3");
        version = version.replace("⁴", "4");
        version = version.replace("⁵", "5");
        version = version.replace("⁶", "6");
        version = version.replace("⁷", "7");
        version = version.replace("⁸", "8");
        version = version.replace("⁹", "9");
        version = version.replace("（", "(");
        version = version.replace("）", ")");
        version = version.replace("【", "[");
        version = version.replace("】", "]");
        version = version.replace("｜", "|");
        return version;
    }

    public static Item handle_version_or(String versionDest, String packageType) {
        Boolean[] leftOrRightIdentifier = find_left_or_right_identifier(versionDest);
        Boolean leftOpen = leftOrRightIdentifier[0];
        Boolean rightOpen = leftOrRightIdentifier[1];
        Object newVersionDest = arrangement_version(versionDest, packageType, true, true, false);
        if (newVersionDest instanceof Version) {
            newVersionDest = ((Version) newVersionDest).getNewVersion();
        }
        Item versionItem = null;
        if (leftOpen != null) {
            versionItem = new Item(String.valueOf(newVersionDest), "", leftOpen, true, null);
        } else {
            versionItem = new Item("", String.valueOf(newVersionDest), true, rightOpen, null);
        }
        return versionItem;


    }

    public static Boolean[] find_left_or_right_identifier(String version) {
        Boolean left_open = null;
        Boolean right_open = null;
        if (version.contains(">")) {
            if (version.contains("=")) {
                left_open = true;
            } else {
                left_open = false;
            }
        }
        if (version.contains("<")) {
            if (version.contains("=")) {
                right_open = true;
            } else {
                right_open = false;
            }
        }
        if (version.contains("[")) {
            left_open = true;
        } else if (version.contains("(")) {
            left_open = false;
        }
        if (version.contains("]")) {
            right_open = true;
        } else if (version.contains(")")) {
            right_open = false;
        }
        return new Boolean[]{left_open, right_open};

    }

    public static Object arrangement_version(String version,
                                             String packageType,
                                             boolean isDelIdentifier,
                                             boolean isFormat,
                                             boolean isDelBrackets) {
        if (isDelIdentifier) {
            version = version.replace(" ", "")
                    .replace("=", "")
                    .replace(">", "")
                    .replace("<", "")
                    .replace("!", "");
        }
        if (isDelBrackets) {
            version = version.replace("[", "")
                    .replace("(", "")
                    .replace("]", "")
                    .replace(")", "");
        }

//        version = version.replaceAll("\\.{2,}", ".");
//        version = version.replaceAll("_{2,}", "_");
        if (isFormat) {
            Version versionObject = getBaseVersion(packageType, version, false);
            return versionObject;
        }
        return version;
    }

    public static void supplementZero(List<Object> versionSourceList, List<Object> versionDestList) {
        if (versionSourceList.size() < versionDestList.size()) {
            for (int i = versionSourceList.size(); i < versionDestList.size(); i++) {
                versionSourceList.add("0");
            }
        } else if (versionSourceList.size() > versionDestList.size()) {
            for (int i = versionDestList.size(); i < versionSourceList.size(); i++) {
                versionDestList.add("0");
            }
        }
    }

    public static List<Object> versionStrChangeList(String versionDest) {
        Pattern pattern = Pattern.compile("([a-z]+)");
        Matcher matcher = pattern.matcher(versionDest);

        if (versionDest instanceof String) {
            if (matcher.find()) {
                String found = matcher.group();
                String[] versionDestList = versionDest.split(found);

                List<Object> result = new ArrayList<>();
                if (!versionDestList[0].isEmpty() && !versionDestList[versionDestList.length - 1].isEmpty()) {
                    result.add(versionDestList[0]);
                    result.add(found);
                    result.addAll(versionStrChangeList(versionDest.substring(versionDestList[0].length() + found.length())));
                } else if (!versionDestList[0].isEmpty() && versionDestList[versionDestList.length - 1].isEmpty()) {
                    result.add(versionDestList[0]);
                    result.add(found);
                } else {
                    result.add(found);
                    result.addAll(versionStrChangeList(versionDest.substring(found.length() + versionDestList[0].length())));
                }
                return result;
            } else {
                List<Object> versionDestStrList = new ArrayList<>();
                versionDestStrList.add(versionDest);
                return versionDestStrList;
            }
        } else {
            List<Object> nonStringList = new ArrayList<>();
            nonStringList.add(versionDest);
            return nonStringList;
        }
    }

    public static int compareStrChangeList(String versionSource, String versionDest) {
        List<Object> versionSourceStrList = versionStrChangeList(versionSource);
        List<Object> versionDestStrList = versionStrChangeList(versionDest);
        int flag = compareStr(versionSourceStrList, versionDestStrList);
        return flag;
    }

    public static int compareStr(List<Object> versionSourceStrList, List<Object> versionDestStrList) {
        int flag = 0;
        int minLen = Math.min(versionSourceStrList.size(), versionDestStrList.size());
        if (versionSourceStrList.size() == minLen) {
            for (int i = 0; i < versionDestStrList.size() - minLen; i++) {
                versionSourceStrList.add(0);
            }
        }
        if (versionDestStrList.size() == minLen) {
            for (int i = 0; i < versionSourceStrList.size() - minLen; i++) {
                versionDestStrList.add(0);
            }
        }

        for (int i = 0; i < versionDestStrList.size(); i++) {
            try {
                int intSource = Integer.parseInt(String.valueOf(versionSourceStrList.get(i)));
                int intDest = Integer.parseInt(String.valueOf(versionDestStrList.get(i)));
                if (intSource > intDest) {
                    flag = 1;
                    break;
                } else if (intSource < intDest) {
                    flag = -1;
                    break;
                }
            } catch (NumberFormatException e) {

                flag = compareStrByDictVal(String.valueOf(versionSourceStrList.get(i)), String.valueOf(versionDestStrList.get(i)));
            } finally {
                if (flag == 1 || flag == -1) {
                    break;
                }
            }
        }

        return flag;
    }


    private static int compareStrByDictVal(String sourceStr, String destStr) {
        int flag = 0;
        VersionSortEnum destValTup = VersionSortEnum.OTHER;
        VersionSortEnum sourceValTup = VersionSortEnum.OTHER;
        try {
            if (sortValues.contains(sourceStr) && sortValues.contains(destStr)) {
                int source_val = VersionSortEnum.valueOf(sourceStr.toUpperCase()).getValue();
                int dest_val = VersionSortEnum.valueOf(destStr.toUpperCase()).getValue();
                if (source_val > dest_val) {
                    flag = 1;
                } else if (source_val < dest_val) {
                    flag = -1;
                }
            } else if (!sortValues.contains(destStr) && sortValues.contains(sourceStr)) {
                int destVal = destValTup.getValue();
                int sourceVal = VersionSortEnum.valueOf(sourceStr.toUpperCase()).getValue();
                if (sourceVal > destVal) {
                    flag = 1;
                } else if (sourceVal < destVal) {
                    flag = -1;
                }

            } else if (!sortValues.contains(destStr) && !sortValues.contains(sourceStr)) {
                Object compareSource;
                Object compareDest;
                try {
                    Integer sourceInt = Integer.parseInt(sourceStr);
                    compareSource = sourceInt;
                } catch (NumberFormatException e) {
                    compareSource = sourceValTup;
                }
                try {
                    Integer destInt = Integer.parseInt(destStr);
                    compareDest = destInt;
                } catch (NumberFormatException e) {
                    compareDest = destValTup;
                }
                if (compareDest.equals(compareSource) && compareDest.equals(VersionSortEnum.OTHER)) {
                    compareSource = sourceStr;
                    compareDest = destStr;
                }
                if (compareSource instanceof Integer && compareDest instanceof Integer) {
                    if (compareSource == compareDest) {
                        flag = 0;
                    } else if ((Integer) compareSource > (Integer) compareDest) {
                        flag = 1;
                    } else {
                        flag = -1;
                    }
                } else if ((compareSource instanceof String && compareDest instanceof String)) {
                    int i = String.valueOf(compareSource).compareTo(String.valueOf(compareDest));
                    if (i == 0) {
                        flag = 0;
                    } else if (i == 1) {
                        flag = 1;
                    } else if (i == -1) {
                        flag = -1;
                    }
                }
                else if((compareSource instanceof VersionSortEnum&& !(compareDest instanceof VersionSortEnum))||(!(compareSource instanceof VersionSortEnum &&compareDest instanceof VersionSortEnum))){
                    throw new ClassCastException();
                }


        } else{
            int sourceVal = sourceValTup.getValue();
            int destVal = VersionSortEnum.valueOf(destStr).getValue();
            if (sourceVal > destVal) {
                flag = 1;
            } else if (sourceVal < destVal) {
                flag = -1;
            }
        }
    } catch(Exception e)
    {
        flag = -1;
    }
        return flag;

}

    public static Item handle_version_compatible(String versionDest, String packageType) {
        if (packageType.equals(PackageTypeEnum.valueOf("Npm").getPackageType())) {
            if (versionDest.contains("~")) {
                versionDest = versionDest.split("~")[1];
            } else {
                versionDest = versionDest.replace("x", "0");
            }
        } else if (packageType.equals(PackageTypeEnum.valueOf("C").getPackageType())) {
            versionDest = versionDest.replace(" ", "");
            if (versionDest.contains("~=")) {
                versionDest = versionDest.split("~=")[1];
            }
        } else if (packageType.equals(PackageTypeEnum.valueOf("Cargo").getPackageType())) {
            versionDest = versionDest.replace(" ", "");
            if (versionDest.contains("~")) {
                versionDest = versionDest.split("~")[1];
            }
            if (versionDest.contains("*")) {
                versionDest = versionDest.replace("*", "0");
            }
            if (versionDest.contains("^")) {
                versionDest = versionDest.split("^")[1];
            }
        } else if (packageType.equals(PackageTypeEnum.valueOf("Other").getPackageType())) {
            versionDest = versionDest.replace(" ", "");
            if (versionDest.contains("~") && !versionDest.contains("~>")) {
                versionDest = versionDest.split("~")[1];
            }
            if (versionDest.contains("~>")) {
                versionDest = versionDest.split("~>")[1];
            }
            if (versionDest.contains("*")) {
                versionDest = versionDest.replace("*", "0");
            }
            if (versionDest.contains("^")) {
                versionDest = versionDest.split("^")[1];
            }
        } else if (packageType.equals(PackageTypeEnum.valueOf("Nuget").getPackageType())) {
            versionDest = versionDest.replace("*", "0");
        } else if (packageType.equals(PackageTypeEnum.valueOf("Snyk").getPackageType())) {
            if (versionDest.contains("~")) {
                versionDest = versionDest.split("~")[1];
            }
        } else if (packageType.equals(PackageTypeEnum.valueOf("Cocoapods").getPackageType())) {
            versionDest = versionDest.replace(" ", "");
            if (versionDest.contains("~>")) {
                versionDest = versionDest.split("~>")[1];
            }
        } else if (packageType.equals(PackageTypeEnum.valueOf("Composer").getPackageType())) {
            if (versionDest.contains("^")) {
                versionDest = versionDest.split("\\^")[1];
            } else if (versionDest.contains("~")) {
                versionDest = versionDest.split("~")[1];
            }
            versionDest = versionDest.replace("x", "0");
        } else if (packageType.equals(PackageTypeEnum.valueOf("Hex").getPackageType())) {
            versionDest = versionDest.replace(" ", "");
            if (versionDest.contains("~>")) {
                versionDest = versionDest.split("~>")[1];
            }
        } else if (packageType.equals(PackageTypeEnum.valueOf("Pip").getPackageType())) {
            versionDest = versionDest.replace(" ", "");
            versionDest = versionDest.split("~=")[1];
        } else if (packageType.equals(PackageTypeEnum.valueOf("Ruby").getPackageType())) {
            versionDest = versionDest.replace(" ", "");
            if (versionDest.contains("~>")) {
                versionDest = versionDest.split("~>")[1];
            }
        }

        Object versionDest2 = arrangement_version(versionDest, packageType, true, true, false);
        String maxVersion = "";
        Item versionItem = null;
        if (versionDest2 instanceof Version) {
            maxVersion = maxVersionCalculation(((Version) versionDest2).getNewVersion());
            versionItem = new Item(((Version) versionDest2).getNewVersion(), maxVersion, true, false, null);

        } else {
            maxVersion = maxVersionCalculation(String.valueOf(versionDest2) );
            versionItem = new Item(String.valueOf(versionDest2), maxVersion, true, false, null);
        }
        return versionItem;
    }


}
