package org.grobid.core.utilities;

/**
 * User: zholudev
 * Date: 2/17/14
 */
public class ElementCounterItem<T> {
    private T item;
    private Integer cnt;


    //for Jackson
    public ElementCounterItem() {
    }

    public ElementCounterItem(T item, Integer cnt) {
        this.item = item;
        this.cnt = cnt;
    }

    public T getItem() {
        return item;
    }

    public java.lang.Integer getCnt() {
        return cnt;
    }
}
