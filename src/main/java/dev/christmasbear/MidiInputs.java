package dev.christmasbear;
import java.util.HashMap;
import java.util.Map;

public class MidiInputs {
    public static Map<String, int[]> idToMidi  = new HashMap<>() {{
        put("crash1", new int[]{49, 55});
        put("crash2", new int[]{57, 52});
        put("ride", new int[]{51, 59, 53});
        put("hhc", new int[]{46, 26, 42, 22});
        put("hho", new int[]{46, 26});
        put("hhp", new int[]{44});
        put("snare", new int[]{38});
        put("snarer", new int[]{40});
        put("bass", new int[]{36, 27});
        put("t1", new int[]{48, 50});
        put("t2", new int[]{47, 45});
        put("t3", new int[]{43, 58});
    }};

    public static Map<String, String> idToName = new HashMap<>() {{
        put("crash1", "Crash 1");
        put("crash2", "Crash 2");
        put("ride", "Ride");
        put("hhc", "Hi Hat Closed");
        put("hho", "Hi Hat Open");
        put("hhp", "Hi Hat Pedal");
        put("snare", "Snare");
        put("snarer", "Snare Rim");
        put("bass", "Bass Drum");
        put("t1", "Tom 1");
        put("t2", "Tom 2");
        put("t3", "Tom 3");
    }};

}
