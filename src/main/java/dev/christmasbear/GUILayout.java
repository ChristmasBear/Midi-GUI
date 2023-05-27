package dev.christmasbear;
import java.util.HashMap;
import java.util.Map;

public class GUILayout {
    static final Map<Integer, String[]> uiLayout = new HashMap<>() {{
        put(0, new String[]{"crash1", "hho", "hhc", "t1"});
        put(1, new String[]{"crash2", "snarer", "snare", "t2"});
        put(2, new String[]{"ride", "hhp", "bass", "t3"});
    }};

    static int[] getPos(String id) {
        for (int i = 0; i < uiLayout.size(); i++) {
            for (int j = 0; j < uiLayout.get(0).length; j++) {
                if (uiLayout.get(i)[j].equals(id)) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    //decay is per second
    static final Map<String, Decay> decay = new HashMap<>() {{
        put("crash1", new Decay(0, 0, 0.05f, 0));
        put("crash2", new Decay(0, 0, 0.05f, 0));
        put("ride", new Decay(0, 0, 0.05f, 0));
        put("hhc", new Decay(0, 0, 0, 0));
        put("hho", new Decay(0, 0, 0.1f, 0));
        put("hhp", new Decay(0, 0, 0, 0));
        put("snare", new Decay(0, 0, 0, 0));
        put("snarer", new Decay(0, 0, 0, 0));
        put("bass", new Decay(0, 0, 0, 0));
        put("t1", new Decay(0, 0, 0, 0));
        put("t2", new Decay(0, 0, 0, 0));
        put("t3", new Decay(0, 0, 0, 0));
    }};
}
