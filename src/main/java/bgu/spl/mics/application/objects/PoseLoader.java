package bgu.spl.mics.application.objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class PoseLoader {

    /**
     * Loads Pose data from a JSON file.
     *
     * @param filePath Path to the JSON file.
     * @return A list of Pose objects.
     */
    public static List<Pose> loadPoses(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Pose>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            // Handle the error gracefully
            System.err.println("Failed to load Pose data from: " + filePath + ". Error: " + e.getMessage());
            return null; // Return null or an empty list as needed
        }
    }
}





