package com.flatflatching.flatflatching.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafael on 28.09.2015.
 */
public class ShoppingList {
    private List<ShoppingListItem> itemList = new ArrayList<>();

    ShoppingList() {

    }

    public void addItem(ShoppingListItem item) {
        this.itemList.add(item);
    }

    public List<ShoppingListItem> getShoppingList() {
        return new ArrayList<>(itemList);
    }

    public double getTotal() {
        double total = 0;
        for (ShoppingListItem item: this.itemList) {
            total += item.getTotal();
        }
        return total;
    }

}
