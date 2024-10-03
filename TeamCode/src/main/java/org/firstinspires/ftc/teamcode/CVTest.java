package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class CVTest extends LinearOpMode {

    private ComputerVision cv;
    private int[][] rgb;

    private int middleRGBAverage;
    @Override
    public void runOpMode() {
        cv = new ComputerVision(hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()), true);
        waitForStart();

        while (opModeIsActive()) {
            rgb = cv.getRGB();
            int recognitionIndex = cv.getRecognition();

            // Use a switch statement to determine the position
            String position;
            switch (recognitionIndex) {
                case 0:
                    position = "Left";
                    break;
                case 1:
                    position = "Middle";
                    break;
                case 2:
                    position = "Right";
                    break;
                default:
                    position = "Unknown";
                    break;
            }

            // Add to telemetry
            telemetry.addData("Position", position);
            telemetry.addData("Recognition Index", recognitionIndex);

            // Calculate average RGB values for the middle region
            double[] avg = new double[3];
            for (int i = 4; i < 8; i++) {
                avg[0] += rgb[i][0];
                avg[1] += rgb[i][1];
                avg[2] += rgb[i][2];
            }
            avg[0] /= 4;
            avg[1] /= 4;
            avg[2] /= 4;

            // Add RGB averages to telemetry
            telemetry.addData("Average Red", avg[0]);
            telemetry.addData("Average Green", avg[1]);
            telemetry.addData("Average Blue", avg[2]);

            // Add the 'x' value to telemetry
            telemetry.addData("X Value", cv.getX());

            // Update telemetry with all the new data
            telemetry.update();
        }
    }
}