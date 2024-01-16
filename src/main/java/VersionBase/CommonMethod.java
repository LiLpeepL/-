package VersionBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonMethod {


    public static String[] isMatch(String versionStr, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(versionStr);
        if (matcher.find()) {
            String reVersion = matcher.group(1);
            return new String[]{Boolean.toString(true), reVersion};
        }
        return new String[]{Boolean.toString(false), ""};
    }
}
