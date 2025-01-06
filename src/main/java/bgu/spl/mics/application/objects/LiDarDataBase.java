package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBusImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */

public class LiDarDataBase {

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */

    List<StampedCloudPoints> StumpedCloudPoints;
    public LiDarDataBase(){
        this.StumpedCloudPoints=new ArrayList<>();
    }

    public List<StampedCloudPoints> getStumpedCloudPoints() {
        return StumpedCloudPoints;
    }
    private void loadFromFile(String filePath)  {////++++ changed
        Gson gson = new Gson();
        Type type = new TypeToken<List<StampedCloudPoints>>() {}.getType();

        try (FileReader reader = new FileReader(filePath)) {
            List<StampedCloudPoints> data = gson.fromJson(reader, type);
            this.StumpedCloudPoints.addAll(data);
        } catch (IOException e) {
        System.err.println("Failed to load file: " + filePath);
        e.printStackTrace(); // הדפסת ה-Stack Trace לצורך Debug
        }
    }
    public static void initialize(String filePath) {
        SingletonHolder.INSTANCE.loadFromFile(filePath);
    }
    public static LiDarDataBase getInstance() {
        return SingletonHolder.INSTANCE;
    }
    public static LiDarDataBase getInstance(String filePath) {
        initialize(filePath);
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final LiDarDataBase INSTANCE = new LiDarDataBase();
    }
}
