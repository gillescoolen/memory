package memory.model;

import memory.util.Observable;

public class Card extends Observable<Card> {
    private Integer id;
    private String url;
    private boolean selected = false;
    private boolean shown = false;
    private boolean cheatMode;
    private final String cover = "/images/reverse.jpg";

    public Card(Integer id, String url, boolean cheatMode) {
        this.id = id;
        this.url = url;
        this.cheatMode = cheatMode;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
        this.update(this);
    }

    public String getUrl() {
        return this.url;
    }

    public String getCover() {
        return (this.cheatMode) ? this.url : this.cover;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void select(boolean selected) {
        this.selected = selected;
        this.update(this);
    }

    public boolean isShown() {
        return shown;
    }

    public void show(boolean shown) {
        this.shown = shown;
        this.update(this);
    }
}
