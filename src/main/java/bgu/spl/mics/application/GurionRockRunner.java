package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import java.nio.file.Paths;

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
        String outputPath = Paths.get(args[0]).getParent().toAbsolutePath().toString();

        try {
            // Load configuration
            Gson gson = new Gson();
            Configuration config = gson.fromJson(new java.io.FileReader(configPath), Configuration.class);

            logger.info("Configuration loaded successfully.");

            // Load LiDAR Data
            LiDarDataBase.getInstance(config.Lidars.lidars_data_path);
            List<StampedCloudPoints> lidarData = LiDarDataBase.getInstance().getStumpedCloudPoints();
            if (lidarData == null || lidarData.isEmpty()) {
                logger.severe("LiDAR data is null or empty.");
                return;
            }
            logger.info("LiDAR data loaded successfully with " + lidarData.size() + " stamped cloud points.");



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

            logger.info("Pose data loaded successfully. Loaded poses: " + poses.size());

            // Initialize GPSIMU
            GPSIMU gpsimu = new GPSIMU(0);
            gpsimu.getPoseList().addAll(poses);

            // Create services
            List<Thread> threads = new ArrayList<>();
            MessageBusImpl bus = MessageBusImpl.getInstance();



            // Initialize Cameras
            int serviceCounter = 0;
            for (Configuration.CameraConfig camConfig : config.Cameras.CamerasConfigurations) {
                // Get detected objects from cameraData using the camera_key
                List<List<StampedDetectedObjects>> nestedObjects = cameraData.get(camConfig.camera_key);

                // Flatten the nested lists into a single list
                List<StampedDetectedObjects> flattenedObjects = new ArrayList<>();
                if (nestedObjects != null) {
                    for (List<StampedDetectedObjects> stampedList : nestedObjects) {
                        flattenedObjects.addAll(stampedList);
                    }
                } else {
                    logger.warning("No detected objects found for camera key: " + camConfig.camera_key);
                }

                // Create Camera object with associated detected objects
                Camera camera = new Camera(camConfig.id, camConfig.frequency, flattenedObjects);
                CameraService cameraService = new CameraService(camera);
                serviceCounter++;
                threads.add(new Thread(cameraService));

            }

            // Initialize LiDAR Workers
            for (Configuration.LiDarConfig lidarConfig : config.Lidars.LidarConfigurations) {
                // Filter data relevant to the LiDAR worker (if applicable)
                // Create LiDarWorkerTracker with its associated data
                LiDarWorkerTracker lidarTracker = new LiDarWorkerTracker(lidarConfig.id, lidarConfig.frequency);

                // Create and start LiDarWorkerService
                LiDarWorkerService lidarService = new LiDarWorkerService(lidarTracker);
                threads.add(new Thread(lidarService));
                logger.info("Initialized LiDAR Worker with ID: " + lidarConfig.id + ", Points: " );
                serviceCounter++;
            }

            // Initialize FusionSlam
            FusionSlam fusionSlam = FusionSlam.getInstance();
            FusionSlamService fusionSlamService = new FusionSlamService(fusionSlam);
            threads.add(new Thread(fusionSlamService));
            serviceCounter++;

            // Initialize Pose Service
            PoseService poseService = new PoseService(gpsimu);
            threads.add(new Thread(poseService));
            serviceCounter++;

            SystemServicesCountDownLatch.init(serviceCounter);
            System.out.println(serviceCounter);



            // Initialize Time Service


            // Start Threads
            logger.info("Starting all services...");


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


            JSONOutputWriter.writeStatisticsToFile();

            logger.info("Simulation completed. Output written to JSON.");

        } catch (Exception e) {
            logger.severe("An error occurred during the simulation: " + e.getMessage());
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
            List<StampedDetectedObjects> detectedObjects; // רשימת אובייקטים שזוהו
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
