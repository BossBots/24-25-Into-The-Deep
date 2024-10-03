//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//@TeleOp
//public class CVTestBlue extends LinearOpMode {
//
//    private ComputerVisionBlue cv;
//    private int[][] rgb;
//
//    private int middleRGBAverage;
//    @Override
//    public void runOpMode() {
//        cv = new ComputerVisionBlue(hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
//        waitForStart();
//
//        while (opModeIsActive()) {
//            rgb = cv.getRGB();
//            telemetry.addData("index", cv.getRecognition());
//            int[][] rgb = cv.getRGB();
//            double[] avg = new double[3];
//            for (int i = 4; i < 8; i++) {
//                avg[0] += rgb[i][0];
//                avg[1] += rgb[i][1];
//                avg[2] += rgb[i][2];
//            }
//            avg[0] /= 4;
//            avg[1] /= 4;
//            avg[2] /= 4;
//            telemetry.addData("red", avg[0]);
//            telemetry.addData("green", avg[1]);
//            telemetry.addData("blue", avg[2]);
//            telemetry.addData("x", cv.getX());
//            telemetry.update();
//        }
//    }
//}