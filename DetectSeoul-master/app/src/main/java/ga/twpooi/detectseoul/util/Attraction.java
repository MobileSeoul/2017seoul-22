package ga.twpooi.detectseoul.util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by tw on 2017. 9. 4..
 */

public class Attraction implements Serializable {

    public String id, title, sContents, contents, address, district, telephone, web, url;
    public Double lat, lng;
    public ArrayList<String> categorize, picture;
    public ArrayList<String[]> detail;

    public Attraction(){
        categorize = new ArrayList<>();
        picture = new ArrayList<>();
        detail = new ArrayList<>();
    }

    public boolean isHaveLatLng(){
        return !(lat == 0.0 && lng == 0.0);
    }

}
