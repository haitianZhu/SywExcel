package sample.model;

/**
 * @Author: haitian
 * @Date: 2019/3/7 10:59 PM
 * @Description:
 */
public class MatchResult {

    public double matchDistance = 0;
    public String sourceStr = "";

    public MatchResult(double matchDistance, String sourceStr) {
        this.matchDistance = matchDistance;
        this.sourceStr = sourceStr;
    }
}
