package bgu.spl.mics.application.objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JSONOutputWriter handles writing the output data to a JSON file.
 */
public class JSONOutputWriter {
    private static final String OUTPUT_FILE = "C:\\Users\\saarw\\Downloads\\Skeleton\\example_input_2\\output.json";

    public static void writeStatisticsToFile() {
        StatisticalFolder stats = StatisticalFolder.getInstance();
        List<LandMark> landmarks = FusionSlam.getInstance().getLandMarks();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("systemRuntime", stats.getSystemRuntime().get());
        jsonObject.addProperty("numDetectedObjects", stats.getNumDetectedObjects().get());
        jsonObject.addProperty("numTrackedObjects", stats.getNumTrackedObjects().get());
        jsonObject.addProperty("numLandmarks", stats.getNumLandmarks().get());

        JsonArray landmarksArray = new JsonArray();
        for (LandMark landmark : landmarks) {
            JsonObject landmarkJson = new JsonObject();
            landmarkJson.addProperty("id", landmark.getId());
            landmarkJson.addProperty("description", landmark.getDescription());
            landmarkJson.addProperty("globalX", landmark.getGlobalX());
            landmarkJson.addProperty("globalY", landmark.getGloablY());
            landmarksArray.add(landmarkJson);
        }

        jsonObject.add("landmarks", landmarksArray);

        File file = new File(OUTPUT_FILE);
        // Ensure the parent directory exists
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new Gson();
            gson.toJson(jsonObject, writer);
            System.out.println("Data written to " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write data to " + OUTPUT_FILE);
            e.printStackTrace();
        }
    }
}
