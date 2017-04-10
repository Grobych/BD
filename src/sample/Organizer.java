package sample;

/**
 * Created by Alex on 20.12.2016.
 */
public class Organizer {
    private int id;
    private String name;
    private int revenue;

    public Organizer(int id, String name, int revenue)
    {
        this.id=id;
        this.name=name;
        this.revenue=revenue;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getRevenue() {
        return revenue;
    }
}
