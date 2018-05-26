package pikassoapp.buildappswithpaulo.com.pikasso;

import java.io.Serializable;

public class ColoringSize  implements Serializable {
    private int width;
    private int color;
    private Boolean erase;

    public ColoringSize(int width, int color, Boolean erase) {
        this.width = width;
        this.color = color;
        this.erase = erase;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Boolean getErase() {
        return erase;
    }

    public void setErase(Boolean erase) {
        this.erase = erase;
    }
}
