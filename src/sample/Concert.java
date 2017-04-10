package sample;

import java.util.List;

/**
 * Created by Alex on 19.12.2016.
 */
public class Concert {
    private int ID;
    private String name;
    private String group;
    private String date;
    private String address;
    private int cost;
    private String place;

    public Concert(int ID, String name, String groups, String date, String place, String address, int cost)
    {
        this.group = groups;
        this.address=address;
        this.cost=cost;
        this.date=date;
        this.ID=ID;
        this.name=name;
        this.place=place;
    }

    public int getCost() {
        return cost;
    }

    public int getID() {
        return ID;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

}
