package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CameraUploader {

    /**
     * Loads camera data from a JSON file.
     *
     * @param filePath Path to the JSON file.
     * @return A map of camera IDs to their respective lists of CameraData.
     */
    public static Map<String, List<List<StampedDetectedObjects>>> loadCameraData(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            System.out.println("Loading camera data from: " + filePath);

            // Initialize Gson and define the type for deserialization
            Gson gson = new Gson();
            Type type = new TypeToken<ConcurrentHashMap<String, List<List<StampedDetectedObjects>>>>() {}.getType();

            // Deserialize the JSON data
            ConcurrentHashMap<String, List<List<StampedDetectedObjects>>> data = gson.fromJson(reader, type);

            // Check if data is null or empty
            if (data == null || data.isEmpty()) {
                System.err.println("Error: Camera data is null or empty.");
                return null;
            }

            // Log the parsed data for debugging
            for (Map.Entry<String, List<List<StampedDetectedObjects>>> entry : data.entrySet()) {
                List<List<StampedDetectedObjects>> nestedObjects = entry.getValue();
                if (nestedObjects != null) {
                    for (List<StampedDetectedObjects> stampedList : nestedObjects) {
                    }
                } else {
                    System.out.println("  No data available for this camera.");
                }
            }
            System.out.println("Camera data loaded successfully.");
            return data;

        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error parsing camera data: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
