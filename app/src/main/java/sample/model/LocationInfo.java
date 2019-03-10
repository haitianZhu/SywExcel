package sample.model;

/**
 * @Author: haitian
 * @Date: 2019/3/8 4:46 PM
 * @Description:
 */
public class LocationInfo {

    public int row;

    public String name;

    public double longitude;

    public double latitude;

    public LocationInfo(int row, String name, double longitude, double latitude) {
        this.row = row;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
