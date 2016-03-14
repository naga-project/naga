package naga.core.util.collection;

import java.util.AbstractList;

/**
 * @author Bruno Salmon
 */
public class IdentityList extends AbstractList<Integer> {

    private int size;

    public IdentityList() {
    }

    public IdentityList(int size) {
        setSize(size);
    }

    @Override
    public Integer get(int index) {
        return index;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }
}
