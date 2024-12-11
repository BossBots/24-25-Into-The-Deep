package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * autonomous program for advanced robotic movements and computer vision integration.
 * utilizes Mecanum drive, linear slide motor, servo-controlled claws, and computer vision.
 */
@Autonomous
public class AutonBlueRight extends LinearOpMode {

    //30 seconds
    //positive linear value = linear slide goes up

    // Declare robot components
    private DcMotor linearSlideMotor;
    private Mecanum mecanum;
    private Servo clawAngle;

    private double levelAngle = 0.53;
    private double depositAngle = 0.8;
    private Servo openClaw;
    private double releasePos = 0.1;
    private double storePix = 0.04;
    // Declare computer vision and recognition variable
    private int recognition;

    private int elementPositionRecognition = 1;

    @Override
    public void runOpMode() throws InterruptedException {

        //intialize imu ...
        IMU imu;
        imu = hardwareMap.get(IMU.class, "imu");


        // Initialize Mecanum drivetrain
        mecanum = new Mecanum(
                hardwareMap.get(BHI260IMU.class, "imu"),
                hardwareMap.get(DcMotor.class, "frontLeft"),
                hardwareMap.get(DcMotor.class, "frontRight"),
                hardwareMap.get(DcMotor.class, "backRight"),
                hardwareMap.get(DcMotor.class, "backLeft")
        );
        // mecanum.constantSpeed();
        mecanum.constantPower();

        // Initialize linear slide motor
        linearSlideMotor = hardwareMap.get(DcMotor.class, "linearSlide");
        linearSlideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        linearSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        linearSlideMotor.setPower(0); // linear slide should be near the ground starting

        // Initialize clawAngleservos
        clawAngle = hardwareMap.get(Servo.class, "angleServo");
        clawAngle.setPosition(levelAngle);

        openClaw = hardwareMap.get(Servo.class, "clawServo");
        openClaw.setPosition(releasePos);

        // Initialize computer vision
        ComputerVision cv = new ComputerVision(hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()), true);

        // Wait for the start button to be pressed
        waitForStart();
        //recognition = cv.getRecognition();

        // Close the claws
        //claw.setPosition(0.3);

        if (opModeIsActive()) {
            //robot is placed on side wall, right next to the center poles
            // team prop randomly placed on left, center, right, - right in front of robot
            // PURPLE pixel loaded onto front of robot scoop
            // YELLOW pixel loaded onto robot claw

            //computer vision initialization simulation for now
            //placement variables
            //pushes the purple loaded pixel next to whichever place has a team element, and then moves
            //the robot back to starting position
            clawAngle.setPosition(storePix);
            if (elementPositionRecognition == 2){ //left side
                mecanum.yaw(-0.1, 15);
                mecanum.forward(0.5, 0, 1300);
                openClaw.setPosition(releasePos);
                linearSlideMotor.setTargetPosition(300);

                mecanum.forward(-0.5, 0, 1300);
                mecanum.yaw(0.1, 15);
            }
            else if (elementPositionRecognition == 3){ //right side
                mecanum.yaw(0.1, 15);
                mecanum.forward(0.5, 0, 1300);
                openClaw.setPosition(releasePos);
                linearSlideMotor.setTargetPosition(300);

                mecanum.forward(-0.5, 0, 1300);
                mecanum.yaw(-0.1, 15);
            }
            else{                                  //center/default
                mecanum.forward(0.5, 0, 1370); //move to cv spot
                openClaw.setPosition(releasePos);
                linearSlideMotor.setTargetPosition(300);
                mecanum.forward(-0.5, 0, 1300); //move back to original
            }

            // rest of auton
//            mecanum.drift(-0.5, 90, 3900);
//            mecanum.forward(0.5, 0, 1300);
//            mecanum.yaw(-0.5, 90);
//            mecanum.forward(0.3, 0, 1000);
//
//            //lift clawAngleand open
//            linearSlideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            linearSlideMotor.setTargetPosition(1500);
//            clawAngle.setPosition(depositAngle);
//            openClaw.setPosition(releasePos); //
//            while (linearSlideMotor.isBusy()){
//                idle();
//            }
//            linearSlideMotor.setPower(0);
//            linearSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//            //linear slide goes down
//
//            //go to parking
//
//            linearSlideMotor.setTargetPosition(-1500);
//            mecanum.drift(-0.5, 90, 100);
//            mecanum.forward(0.5, 0, 100);
//



            // moves robot to loading place for pixel
//            mecanum.forward(-0.1, 0, 200);
//            claw.setPosition(collectPos);
//            linearSlideMotor.setTargetPosition(-100);
//            linearSlideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            linearSlideMotor.setPower(-0.2);

            // move right a little
            //mecanum.drive(0, 90, 500);

            // go straight a lot
            //mecanum.forward(0.1, 0, 1500);

            // go left a little
            //mecanum.drive(0, -90, 500);

            // use prop as an indicator and drop yellow pixel onto backdrop
            // assuming we have a method to drop the yellow pixel, call it here
            // dropPixel() ?

            // go right a little
            //mecanum.drive(0, 90, 200);

            // go backward a lot
            //mecanum.forward(-0.2, 180, 200);

            // go left a little
            //mecanum.drive(0, -90, 500);

            // pick up purple pixel
            //linearSlideMotor.setTargetPosition(-3500);

            // pick up yellow pixel
            // assuming we have a method to pick up the yellow pixel, call it here
            // pickUpPixel() ?

            // drop purple pixel on the line
            //claw.setPosition(releasePos);

            // go right a little
            //mecanum.drive(0, 90, 500);

            // go straight a lot
            //mecanum.forward(0.2, 0, 1500);

            // go left a little
            //mecanum.drive(0, -90, 500);

            // match and drop yellow pixel on the backdrop
            // assuming you have a method to drop the yellow pixel, call it here
            // dropPixel() ?

            // park (backstage)
            // assuming we have a method to park, call it here
        }
    }
}

// change the code to implement the changes you mentioned professionally.

/**
 * Steps
 *
 * 1. put purple pixel next to prop
 * 2. take yellow pixel to backdrop (use prop as indicator)
 * 3. come back
 * 4. pick up purple pixel, then yellow pixel
 * 5. drop purple pixel on line
 * 6. drop yellow pixel on backdrop (matched)
 * 7. park (backstage)
 */

/**
 * Cheatsheet
 *
 * mecanum.constantSpeed() - this will make the robot move at a constant speed
 * mecanum.constantPower() - this will make the robot move at a constant power
 * mecanum.getHeading() - this will return the current heading of the robot
 * mecanum.forward(double power, double angle, long interval) - this will make the robot move forward
 * mecanum.yaw(double power, double angle) - this will make the robot turn
 * mecanum.reset() - this will reset the wheel angles
 * mecanum.drift(double power, double angle, long interval) - this will make the robot drift
 * mecanum.brake() - this will make the robot brake
 * mecanum.drive(double forward, double yaw, double drift) - this will make the robot drive
 */