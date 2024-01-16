package VersionBase.BaseItem;

import lombok.Data;

import static init.init.getBaseVersion;

@Data
public class Line {
    private String start;
    private String end;

    public Line(String start, String end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        String startStr = (start != null && !start.isEmpty()) ? start.toString() : "0";
        String endStr = (end != null && !end.isEmpty()) ? end.toString() : "9999999";
        return startStr + "," + endStr;
    }

    public boolean inLine(String spot, boolean isEnd) {
        boolean isInLine = false;
        String startVal = (start != null && !start.isEmpty()) ? start : "0";
        String endVal = (end != null && !end.isEmpty()) ? end : "99999999";

        if (spot == null || spot.isEmpty()) {
            spot = isEnd ? "99999999" : "0";
        }
        int leftFlag = getBaseVersion("snyk", startVal, false).compareTo(spot);
        int rightFlag = getBaseVersion("snyk", endVal, false).compareTo(spot);
        if (leftFlag <= 0 && rightFlag >= 0) {
            isInLine = true;
        }
        return isInLine;
    }


}
