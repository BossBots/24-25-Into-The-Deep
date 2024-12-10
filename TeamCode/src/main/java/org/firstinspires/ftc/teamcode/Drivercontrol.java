package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class Drivercontrol extends LinearOpMode {

    private DcMotor linearSlideMotor;
    private Mecanum mecanum;
    private Servo turningServo1;  // X-axis servo
    private Servo turningServo2;  // Y-axis servo

    // Constants for turningServo2 positions
    private final double SERVO2_X_POSITION = 0.4;  // Position when X is pressed
    private final double SERVO2_A_POSITION = 1;  // Position when A is pressed

    // Constants for linear slide
    private final double LINEAR_SLIDE_POWER = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize mecanum drive
        mecanum = new Mecanum(
                hardwareMap.get(BHI260IMU.class, "imu"),
                hardwareMap.get(DcMotor.class, "frontLeft"),
                hardwareMap.get(DcMotor.class, "frontRight"),
                hardwareMap.get(DcMotor.class, "backRight"),
                hardwareMap.get(DcMotor.class, "backLeft")
        );
        mecanum.constantPower();

        // Initialize linear slide motor
        linearSlideMotor = hardwareMap.get(DcMotor.class, "linearSlide");
        linearSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        linearSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Initialize servos
        turningServo1 = hardwareMap.get(Servo.class, "turningServo1");
        turningServo2 = hardwareMap.get(Servo.class, "turningServo2");

        // Set initial positions for servos
        turningServo1.setPosition(0.5);  // Center position
        turningServo2.setPosition(0.5);  // Center position

        waitForStart();

        while (opModeIsActive()) {
            // Gamepad 1 (Controller A) - Driving controls
            double drive = -gamepad1.left_stick_y;   // Forward/backward
            double strafe = gamepad1.left_stick_x;   // Left/right
            double rotate = gamepad1.right_stick_x;  // Rotation

            mecanum.drive(drive, strafe, rotate);

            // Gamepad 2 (Controller B) controls

            // Linear Slide Control (D-pad)
            if (gamepad2.dpad_up) {
                linearSlideMotor.setPower(LINEAR_SLIDE_POWER);
            } else if (gamepad2.dpad_down) {
                linearSlideMotor.setPower(-LINEAR_SLIDE_POWER);
            } else {
                linearSlideMotor.setPower(0);
            }

            // TurningServo1 Control (Left joystick)
            // Map the joystick Y value (-1 to 1) to servo position (0 to 1)
            double servo1Position = ((-gamepad2.left_stick_y + 1) / 2);
            turningServo1.setPosition(servo1Position);

            // TurningServo2 Control (X and A buttons)
            if (gamepad2.x) {
                turningServo2.setPosition(SERVO2_X_POSITION);
            } else if (gamepad2.a) {
                turningServo2.setPosition(SERVO2_A_POSITION);
            }

            // Telemetry
            telemetry.addData("Drive Power", drive);
            telemetry.addData("Strafe Power", strafe);
            telemetry.addData("Rotate Power", rotate);
            telemetry.addData("Linear Slide Power", linearSlideMotor.getPower());
            telemetry.addData("Turning Servo 1 Position", turningServo1.getPosition());
            telemetry.addData("Turning Servo 2 Position", turningServo2.getPosition());
            telemetry.update();
        }
    }
}