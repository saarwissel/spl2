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
            System.out.println("Loading camera data from: " + filePath);
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<List<StampedDetectedObjects>>>>() {}.getType();
            Map<String, List<List<StampedDetectedObjects>>> data = gson.fromJson(reader, type);
            System.out.println("Camera data loaded successfully: " + data);
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



