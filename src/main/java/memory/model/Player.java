package memory.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import memory.util.Observable;

import java.util.ArrayList;
import java.util.List;

public class Player extends Observable<Player> {
    private StringProperty name;
    private List<Badge> badges = new ArrayList<>();

    public Player(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public StringProperty getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name.set(name);
        this.update(this);
    }

    public List<Badge> getBadges() {
        return List.copyOf(this.badges);
    }

    public void addBadge(Badge badge) {
        this.badges.add(badge);
        this.update(this);
    }
}
