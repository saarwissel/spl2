package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GurionRockRunner {

    private static final Logger logger = Logger.getLogger(GurionRockRunner.class.getName());

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.severe("Error: No configuration file path provided.");
            System.err.println("Please provide a configuration file path.");
            return;
        }

        String configPath = args[0];
        String lidarDataPath = configPath.replace("configuration_file.json", "lidar_data.json");
        String poseDataPath = configPath.replace("configuration_file.json", "pose_data.json");

        logger.info("Initializing system components...");
        try {
            // אתחול LiDarDataBase
            try {
                LiDarDataBase.initialize(lidarDataPath);
                logger.info("LiDarDataBase initialized successfully. Loaded LiDAR data: " +
                        LiDarDataBase.getInstance().getStumpedCloudPoints().size());
            } catch (Exception e) {
                logger.severe("Failed to initialize LiDarDataBase: " + e.getMessage());
                return;
            }

            // אתחול GPSIMU עם נתוני Pose
            GPSIMU gpsimu = new GPSIMU(0);
            List<Pose> poses = PoseLoader.loadPoses(poseDataPath);
            if (poses == null || poses.isEmpty()) {
                logger.severe("Error: No poses loaded from " + poseDataPath);
                return;
            }
            gpsimu.getPoseList().addAll(poses);
            logger.info("Loaded poses: " + poses.size());

            // יצירת LiDAR Worker Services
            LiDarWorkerTracker lidarTracker1 = new LiDarWorkerTracker(1, 4);
            LiDarWorkerTracker lidarTracker2 = new LiDarWorkerTracker(2, 3);

            LiDarWorkerService lidarService1 = new LiDarWorkerService(lidarTracker1);
            LiDarWorkerService lidarService2 = new LiDarWorkerService(lidarTracker2);

            // יצירת Camera Services
            Camera camera1 = new Camera(1, 2);
            Camera camera2 = new Camera(2, 1);

            CameraService cameraService1 = new CameraService(camera1);
            CameraService cameraService2 = new CameraService(camera2);

            // יצירת FusionSlam Services
            FusionSlam fusionSlam = FusionSlam.getInstance();
            FusionSlamService fusionSlamService = new FusionSlamService(fusionSlam);

            // יצירת Pose Service
            PoseService poseService = new PoseService(gpsimu);

            // יצירת Time Service
            TimeService timeService = new TimeService(1000, 50); // 50 טיקים לדוגמה

            // רישום כל השירותים ל-MessageBus
            MessageBusImpl bus = MessageBusImpl.getInstance();
            /*logger.info("Registering MicroServices...");
            bus.register(lidarService1);
            logger.info("Registered LiDAR Service 1");
            bus.register(lidarService2);
            logger.info("Registered LiDAR Service 2");
            bus.register(cameraService1);
            logger.info("Registered Camera Service 1");
            bus.register(cameraService2);
            logger.info("Registered Camera Service 2");
            bus.register(poseService);
            logger.info("Registered Pose Service");
            bus.register(fusionSlamService);
            logger.info("Registered FusionSlam Service");
            bus.register(timeService);
            logger.info("Registered Time Service");
            */

            // יצירת Threads לכל השירותים
            List<Thread> threads = new ArrayList<>();
            threads.add(new Thread(lidarService1));
            threads.add(new Thread(lidarService2));
            threads.add(new Thread(cameraService1));
            threads.add(new Thread(cameraService2));
            threads.add(new Thread(poseService));
            threads.add(new Thread(fusionSlamService));
            threads.add(new Thread(timeService));

            // הפעלת כל השירותים
            logger.info("Starting threads...");
            for (Thread thread : threads) {
                logger.info("Starting thread: " + thread.getName());
                thread.start();
            }

            // המתנה לסיום כל השירותים
            logger.info("Waiting for threads to complete...");
            for (Thread thread : threads) {
                thread.join();
                logger.info("Thread " + thread.getName() + " completed. State: " + thread.getState());
            }

            logger.info("Simulation completed successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during simulation: ", e);
        }
    }
}
