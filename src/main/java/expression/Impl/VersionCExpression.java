package expression.Impl;

import VersionBase.BaseItem.Item;
import VersionBase.BaseItem.ItemGroup;
import expression.BaseMethod;
import expression.VersionExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static expression.BaseMethod.handle_version_compatible;
import static expression.BaseMethod.handle_version_or;

public class VersionCExpression implements VersionExpression {
    public Item change_to_expression(String packageType, String versionExpression) {
        versionExpression = versionExpression.replace("[", "");
        versionExpression = versionExpression.replace("]", "");
        versionExpression = BaseMethod.chinese_to_english_version(versionExpression);
        versionExpression = Pattern.compile("[,<>=&|\\^~]+ *").matcher(versionExpression).replaceAll(matchResult -> VersionExpression.func(matchResult));
        Object versionItem;
        if (versionExpression.contains(" ")) {
            String[] versionDestList = versionExpression.split(" ");
            List<Object> items = new ArrayList<>();
            for (String version : versionDestList) {
                versionItem = handle_conan_compare(version, packageType);
                items.add(versionItem);
            }
            versionItem = new ItemGroup(false, items);

        } else {
            versionItem = handle_conan_compare(versionExpression, packageType);
        }
        if (versionItem instanceof ItemGroup) {
            versionItem = ((ItemGroup) versionItem).merge();
        }
        return (Item) versionItem;


    }

    public Item handle_conan_compare(String versionExpression, String packageType) {
        Item versionItem;
        if (versionExpression.contains(">") || versionExpression.contains("<")) {
            return handle_version_or(versionExpression, packageType);
        } else if (versionExpression.contains("~=")) {
            return handle_version_compatible(versionExpression, packageType);
        } else {
            if (!versionExpression.isEmpty()) {
                if ("".equals(versionExpression.replace("*", ""))) {
                    versionItem = new Item("", "", true, true, null);
                } else {
                    versionItem = new Item(versionExpression, versionExpression, true, true, null);
                }
            } else {
                versionItem = new Item("", "", true, true, null);
            }
        }
        return versionItem;
    }


}
