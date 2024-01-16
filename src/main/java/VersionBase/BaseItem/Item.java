package VersionBase.BaseItem;

import VersionBase.BaseVersion.Version;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

import static init.init.getBaseVersion;
import static init.init.sortVersions;

@Data
public class Item implements Comparable<Item> {
    private String left;
    private String right;
    private Boolean leftOpen;
    private Boolean rightOpen;
    private Boolean valid;

    public Item(String left, String right, Boolean leftOpen, Boolean rightOpen, Boolean valid) {
        this.left = left;
        this.right = right;
        this.leftOpen = leftOpen;
        this.rightOpen = rightOpen;
        if (valid == null) {
            valid = true;
        }
        this.valid = setValid(valid);

    }

    private class Pair<K, V> {
        private K first;
        private V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }

        public K getFirst() {
            return first;
        }

        public V getSecond() {
            return second;
        }
    }

    private Boolean setValid(Boolean valid) {
        valid = valid;

        if (!right.isEmpty() && !left.isEmpty()) {
            if (!left.equals(right)) {
                int isIntersect = getBaseVersion("", left, false).compareTo(right);
                if (isIntersect > 0) {
                    valid = false;
                }
            }
        }
        return valid;
    }

    public List[] sortMatchLine(Line firstLine, Line otherLine) {
        if (firstLine.getStart() == null || firstLine.getStart().isEmpty()) {
            firstLine.setStart("0");
        }
        if (otherLine.getStart() == null || otherLine.getStart().isEmpty()) {
            otherLine.setStart("0");
        }
        if (firstLine.getEnd() == null || firstLine.getEnd().isEmpty()) {
            firstLine.setEnd("99999999");
        }
        if (otherLine.getEnd() == null || otherLine.getEnd().isEmpty()) {
            otherLine.setEnd("99999999");
        }

        List<String> versionList = Arrays.asList(
                firstLine.getStart(),
                firstLine.getEnd(),
                otherLine.getStart(),
                otherLine.getEnd()
        );
        return sortVersions(versionList, false, "");
    }

    public boolean is_open(Item other, String versionStr, boolean isLeft, boolean isOr, boolean isNull) {
        if (isLeft) {
            if (isNull) {
                if (this.left.equals(versionStr)) {
                    return this.leftOpen;
                } else {
                    return other.leftOpen;
                }
            } else {
                if (this.left.equals(versionStr) || other.left.equals(versionStr)) {
                    if (isOr) {
                        return this.leftOpen || other.leftOpen;
                    } else {
                        return this.leftOpen && other.leftOpen;
                    }
                }
            }
        } else {
            if (isNull) {
                if (this.right.equals(versionStr)) {
                    return this.rightOpen;
                } else {
                    return other.rightOpen;
                }
            } else {
                if (this.right.equals(versionStr) || other.right.equals(versionStr)) {
                    if (isOr) {
                        return this.rightOpen || other.rightOpen;
                    } else {
                        return this.rightOpen && other.rightOpen;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getValue();
    }

    public String getValue() {
        if (!valid) {
            return "NONE";
        }
        if (left == null) {
            left = "";
        }
        if (right == null) {
            right = "";
        }

        if (left.equals("") && right.equals("")) {
            return "*";
        }
        if (left.equals(right)) {
            if (rightOpen && leftOpen) {
                return left;
            }
            if (!rightOpen && !leftOpen) {
                return "-";
            }
        }
        String leftIdentifier = leftOpen ? "[" : "(";
        String rightIdentifier = rightOpen ? "]" : ")";
        return leftIdentifier + left + "," + right + rightIdentifier;
    }

    public Item merge() {
        return this;
    }


    public Object mergeOr(Object other) {
        if (other instanceof ItemGroup) {
            return ((ItemGroup) other).mergeOr(this);
        }
        Object newItem = isIntersect((Item) other, true);
        return newItem;
    }

    public Object mergeAnd(Object other) {
        if (other instanceof ItemGroup) {
            return ((ItemGroup) other).mergeAnd(this);
        }
        Object newItem = isIntersect((Item) other, false);
        return newItem;
    }


    public Object isIntersect(Item other, boolean isOr) {
        Line firstLine = new Line(this.left, this.right);
        Line otherLine = new Line(other.left, other.right);
        if (firstLine.inLine(otherLine.getStart(), false)
                || firstLine.inLine(otherLine.getEnd(), true)
                || otherLine.inLine(firstLine.getStart(), false)
                || otherLine.inLine(firstLine.getEnd(), true)
        ) {
            List[] lists = sortMatchLine(firstLine, otherLine);
            List<String> sortList = lists[0];
            for (int i = 0; i < sortList.size(); i++) {
                if ("0".equals(sortList.get(i))) {
                    sortList.set(i, "");
                }
            }
            for (int i = 0; i < sortList.size(); i++) {
                if ("99999999".equals(sortList.get(i))) {
                    sortList.set(i, "");
                }
            }
            if (sortList.size() == 4) {
                if (!isOr) {
                    boolean isLeftNull = sortList.get(0).isEmpty();
                    boolean isRightNull = sortList.get(sortList.size() - 1).isEmpty();
                    boolean leftOpen = is_open(other, sortList.get(1), true, false, isLeftNull);
                    boolean rightOpen = is_open(other, sortList.get(2), false, false, isRightNull);
                    return new Item(sortList.get(1), sortList.get(2), leftOpen, rightOpen, null);

                } else {
                    boolean isLeftNull = false;
                    boolean isRightNull = false;

                    if (sortList.get(0).isEmpty()) {
                        isLeftNull = true;
                    }
                    if (sortList.get(sortList.size() - 1).isEmpty()) {
                        isRightNull = false;
                    }
                    boolean leftOpen = is_open(other, sortList.get(0), true, true, isLeftNull);
                    boolean rightOpen = is_open(other, sortList.get(sortList.size() - 1), false, true, isRightNull);
                    Pair<Integer, Boolean> compareResult = toCompare(sortList.get(1), sortList.get(2));

                    if (compareResult.getSecond() && compareResult.getFirst() == 0) {
                        if (sortList.get(2).equals(this.right)) {
                            if (this.rightOpen || other.leftOpen) {
                                return new Item(sortList.get(0), sortList.get(sortList.size() - 1), this.leftOpen, other.rightOpen, null);
                            } else {
                                return new ItemGroup(true, Arrays.asList(other));
                            }
                        } else if (sortList.get(2).equals(this.left)) {
                            if (this.leftOpen || other.leftOpen) {
                                return new Item(sortList.get(0), sortList.get(sortList.size() - 1), leftOpen, rightOpen, null);
                            } else {
                                return new ItemGroup(true, Arrays.asList(other));
                            }
                        } else {
                            return new Item(sortList.get(0), sortList.get(sortList.size() - 1), leftOpen, rightOpen, null);
                        }
                    } else if (compareResult.getSecond() && compareResult.getFirst() < 1) {
                        if (this.right.equals(sortList.get(1))) {
                            return new ItemGroup(true, Arrays.asList(other));
                        } else {
                            return new Item(sortList.get(0), sortList.get(sortList.size() - 1), leftOpen, rightOpen, null);
                        }
                    } else if (compareResult.getSecond() && compareResult.getFirst() == 1) {
                        return new Item(sortList.get(0), sortList.get(sortList.size() - 1), leftOpen, rightOpen, null);
                    } else {
                        throw new IllegalArgumentException(sortList.get(1) + ", " + sortList.get(2) + " 不可比较");
                    }


                }

            } else {
                return new Item("", "", null, null, false);
            }
        } else {
            if (isOr) {
                return new ItemGroup(true, Arrays.asList(this, other));
            } else {
                return new Item("", "", null, null, false);
            }

        }
    }


    public Pair<Integer, Boolean> toCompare(String oneVersion, String twoVersion) {
        int flag = 1;
        boolean isCompare = true;

        try {
            Version oneVersionObj = getBaseVersion("snyk", oneVersion, false);
            Version twoVersionObj = getBaseVersion("snyk", twoVersion, false);
            flag = oneVersionObj.compareTo(twoVersionObj);
        } catch (Exception e) {
            isCompare = false;
        }

        return new Pair<>(flag, isCompare);
    }


    @Override
    public int compareTo(Item item) {
        int flag = 1;
        Line firstLine = new Line(this.left, this.right);
        Line otherLine = new Line(item.left, item.right);

        if (firstLine.inLine(otherLine.getStart(), false)
                || firstLine.inLine(otherLine.getEnd(), true)
                || otherLine.inLine(firstLine.getStart(), false)
                || otherLine.inLine(firstLine.getEnd(), true)) {

            String sLeft = this.left != null ? this.left : "0";
            String oLeft = item.left != null ? item.left : "0";
            Pair<Integer, Boolean> leftResult = toCompare(sLeft, oLeft);

            String sRight = this.right != null ? this.right : "99999999999";
            String oRight = item.right != null ? item.right : "99999999999";
            Pair<Integer, Boolean> rightResult = toCompare(sRight, oRight);

            if (leftResult.getSecond() && rightResult.getSecond()) {
                if (leftResult.getFirst() == 0 && rightResult.getFirst() == 0) {
                    flag = 0;
                } else if ((leftResult.getFirst() == 0 && rightResult.getFirst() == -1) || leftResult.getFirst() == -1) {
                    flag = -1;
                }
            }
        } else {
            String sRight = this.right != null ? this.right : "99999999999";
            String oLeft = item.left != null ? item.left : "0";
            Pair<Integer, Boolean> leftResult = toCompare(sRight, oLeft);

            if (leftResult.getSecond()) {
                if (leftResult.getFirst() == 0 || leftResult.getFirst() == -1) {
                    flag = -1;
                } else if (leftResult.getFirst() == 1) {
                    flag = 1;
                }
            }
        }

        return flag;
    }
}
