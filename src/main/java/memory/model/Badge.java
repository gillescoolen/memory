package memory.model;

import java.text.DecimalFormat;

public class Badge {
    private Integer id;
    private String url;

    public Badge(Integer id) {
        this.id = id;

        var decimalFormat = new DecimalFormat("00");
        this.url = String.format("/images/image%s.jpg", decimalFormat.format(id));
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
