package VersionBase;

import VersionBase.BaseVersion.Version;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static init.init.getBaseVersion;

@Data
public class LjVersion {
    private String versionStr;
    private String formatVersion;
    private String packageType;
    private Object formatVersionObj;  // Assuming the type of this object based on your implementation
    private List<String> formatVersionList;

    public LjVersion(String versionStr, String packageType) {
        this.versionStr = versionStr;
        this.packageType = packageType;
        this.formatVersion = "";  // Initialize as empty
        this.formatVersionObj = formatCompareVersion();
        this.formatVersionList = new ArrayList<>();
    }

    public Object formatCompareVersion() {
        Version baseVersion = getBaseVersion(this.packageType, this.versionStr, false);

        if (baseVersion.getNewVersion() != null) {
            this.formatVersion = baseVersion.getNewVersion();
        } else {
            this.formatVersion = baseVersion.getOldVersion();
        }

        return baseVersion;
    }

    public List[] sortVersions2(List<String> versions, boolean removeNotFinal) {
        List<Version> notVersionObjs = new ArrayList<>();
        List<Version> finalVersionObjs = new ArrayList<>();
        List<Version> failVersions = new ArrayList<>();

        for (String version : versions) {
            Version baseVersion = getBaseVersion(this.packageType, version, false);
            if (baseVersion.getNewVersion() != null) {
                if (removeNotFinal) {
                    if (!baseVersion.getNewVersion().contains("a")
                            && !baseVersion.getNewVersion().contains("b")
                            && !baseVersion.getNewVersion().contains("rc")
                            && !baseVersion.getNewVersion().contains("pre")) {
                        notVersionObjs.add(baseVersion);
                    } else {
                        failVersions.add(baseVersion);
                    }
                } else {
                    finalVersionObjs.add(baseVersion);
                }
            } else {
                failVersions.add(baseVersion);
            }
        }

        List<Version> versionObjs = removeNotFinal ? notVersionObjs : finalVersionObjs;
        Collections.sort(versionObjs);
        List<String> lastSortVersions = new ArrayList<>();
        for (Version version : versionObjs) {
            lastSortVersions.add(version.toString());
        }

        List<String> sortedFailVersions = new ArrayList<>();
        for (Version version : failVersions) {
            sortedFailVersions.add(version.toString());
        }
        Collections.sort(failVersions);
        return new List[]{lastSortVersions, sortedFailVersions};
    }

    public int versionCompare(String version1, String version2) {
        Version version1Obj = getBaseVersion(packageType, version1, false);
        Version version2Obj = getBaseVersion(packageType, version2, false);
        return version1Obj.compareTo(version2Obj);
    }

}
