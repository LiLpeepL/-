package expression;

import VersionBase.BaseItem.Item;

import java.util.regex.MatchResult;

public interface VersionExpression {
    public Item change_to_expression(String packageType, String versionExpression);

    public static String func(MatchResult data){
        return data.group().trim();
    }

}
