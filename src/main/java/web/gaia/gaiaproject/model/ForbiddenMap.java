package web.gaia.gaiaproject.model;


import java.util.HashMap;
import java.util.Map;

public class ForbiddenMap {
    public Map<String, Boolean> forbiddenMap;

    public ForbiddenMap() {
        forbiddenMap = new HashMap<>();
    }

    public Boolean isForbidden(String src) {
        return forbiddenMap.getOrDefault(src, false);
    }

    public void setForbidden(String src) {
        forbiddenMap.put(src, true);
    }
}
