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

    List<StampedCloudPoints> cloudPoints;
    public LiDarDataBase(){
        this.cloudPoints=new ArrayList<>();
    }

    public List<StampedCloudPoints> getCloudPoints() {
        return cloudPoints;
    }
    public StampedCloudPoints getCordinate(String id){///////+++++++++chack withgal
        for(StampedCloudPoints t:this.cloudPoints){
            if(t.getId()==id){
             return t;
            }
        }
        return null;
    }
    private void loadFromFile(String filePath)  {////++++ lookwith gal
        Gson gson = new Gson();
        Type type = new TypeToken<List<StampedCloudPoints>>() {}.getType();

        try (FileReader reader = new FileReader(filePath)) {
            List<StampedCloudPoints> data = gson.fromJson(reader, type);
            cloudPoints.addAll(data); // טעינת הנתונים לתוך הרשימה
        } catch (IOException e) {
        // טיפול בחריגה, ניתן להדפיס הודעת שגיאה או לרשום ל-Log
        System.err.println("Failed to load file: " + filePath);
        e.printStackTrace(); // הדפסת ה-Stack Trace לצורך Debug
        }
    }

    public static LiDarDataBase getInstance(String filePath) {///++++check with gall
        if (SingletonHolder.INSTANCE == null) {
            SingletonHolder.INSTANCE = new LiDarDataBase();
            SingletonHolder.INSTANCE.loadFromFile(filePath); // טעינת קובץ JSON
        }
        return SingletonHolder.INSTANCE;
    }
    private static class SingletonHolder {
        private static  LiDarDataBase INSTANCE = new LiDarDataBase();
    }
}
