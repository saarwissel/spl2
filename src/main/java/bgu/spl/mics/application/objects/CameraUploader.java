package bgu.spl.mics.application.objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class CameraUploader {

    /**
     * Loads camera data from a JSON file.
     *
     * @param filePath Path to the JSON file.
     * @return A map of camera IDs to their respective lists of CameraData.
     */
        public static Map<String, List<List<StampedDetectedObjects>>> loadCameraData(String filePath) {
            try (FileReader reader = new FileReader(filePath)) {
                Gson gson = new Gson();
                // התאמת סוג הנתונים למערך של מערכים
                Type type = new TypeToken<Map<String, List<List<StampedDetectedObjects>>>>() {}.getType();
                return gson.fromJson(reader, type);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }



