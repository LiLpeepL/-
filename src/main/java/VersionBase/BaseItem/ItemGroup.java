package VersionBase.BaseItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static init.init.getBaseVersion;
import static init.init.versionCompare;

public class ItemGroup {
    private List<Object> items;
    private boolean isOr;
    private Set<Object> allEqual;
    private List<Object> allOther;

    public ItemGroup(boolean isOr, List<Object> items) {
        this.items = items;
        this.isOr = isOr;
    }

    public Set<Object> getAllEqual() {
        if (allEqual == null || allEqual.isEmpty()) {
            calculateAllOr();
        }
        return allEqual;
    }

    public List<Object> getAllOther() {
        if (allOther == null || allOther.isEmpty()) {
            calculateAllOr();
        }
        return allOther;
    }

    private void calculateAllOr() {
        allEqual = new HashSet<>();
        allOther = new ArrayList<>();
        for (Object item : items) {
            if (item instanceof Item) {
                Item castItem = (Item) item;
                if (isOr && castItem.getLeft().equals(castItem.getRight()) && (castItem.getLeft() != null && !castItem.getLeft().isEmpty())) {
                    allEqual.add(castItem.getLeft());
                } else {
                    allOther.add(castItem);
                }
            } else if (item instanceof ItemGroup) {
                ItemGroup group = (ItemGroup) item;
                allEqual.addAll(group.getAllEqual());
                allOther.addAll(group.getAllOther());
            }
        }
    }

    public Object merge() {
        Object ret = null;
        for (Object item : this.items) {
            if (item instanceof ItemGroup) {
                item = ((ItemGroup) item).merge();
            }
            if (ret == null) {
                try {
                    if (((Item) item).getValid()) {
                        ret = item;
                    } else if (!this.isOr) {
                        return new Item("", "", null, null, false); // Assuming Item constructor takes a boolean for validity
                    }
                } catch (Exception e) {
                    ret = item;
                }
            } else if (this.isOr) {
                if (((Item) item).getValid()) {
                    ret = ((Item) ret).mergeOr(item); // Assuming mergeOr method exists
                }
            } else {
                if (!((Item) item).getValid()) {
                    return new Item("", "", null, null, false);
                }
                ret = ((Item) ret).mergeAnd(item); // Assuming mergeAnd method exists
                if (!((Item) ret).getValid()) {
                    return new Item("", "", null, null, false);
                }
            }
        }
        return ret instanceof ItemGroup ? ((ItemGroup) ret).mergeFinish() : ret; // Assuming mergeFinish method exists
    }

    public Object mergeAnd(Object other) {
        assert this.isOr;
        if (other instanceof ItemGroup) {
            assert ((ItemGroup) other).isOr;
        }

        List<Object> subItems = new ArrayList<>();
        for (Object item : this.items) {
            subItems.add(new ItemGroup(false, Arrays.asList(item)));
        }
        ItemGroup ret = new ItemGroup(true, subItems);
        return ret.merge();
    }

    public Object mergeOr(Object other) {
        boolean isIn = false;
        if (other instanceof Item) {
            Item otherItem = (Item) other;
            for (int i = 0; i < items.size(); i++) {
                Object isItem = otherItem.mergeOr(items.get(i));
                if (isItem instanceof Item && ((Item) isItem).getValid()) {
                    isIn = true;
                    items.remove(i);
                    items.add(isItem);
                    break;
                }
                if (otherItem.getValue().equals(((Item) isItem).getValue())) {
                    isIn = true;
                    break;
                }
            }
            if (!isIn) {
                items.add(otherItem);
            }
        } else {

            for (Object otherI : ((ItemGroup) other).items) {
                Object partI = null;
                if (otherI instanceof Item) {
                    partI = ((Item) otherI).mergeOr(this);
                }
                if (otherI instanceof ItemGroup) {
                    partI = ((ItemGroup) otherI).mergeOr(this);
                }
                if (!(partI instanceof Item)) {
                    items.add(otherI);
                }
            }
        }

        return new ItemGroup(true, items);
    }

    public Object mergeFinish() {
        assert isOr;
        for (Object item : items) {
            assert item instanceof Item;
        }
        List<Item> newItems = new ArrayList<>();
        for (Object item : items) {
            newItems.add((Item) item);
        }
        Collections.sort(newItems);
        List<Object> ret = new ArrayList<>();
        Item last = newItems.get(0);
        for (int i = 1; i < newItems.size(); i++) {
            String right = last.getRight();
            String left = newItems.get(i).getLeft();

            if (right == null || right.isEmpty()) {
                right = "99999999";
            }
            if (left == null || right.isEmpty()) {
                left = "0";
            }

            int flag = versionCompare("snyk", right, left);

            if (flag == 1) {
                String finalRight = newItems.get(i).getRight();
                if (finalRight == null || finalRight.isEmpty()) {
                    finalRight = "99999999";
                }

                int rightFlag = getBaseVersion("snyk", right, false).compareTo(finalRight);

                if (rightFlag == 1) {
                    // No changes to 'last'
                } else if (rightFlag == 0) {
                    if (newItems.get(i).getRightOpen() || newItems.get(i).getLeftOpen()) {
                        last = new Item(last.getLeft(), last.getRight(), last.getLeftOpen(), true, null);
                    } else {
                        last = new Item(last.getLeft(), last.getRight(), last.getLeftOpen(), false, null);
                    }

                } else {
                    last = new Item(last.getLeft(), newItems.get(i).getRight(), last.getLeftOpen(),
                            newItems.get(i).getRightOpen(), null);
                }
            } else if (flag == 0) {
                if (newItems.get(i).getLeftOpen() || newItems.get(i).getRightOpen()) {
                    last = new Item(last.getLeft(), newItems.get(i).getRight(), last.getLeftOpen(), true, null);
                } else {
                    ret.add(last);
                    last = newItems.get(i);
                }
            } else {
                ret.add(last);
                last = newItems.get(i);
            }

        }
        ret.add(last);
        // Handle special case for '-' value
        while (!ret.isEmpty() && ((Item) ret.get(0)).getValue().equals("-")) {
            ret.remove(0);
        }

        if (ret.size() == 1) {
            return ret.get(0);
        } else {
            return new ItemGroup(true, ret); // Assuming ItemGroup constructor takes a list and a boolean
        }


    }
}
