package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.ArrayList;
import java.util.List;

@TeleOp
public class CVTest extends LinearOpMode {

    private ComputerVision cv;
    private int[][] rgb;

    // Adjustable detection parameters for black object
    private static final int BRIGHTNESS_THRESHOLD = 50; // Higher number = more lenient black detection
    private static final int MIN_DARK_REGIONS = 3; // Minimum number of dark regions needed
    private static final double DETECTION_CONFIDENCE_THRESHOLD = 0.5; // 50% confidence required

    // Debug counters
    private int totalFramesProcessed = 0;
    private int successfulDetections = 0;
    private List<Double> confidenceHistory = new ArrayList<>();
    private List<Integer> darkRegionsHistory = new ArrayList<>();

    @Override
    public void runOpMode() {
        cv = new ComputerVision(hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()), true);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Debug", "Press start to begin black object detection");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            totalFramesProcessed++;
            rgb = cv.getRGB();

            // Debug: Raw RGB values from different regions
            telemetry.addLine("\n=== Raw RGB Values ===");
            for (int i = 0; i < rgb.length; i++) {
                int brightness = calculateBrightness(rgb[i][0], rgb[i][1], rgb[i][2]);
                telemetry.addData(String.format("Region %d", i),
                        String.format("R:%d G:%d B:%d Brightness:%d %s",
                                rgb[i][0], rgb[i][1], rgb[i][2], brightness,
                                (brightness < BRIGHTNESS_THRESHOLD ? "DARK" : "")));
            }

            // Calculate detection metrics
            DetectionResult result = calculateDetectionMetrics();

            // Store history
            confidenceHistory.add(result.confidence);
            darkRegionsHistory.add(result.darkRegionsCount);
            if (confidenceHistory.size() > 10) {
                confidenceHistory.remove(0);
                darkRegionsHistory.remove(0);
            }

            // Update detection counter
            if (result.isDetected) {
                successfulDetections++;
            }

            // Debug: Detection Statistics
            telemetry.addLine("\n=== Detection Stats ===");
            telemetry.addData("Black Object Detected", result.isDetected);
            telemetry.addData("Confidence", String.format("%.2f%%", result.confidence * 100));
            telemetry.addData("Dark Regions Found", result.darkRegionsCount);
            telemetry.addData("Average Brightness", String.format("%.1f", result.averageBrightness));
            telemetry.addData("Detection Rate", String.format("%.2f%%",
                    (double)successfulDetections / totalFramesProcessed * 100));

            // Debug: Region Analysis
            telemetry.addLine("\n=== Region Analysis ===");
            telemetry.addData("Darkest Region Brightness", result.darkestRegionBrightness);
            telemetry.addData("Brightest Region Brightness", result.brightestRegionBrightness);
            telemetry.addData("Brightness Variance", String.format("%.1f", result.brightnessVariance));

            // Debug: Historical Data
            telemetry.addLine("\n=== Detection History ===");
            telemetry.addData("Dark Regions Trend", darkRegionsHistory.toString());
            telemetry.addData("Confidence Trend",
                    confidenceHistory.stream()
                            .map(d -> String.format("%.2f", d))
                            .collect(java.util.stream.Collectors.joining(", ")));

            // Camera Position Debug
            telemetry.addLine("\n=== Camera Info ===");
            telemetry.addData("Camera X Position", cv.getX());
            telemetry.addData("Total Frames", totalFramesProcessed);

            telemetry.update();
        }
    }

    private static class DetectionResult {
        boolean isDetected;
        double confidence;
        int darkRegionsCount;
        double averageBrightness;
        int darkestRegionBrightness;
        int brightestRegionBrightness;
        double brightnessVariance;
    }

    private DetectionResult calculateDetectionMetrics() {
        DetectionResult result = new DetectionResult();
        result.darkRegionsCount = 0;
        result.darkestRegionBrightness = 255;
        result.brightestRegionBrightness = 0;
        double totalBrightness = 0;
        List<Integer> brightnessList = new ArrayList<>();

        // First pass: collect basic statistics
        for (int[] region : rgb) {
            int brightness = calculateBrightness(region[0], region[1], region[2]);
            brightnessList.add(brightness);
            totalBrightness += brightness;

            if (brightness < BRIGHTNESS_THRESHOLD) {
                result.darkRegionsCount++;
            }

            result.darkestRegionBrightness = Math.min(result.darkestRegionBrightness, brightness);
            result.brightestRegionBrightness = Math.max(result.brightestRegionBrightness, brightness);
        }

        // Calculate average and variance
        result.averageBrightness = totalBrightness / rgb.length;

        double sumSquaredDiff = 0;
        for (int brightness : brightnessList) {
            sumSquaredDiff += Math.pow(brightness - result.averageBrightness, 2);
        }
        result.brightnessVariance = sumSquaredDiff / rgb.length;

        // Calculate confidence based on multiple factors
        double darkRegionConfidence = (double)result.darkRegionsCount / MIN_DARK_REGIONS;
        double brightnessConfidence = 1.0 - (result.averageBrightness / 255.0);
        double varianceConfidence = Math.min(1.0, result.brightnessVariance / 1000.0);

        result.confidence = (darkRegionConfidence * 0.5 +
                brightnessConfidence * 0.3 +
                varianceConfidence * 0.2);

        result.isDetected = result.confidence >= DETECTION_CONFIDENCE_THRESHOLD &&
                result.darkRegionsCount >= MIN_DARK_REGIONS;

        return result;
    }

    private int calculateBrightness(int r, int g, int b) {
        // Weighted brightness calculation (human eye is more sensitive to green)
        return (int)(r * 0.299 + g * 0.587 + b * 0.114);
    }
}