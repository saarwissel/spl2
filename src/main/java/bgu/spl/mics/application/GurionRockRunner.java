package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GurionRockRunner {

    private static final Logger logger = Logger.getLogger(GurionRockRunner.class.getName());

    public static void main(String[] args) {
        // בדיקת ארגומנטים
        if (args.length == 0) {
            logger.severe("Error: No configuration file path provided.");
            System.err.println("Please provide a configuration file path.");
            return;
        }

        String configPath = args[0];

        // בדיקה שהנתיב הוא תקין וקיים
        File configDirectory = new File(configPath);
        if (!configDirectory.exists() || !configDirectory.isDirectory()) {
            logger.severe("Error: Provided path is not a valid directory: " + configPath);
            System.err.println("Invalid configuration directory: " + configPath);
            return;
        }

        logger.info("Initializing system components...");
        try {
            // טעינת נתוני LiDAR ו-Pose
            LiDarDataBase lidarDataBase = LiDarDataBase.getInstance(configPath + "/lidar_data.json");
            List<Pose> poses = PoseLoader.loadPoses(configPath + "/pose_data.json");
            if (poses == null || poses.isEmpty()) {
                logger.severe("Error: Failed to load pose data from: " + configPath + "/pose_data.json");
                return;
            }

            GPSIMU gpsimu = new GPSIMU(0);
            gpsimu.getPoseList().addAll(poses);

            // יצירת אובייקטים ומצלמות
            Camera camera = new Camera(1, 2);
            LiDarWorkerTracker lidarWorkerTracker = new LiDarWorkerTracker(1, 2);

            // יצירת שירותים
            CameraService cameraService = new CameraService(camera);
            LiDarWorkerService lidarService = new LiDarWorkerService(lidarWorkerTracker);
            PoseService poseService = new PoseService(gpsimu);
            FusionSlam fusionSlam = FusionSlam.getInstance();
            FusionSlamService fusionSlamService = new FusionSlamService(fusionSlam);
            TimeService timeService = new TimeService(1000, 50); // זמן טיק ודורציה לדוגמה

            // אתחול שירותים
            logger.info("Starting services...");
            Thread cameraThread = new Thread(cameraService);
            Thread lidarThread = new Thread(lidarService);
            Thread poseThread = new Thread(poseService);
            Thread fusionThread = new Thread(fusionSlamService);
            Thread timeThread = new Thread(timeService);

            MessageBusImpl bus = MessageBusImpl.getInstance();
            bus.register(cameraService);
            bus.register(lidarService);
            bus.register(poseService);
            bus.register(fusionSlamService);
            bus.register(timeService);

            // נפעיל את השירותים הרצויים
            cameraThread.start();
            lidarThread.start();
            poseThread.start();
            fusionThread.start();
            timeThread.start();

            // סיום התוכנית
            timeThread.join();
            logger.info("Simulation completed successfully.");
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Simulation interrupted: ", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during simulation: ", e);
        }
    }
}
