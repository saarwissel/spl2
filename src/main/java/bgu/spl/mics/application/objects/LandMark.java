package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    String id;
    String Description;
    List<CloudPoint> LandCloudPoints;
    double globalX;
    double gloablY;
    Pose pose;


    public LandMark(String id, String Description, List<CloudPoint>TrackedCloudPoints,Pose pose){
        this.id=id;
        this.Description=Description;
        this.LandCloudPoints=new ArrayList<>();
        this.globalX = 0;
        this.gloablY = 0;
        if(TrackedCloudPoints.size()>0){
            for(CloudPoint cloudPoint:TrackedCloudPoints) {
                this.globalX = calculX(cloudPoint.getX(), cloudPoint.getY());
                this.gloablY = calculY(cloudPoint.getX(), cloudPoint.getY());
                LandCloudPoints.add(new CloudPoint(this.globalX, this.gloablY));
            }
        }
        this.pose=pose;
    }

    public String getId() {
        return id;
    }

    public double getGlobalX() {
        return this.globalX;
    }

    public double getGloablY() {
        return this.gloablY;
    }

    public List<CloudPoint> getLandCloudPoints() {
        return LandCloudPoints;
    }

    public String getDescription() {
        return Description;
    }

    public double calculX(double x, double y){
        double radians = Math.toRadians(pose.getYaw());
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double x1 = x*cos;
        double y1 = y*sin;
        return x1-y1+pose.getX();
    }
    public double calculY(double x, double y){
        double radians = Math.toRadians(pose.getYaw());
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double x1 = x*sin;
        double y1 = y*cos;
        return x1+y1+pose.getY();
    }

    public void update(List<CloudPoint>TrackedCloudPoints){
        List<CloudPoint> LandToAdd=new ArrayList<>();
        if(TrackedCloudPoints.size()>0) {
            for (CloudPoint cloudPoint : TrackedCloudPoints) {
                this.globalX = calculX(cloudPoint.getX(), cloudPoint.getY());
                this.gloablY = calculY(cloudPoint.getX(), cloudPoint.getY());
                LandToAdd.add(new CloudPoint(this.globalX, this.gloablY));
            }
        }
        int i = 0;
        int j = 0;
        while (i<this.LandCloudPoints.size() && j<LandToAdd.size()){
            this.LandCloudPoints.get(i).x  = (this.LandCloudPoints.get(i).x + LandToAdd.get(j).x)/2;
            this.LandCloudPoints.get(i).y  = (this.LandCloudPoints.get(i).y + LandToAdd.get(j).y)/2;
            i++;
            j++;
        }
        while (j<LandToAdd.size()){
            this.LandCloudPoints.add(LandToAdd.get(j));
            j++;
        }

    }

    @Override
    public String toString() {
        return super.toString();
    }
}
