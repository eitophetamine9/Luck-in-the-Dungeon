package model;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<GachaItem> items;
    private int maxCapacity;

    public Inventory(int maxCapacity){
        this.items = new ArrayList<>();
        this.maxCapacity = maxCapacity;
    }

    public boolean addItem(GachaItem item){
        if(items.size() < maxCapacity){
            items.add(item);
            return true;
        }
        return false;
    }

    public void removeItem(GachaItem item){
        items.remove(item);
    }

    public List<GachaItem> getItems() {
        return new ArrayList<>(items); // Return copy of actual items
    }

    public boolean isFull(){
        return items.size() >= maxCapacity;
    }

    public boolean containsItem(String itemName){
        for(GachaItem item : items){
            if(item.getName().equals(itemName)) return true;
        }
        return false;
    }
}
