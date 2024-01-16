package VersionBase.BaseVersion;

import lombok.Data;
import misc.VersionSortEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static VersionBase.CommonMethod.isMatch;
import static expression.BaseMethod.*;
import static init.init.getBaseVersion;

@Data
public class Version implements Comparable {

    private boolean isSort;
    private String oldVersion;
    private String packageType;
    private String newVersion;
    private List<Object> cmpVersions;

    public Version(String version, String packageType, boolean isSort) {
        this.isSort = isSort;
        this.oldVersion = version;
        this.packageType = packageType;
        this.newVersion = formatVersion();
        this.cmpVersions = new ArrayList<>();
    }

    public String delZero(String data) {
        if (data != null && !data.isEmpty()) {
            Pattern pattern = Pattern.compile("\\.0+([1-9]+)");
            Matcher matcher = pattern.matcher(data);

            while (matcher.find()) {
                data = matcher.replaceFirst(delZeroFunc(matcher));
                matcher = pattern.matcher(data);
            }

            if (packageType.equals("snyk") && data.contains("_")) {
                pattern = Pattern.compile("\\_0+([1-9]+)");
                matcher = pattern.matcher(data);

                while (matcher.find()) {
                    data = matcher.replaceFirst(delZeroFunc(matcher));
                    matcher = pattern.matcher(data);
                }
            }
        }

        return data;
    }

    private String delZeroFunc(Matcher matcher) {
        String iden = ".";
        String newData = matcher.group().replace(".", "");

        if (packageType.equals("snyk") && newData.contains("_")) {
            newData = newData.replace("_", "");
            iden = "_";
        }

        return iden + Integer.parseInt(newData);
    }

    public String formatVersion() {
        String version = "";
        if (oldVersion != null && !oldVersion.isEmpty()) {
            version = oldVersion;
            version = version.toLowerCase()
                    .replace(" ", "")
                    .replace("/", "")
                    .replace("_", ".")
                    .replace("\"", "")
                    .replace("、", ".")
                    .replace(":", ".")
                    .replace("_", ".");
            version = delZero(version);
            if (version.charAt(0) == '.') {
                version = "0" + version;
            }
            String identification = getIdentification(version);
            newVersion = baseFormat(version);
            String canonicalVersion = String.valueOf(arrangement_version(newVersion, packageType, true, false, true));
            if (!isCanonical(canonicalVersion)) {
                if (!isSort) {
                    newVersion = delStr(newVersion);
                }
            }
            if (!newVersion.contains(identification)) {
                newVersion = identification + newVersion;
            }
            if (newVersion != null && !newVersion.isEmpty() && newVersion.endsWith(".")) {
                newVersion = newVersion.substring(0, newVersion.length() - 1);
            }

            return newVersion;


        }
        return null;
    }

    public String getIdentification(String version) {
        String newVersion = "";
        String pattern = "(>|<|=)*";
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(version);

        if (matcher.find()) {
            newVersion = version.substring(matcher.start(), matcher.end());
        }

        return newVersion;
    }

    public String baseFormat(String version) {
        version = version.replace("+", "");
        String[] match = isMatch(version, "^[a-zA-Z-_\\/]*(\\S+)$");
        Boolean flag = Boolean.parseBoolean(match[0]);
        String reVersion = match[1];
        if (!version.equals(reVersion) && flag && reVersion.length() > 1) {
            version = reVersion;
        }
        if ((reVersion.equals(version) && reVersion.length() == 1) || reVersion.length() > 1) {
            String partVersion = new String(version);
            if (partVersion.replace(" ", "").replace(".", "") != "master") {
                version = formatRelease(version);
                version = formatPost(version);
                version = formatDev(version);
                version = formatDel(version);
            }

        }
        return version;
    }

    public String formatRelease(String version) {
        if (version.contains("alpha")) {
            version = version.replace("alpha", "a");
        }
        if (version.contains("beta")) {
            version = version.replace("beta", "b");
        }
        if (version.contains("rc")) {
            version = version.replace("rc", "rc");
        }
        if (version.contains("preview")) {
            version = version.replace("preview", "rc");

        }
        if (version.contains("pre")) {
            version = version.replace("pre", "rc");
        }
        if (version.contains("c") && !version.contains("rc")) {
            version = version.replace("c", "rc");
        }
        return version;
    }

    public String formatPost(String version) {
        if (version.contains("release")) {
            version = version.replace("release", "post");
        }
        if (version.contains("rev")) {
            version = version.replace("rev", "post");
        }
        if (version.contains("r") && !version.contains("rc")) {
            version = version.replace("r", "post");
        }
        return version;
    }

    public String formatDev(String version) {
        if (version.contains("devel")) {
            version = version.replace("devel", "dev");
        }
        if (version.contains("dev")) {
            version = version.replace("dev", "dev");
        }
        if (version.contains("final")) {
            version = version.replace("final", "dev");
        }
        return version;
    }

    public String formatDel(String version) {
        version = delIndex(version, "a");
        version = delIndex(version, "b");
        version = delIndex(version, "rc");
        version = delIndex(version, "post");
        version = delIndex(version, "dev");
        if (version.charAt(version.length() - 1) == '.') {
            version = version.substring(0, version.length() - 1);
        }
        return version;

    }

    public String delIndex(String version, String verType) {
        Pattern pattern = Pattern.compile("^\\d.*");
        if (version.contains(verType)) {
            int indexVer = version.indexOf(verType);
            Matcher matcher = pattern.matcher(version.substring(indexVer + verType.length()));
            if (matcher.find()) {
                String verNum = matcher.group();
                if (version.charAt(indexVer - 1) == '.') {
                    if ("dev".equals(verType) || "post".equals(verType)) {
                        return version.substring(0, indexVer + verType.length() + verNum.length()) +
                                delIndex(version.substring(indexVer + verType.length() + verNum.length()), verType);

                    } else {
                        return version.substring(0, indexVer - 1) +
                                version.substring(indexVer, indexVer + verType.length() + verNum.length()) +
                                delIndex(version.substring(indexVer + verType.length() + verNum.length()), verType);
                    }
                }
                return version.substring(0, indexVer + verType.length() + verNum.length()) +
                        delIndex(version.substring(indexVer + verType.length() + verNum.length()), verType);

            } else {
                if (indexVer + verType.length() < version.length()) {
                    Matcher matcher2 = pattern.matcher(version.substring(indexVer + verType.length() + 1));
                    if (("a".equals(verType) || "b".equals(verType) || "rc".equals(verType)) &&
                            indexVer > 0 && version.charAt(indexVer - 1) == '.') {
                        return version.substring(0, indexVer - 1) + delIndex(version.substring(indexVer), verType);
                    }
                    if (version.charAt(indexVer + verType.length()) == '.') {
                        if (indexVer + verType.length() + 1 == version.length()) {
                            if (verType.equals("dev") || verType.equals("post")) {
                                return version.substring(0, indexVer + verType.length() - 1) + "1";
                            } else {
                                return version.substring(0, indexVer + verType.length()) + "1";
                            }
                        } else {
                            if (verType.equals("dev") || verType.equals("post")) {
                                if (matcher2.find()) {
                                    String verNum = matcher2.group();
                                    if (indexVer + verType.length() + verNum.length() + 1 == version.length()) {
                                        return version.substring(0, indexVer + verType.length()) + verNum;
                                    } else {
                                        return version.substring(0, indexVer + verType.length()) + verNum + '.' +
                                                delIndex(version.substring(indexVer + verType.length() + verNum.length()), verType);
                                    }
                                } else {
                                    return version.substring(0, indexVer + verType.length()) + "1." +
                                            delIndex(version.substring(indexVer + verType.length() + 1), verType);
                                }
                            } else {
                                if (matcher2.find()) {
                                    String verNum = matcher2.group();
                                    if (indexVer + verType.length() + verNum.length() + 1 == version.length()) {
                                        return version.substring(0, indexVer + verType.length()) + verNum;
                                    } else {
                                        return version.substring(0, indexVer + verType.length()) + verNum + '.' + delIndex(
                                                version.substring(indexVer + verType.length() + verNum.length() + 1), verType);
                                    }
                                } else {
                                    return version.substring(0, indexVer + verType.length()) + "1" +
                                            delIndex(version.substring(indexVer + verType.length() + 1), verType);
                                }
                            }
                        }
                    } else {
                        return version.substring(0, indexVer + verType.length()) +
                                delIndex(version.substring(indexVer + verType.length()), verType);
                    }


                } else if (indexVer + verType.length() == version.length()) {
                    if (version.charAt(indexVer - 1) == '.' && !verType.equals("dev") && !verType.equals("post")) {
                        return version.substring(0, indexVer - 1) + verType + "1";
                    } else {
                        if (version.charAt(indexVer) != '.' && (verType.equals("post") || verType.equals("dev"))) {
                            if (version.charAt(indexVer - 1) != '.') {
                                return version.substring(0, indexVer) + "." + verType + "1";
                            }
                        }
                        return version.substring(0, indexVer) + verType + "1";
                    }
                } else {
                    if (version.charAt(indexVer - 1) == '.') {
                        return version.substring(0, indexVer - 1) + verType + "1";
                    } else {
                        return version;
                    }
                }
            }
        }
        return version;
    }

    public boolean isCanonical(String version) {
        /**
         * 版本格式化之后的验证
         */
        String versionRegex = "^([1-9][0-9]*!)?(0|[1-9][0-9]*)(\\.(0|[1-9][0-9]*))*((a|b|rc)(0|[1-9][0-9]*))?(\\.post(0|[1-9][0-9]*))?(\\.dev(0|[1-9][0-9]*))?$";

        if (version != null && !version.isEmpty()) {
            Pattern pattern = Pattern.compile(versionRegex);
            Matcher matcher = pattern.matcher(version);
            return matcher.matches();
        }
        return false;
    }

    public String delStr(String version) {
        String patternStr = "((>|<|=)*)\\d(_|\\.|\\d|a|b|(rc)|(\\.post)|(_post)){0,}\\d*";;
        Pattern pattern = Pattern.compile(patternStr);
        Matcher versionMatcher = pattern.matcher(version);
        String[] finalVersion = {};
        while (versionMatcher.find()) {
            String[] listVersion = versionMatcher.group().split("\\.", -1);
            if (listVersion.length > finalVersion.length) {
                finalVersion = listVersion;
            }
        }

        String newVersion = String.join(".", finalVersion);
        return newVersion;
    }

    public String delReplace(String version) {
        String cmpVersions = version
                .replace("<", "")
                .replace(">", "")
                .replace("=", "")
                .replace("[", "")
                .replace("(", "")
                .replace("]", "")
                .replace(")", "");

        return cmpVersions;
    }

    @Override
    public String toString() {
        return this.oldVersion;
    }

    public List<Object> formatCmpVersion() {
        List<Object> finalCmpVersion = new ArrayList<>();
        if (this.newVersion != null || this.oldVersion != null) {
            String[] cmpVersions;
            if (this.newVersion != null) {
                cmpVersions = this.delReplace(this.newVersion).split("\\.");
            } else {
                cmpVersions = this.delReplace(this.oldVersion).split("\\.");
            }
            for (String cmpVersion : cmpVersions) {
                Pattern pattern = Pattern.compile("([a-z]+)");
                Matcher matcher = pattern.matcher(cmpVersion);
                if (matcher.find()) {
                    finalCmpVersion.add(cmpVersion);

                } else {
                    if (!cmpVersion.isEmpty()) {
                        try {
                            finalCmpVersion.add(Integer.parseInt(cmpVersion));
                        } catch (NumberFormatException e) {
                            // Ignore the exception, do nothing
                        }
                    }
                }
            }

        }
        return finalCmpVersion;

    }

    public int compareTo(Object other) {
        if (other instanceof String) {
            other = getBaseVersion(packageType, (String) other, false);
        }
        ((Version) other).cmpVersions = ((Version) other).formatCmpVersion();
        this.cmpVersions = this.formatCmpVersion();
//        System.out.println(((Version) other).cmpVersions+" "+this.cmpVersions);
        int flag = this.compareToVersions((Version) other);

        if (!this.cmpVersions.isEmpty()) {
            return flag;
        } else {
            return 2;
        }
    }

    public int compareToVersions(Version other) {
        int equalNum = 0;
        int flag = 0;
        supplementZero(this.cmpVersions, other.cmpVersions);
        List<Object> versionSourceList = this.cmpVersions;
        List<Object> versionDestList = other.cmpVersions;
        for (int i = 0; i < versionDestList.size(); i++) {
            if (versionDestList.get(i).equals("0")) {
                versionDestList.set(i, 0);
            }
        }
        for (int i = 0; i < versionSourceList.size(); i++) {
            if (versionSourceList.get(i).equals("0")) {
                versionSourceList.set(i, 0);
            }
        }
//        System.out.println(versionSourceList+" "+versionDestList);
        for (int i = 0; i < versionSourceList.size(); i++) {
            if (versionSourceList.get(i).equals("master") || versionDestList.get(i).equals("master")) {
                if (!versionDestList.get(i).equals("master")) {
                    flag = 1;
                    break;
                } else if (!versionSourceList.get(i).equals("master")) {
                    flag = -1;
                    break;
                }
            }

            if (versionSourceList.get(i) instanceof Integer && versionDestList.get(i) instanceof Integer) {
                int source = (Integer) versionSourceList.get(i);
                int dest = (Integer) versionDestList.get(i);

                if (source > dest) {
                    flag = 1;
                    break;
                } else if (source < dest) {
                    flag = -1;
                    break;
                } else {
                    equalNum++;
                }
            } else {
                if (!this.oldVersion.equals(versionSourceList.get(i)) || !other.oldVersion.equals(versionDestList.get(i))) {
                    flag = compareStrChangeList(String.valueOf(versionSourceList.get(i)), String.valueOf(versionDestList.get(i)));
                    if (flag != 0) {
                        break;
                    } else {
                        equalNum++;
                    }
                } else {
                    flag = -1;
                    break;
                }
            }
        }

        return flag;

    }

    public static String maxVersionCalculation(String version) {
        String[] versionList = version.split("\\.");
        String maxVersion = "";
        if (versionList.length >= 2) {
            Matcher matcher = Pattern.compile("([a-z]+)").matcher(version);
            if (matcher.find()) {
                for (int ver = 0; ver < versionList.length; ver++) {
                    matcher = Pattern.compile("([a-z]+)").matcher(versionList[ver]);
                    if (matcher.find()) {
                        maxVersion = maxVersionCalculation(maxVersion);
                        break;
                    } else {
                        maxVersion += versionList[ver] + ".";
                    }
                }
            } else {
                for (int ver = 0; ver < versionList.length; ver++) {
                    if (ver < versionList.length - 2) {
                        maxVersion += versionList[ver] + ".";
                    } else if (ver == versionList.length - 2) {
                        int maxVersionCount = Integer.parseInt(versionList[ver]) + 1;
                        maxVersion += String.valueOf(maxVersionCount) + ".";
                    } else {
                        if (ver != versionList.length - 1) {
                            maxVersion += String.valueOf(0) + ".";
                        } else {
                            maxVersion += String.valueOf(0);
                        }
                    }
                }


            }


        } else {
            Matcher matcher = Pattern.compile("([a-z]+)").matcher(versionList[0]);
            if (matcher.find()) {
                maxVersion = String.valueOf(Integer.parseInt(versionList[0]) + 1);
            } else {
                String maxVersionNum = maxVersionStr(versionList[0]);
                maxVersion += maxVersionNum + maxVersionNum;
            }
        }
        if (maxVersion.charAt(maxVersion.length() - 1) == '.') {
            maxVersion = maxVersion.substring(0, maxVersion.length() - 1);
        }
        return maxVersion;


    }

    public static String maxVersionStr(String version) {
        Matcher matcher = Pattern.compile("([a-z]+)").matcher(version);
        List<String> versionRe = new ArrayList<>();
        while (matcher.find()) {
            versionRe.add(matcher.group());
        }
        String[] splitVersion = version.split(versionRe.get(0));
        List<String> versionDestStrList = new ArrayList<>();
        for (String i : splitVersion) {
            if (!i.isEmpty()) {
                versionDestStrList.add(String.valueOf(Integer.parseInt(i)));
            } else {
                versionDestStrList.add("0");
            }
        }
        VersionSortEnum verEnum = VersionSortEnum.OTHER;
        try {
            verEnum = VersionSortEnum.valueOf(versionRe.get(0).toUpperCase()); // Assuming VersionSort is an enum
        } catch (Exception e) {
            System.out.println("此处未找到对应的英文版本");
        } finally {
            int maxVersionNum;
            int verArg = verEnum.getValue();
            if (verArg != 6) {
                maxVersionNum = Integer.parseInt(versionDestStrList.get(0)) + 1;
            } else {
                maxVersionNum = Integer.parseInt(versionDestStrList.get(0)) + 2;
            }
            return String.valueOf(maxVersionNum);
        }

    }


}
