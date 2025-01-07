package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class GurionRockRunner {

    private static final Logger logger = Logger.getLogger(GurionRockRunner.class.getName());

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide a configuration file path.");
            return;
        }

        String configPath = args[0];
        String outputPath = "C:\\Users\\saarw\\Downloads\\Skeleton\\example_input_2\\output_file.json";

        try {
            // Load configuration
            Gson gson = new Gson();
            Configuration config = gson.fromJson(new java.io.FileReader(configPath), Configuration.class);

            logger.info("Configuration loaded successfully.");

            // Load LiDAR Data
            LiDarDataBase.initialize(config.Lidars.lidars_data_path);
            logger.info("LiDAR data loaded successfully.");

            // Load Camera Data
            Map<String, List<List<StampedDetectedObjects>>> cameraData = CameraUploader.loadCameraData(config.Cameras.camera_datas_path);
            if (cameraData == null) {
                logger.severe("Failed to load camera data.");
                return;
            }
            logger.info("Camera data loaded successfully for cameras: " + cameraData.keySet());


            // Load Pose Data
            List<Pose> poses = PoseLoader.loadPoses(config.poseJsonFile);
            if (poses == null || poses.isEmpty()) {
                logger.severe("Failed to load pose data.");
                return;
            }
            System.out.println("הגענו עד הלום ");

            logger.info("Pose data loaded successfully. Loaded poses: " + poses.size());

            // Initialize GPSIMU
            GPSIMU gpsimu = new GPSIMU(0);
            gpsimu.getPoseList().addAll(poses);

            // Create services
            List<Thread> threads = new ArrayList<>();
            MessageBusImpl bus = MessageBusImpl.getInstance();

            // Initialize Cameras
            for (Configuration.CameraConfig camConfig : config.Cameras.CamerasConfigurations) {
                Camera camera = new Camera(camConfig.id, camConfig.frequency);
                CameraService cameraService = new CameraService(camera);
                threads.add(new Thread(cameraService));

            }
            // Initialize LiDAR Workers
            for (Configuration.LiDarConfig lidarConfig : config.Lidars.LidarConfigurations) {
                LiDarWorkerTracker lidarTracker = new LiDarWorkerTracker(lidarConfig.id, lidarConfig.frequency);
                LiDarWorkerService lidarService = new LiDarWorkerService(lidarTracker);
                threads.add(new Thread(lidarService));
            }

            // Initialize FusionSlam
            FusionSlam fusionSlam = FusionSlam.getInstance();
            FusionSlamService fusionSlamService = new FusionSlamService(fusionSlam);
            threads.add(new Thread(fusionSlamService));

            // Initialize Pose Service
            PoseService poseService = new PoseService(gpsimu);
            threads.add(new Thread(poseService));


            // Initialize Time Service


            // Start Threads
            logger.info("Starting all services...");
            System.out.println("הגענו עד הלום ");

            for (Thread thread : threads) {
                thread.start();
            }
            TimeService timeService = new TimeService(config.TickTime, config.Duration);
            Thread t = new Thread(timeService);
            threads.add(t);
            t.start();

            // Wait for Threads to Complete
            logger.info("Waiting for services to complete...");
            for (Thread thread : threads) {
                thread.join();
            }

            // Output Results
            writeOutput(outputPath, fusionSlam);
            logger.info("Simulation completed successfully. Output written to: " + outputPath);

        } catch (Exception e) {
            logger.severe("An error occurred during the simulation: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static void writeOutput(String outputPath, FusionSlam fusionSlam) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            JsonObject output = new JsonObject();

            // Add landmarks to output
            JsonArray landmarks = new JsonArray();
            for (LandMark landmark : fusionSlam.getLandMarks()) {
                JsonObject landMarkJson = new JsonObject();
                landMarkJson.addProperty("id", landmark.getId());
                landMarkJson.addProperty("description", landmark.getDescription());
                JsonArray coordinates = new JsonArray();
                for (CloudPoint point : landmark.getLandCloudPoints()) {
                    JsonObject coordinate = new JsonObject();
                    coordinate.addProperty("x", point.getX());
                    coordinate.addProperty("y", point.getY());
                    coordinates.add(coordinate);
                }
                landMarkJson.add("coordinates", coordinates);
                landmarks.add(landMarkJson);
            }
            output.add("landMarks", landmarks);

            // Write to file
            writer.write(new Gson().toJson(output));
        } catch (IOException e) {
            System.err.println("Failed to write output file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Configuration Class
    private static class Configuration {
        CamerasConfig Cameras;
        LidarsConfig Lidars;
        String poseJsonFile;
        int TickTime;
        int Duration;

        static class CamerasConfig {
            List<CameraConfig> CamerasConfigurations;
            String camera_datas_path;
        }

        static class CameraConfig {
            int id;
            int frequency;
            String camera_key;
        }

        static class LidarsConfig {
            List<LiDarConfig> LidarConfigurations;
            String lidars_data_path;
        }

        static class LiDarConfig {
            int id;
            int frequency;
        }
    }
}
