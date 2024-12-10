package org.firstinspires.ftc.teamcode;
//closer to board

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
public class AutonBlueLeft extends LinearOpMode {

    private Mecanum mecanum;

    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize Mecanum drivetrain
        mecanum = new Mecanum(
                hardwareMap.get(BHI260IMU.class, "imu"),
                hardwareMap.get(DcMotor.class, "frontLeft"),
                hardwareMap.get(DcMotor.class, "frontRight"),
                hardwareMap.get(DcMotor.class, "backRight"),
                hardwareMap.get(DcMotor.class, "backLeft")
        );
        mecanum.constantPower();

        // Wait for the start button to be pressed
        waitForStart();

        if (opModeIsActive()) {
            // Move forward for about an inch
            mecanum.forward(0.3, 0, 1000); // Adjust the time (100ms) as needed for approximately 1 inch

            sleep(1000); // Pause for half a second

            // Move backward for about an inch
            mecanum.forward(-0.3, 0, 1000); // Negative power for backward movement

            sleep(1000); // Pause for half a second

            // Spin clockwise once
            mecanum.yaw(0.3, 360); // 360 degrees for a full rotation
            // Stop the robot
            mecanum.brake(10);
        }
    }
}



/* {




    // 30 seconds
    // positive linear value = linear slide goes up

    // Declare robot components
    private DcMotor linearSlideMotor;
    private Mecanum mecanum;
    private Servo clawAngle;
    private Servo openClaw;

    private double levelAngle = 0.53;
    private double depositAngle = 0.8;

    private double releasePos = 0.875;
    private double storePix = 0.808;

    // Declare computer vision and recognition variable
    private int recognition;

    private int elementPositionRecognition = 1;


    @Override
    public void runOpMode() throws InterruptedException {

        //intialize imu
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
        linearSlideMotor.setPower(0); // linear slide should be near the ground starting
        linearSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        linearSlideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        linearSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


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
        // claw.setPosition(0.3);

        if (opModeIsActive()) {
            linearSlideMotor.setPower(0.1);

            clawAngle.setPosition(storePix); // closed
            if (elementPositionRecognition == 1) { // middle
                mecanum.forward(0.5, 0, 1300); // move to cv spot
                openClaw.setPosition(releasePos); // release
                linearSlideMotor.setTargetPosition(300);
                mecanum.forward(-0.5, 0, 1300); // move back to original spot
            }
            else if (elementPositionRecognition == 2) { // left
                mecanum.yaw(-0.1, 15);
                mecanum.forward(0.5, 0, 1300);
                openClaw.setPosition(releasePos);
                linearSlideMotor.setTargetPosition(300);

                mecanum.forward(-0.5, 0, 1300);
                mecanum.yaw(0.1, 15);
            }
            else if (elementPositionRecognition == 3) { // right
                mecanum.yaw(0.1, 15);
                mecanum.forward(0.5, 0, 1300);

                openClaw.setPosition(releasePos);

                linearSlideMotor.setTargetPosition(300);

                mecanum.forward(-0.5, 0, 1300);
                mecanum.yaw(-0.1, 15);
            }
            clawAngle.setPosition(storePix);
        }
    }
}
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