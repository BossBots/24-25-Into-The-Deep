package org.firstinspires.ftc.teamcode;


import com.qualcomm.hardware.bosch.BHI260IMU;
// not usedimport com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class Drivercontrol extends LinearOpMode {

    private DcMotor linearSlideMotor;
    private Mecanum mecanum;
    private Servo clawAngle;
    private Servo openClaw;
    private Servo launcher;
    private boolean currentState;
    private boolean droneState;
    private boolean oldState = false;
    private boolean newState;
    private boolean precisionMode = false;

    private double abortAngle = 0.75;
    private double levelAngle = 0.7272;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize hardware (keep your existing initialization code here)
        mecanum = new Mecanum(
                hardwareMap.get(BHI260IMU.class, "imu"),
                hardwareMap.get(DcMotor.class, "frontLeft"),
                hardwareMap.get(DcMotor.class, "frontRight"),
                hardwareMap.get(DcMotor.class, "backRight"),
                hardwareMap.get(DcMotor.class, "backLeft")
        );
        mecanum.constantPower();
        linearSlideMotor = hardwareMap.get(DcMotor.class, "linearSlide");
        linearSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        clawAngle = hardwareMap.get(Servo.class, "angleServo");
        clawAngle.setPosition(levelAngle); // 0 is open

        openClaw = hardwareMap.get(Servo.class, "clawServo");
        openClaw.setPosition(0.04);

        launcher = hardwareMap.get(Servo.class, "launcher");
        launcher.setPosition(0.4);

        waitForStart();

        while (opModeIsActive()) {
            // Linear slide controls
            if (Math.abs(gamepad2.right_stick_y) < 0.1) {
                linearSlideMotor.setPower(0);
                linearSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            } else {
                linearSlideMotor.setPower(0.25 * 0.75 * gamepad2.right_stick_y);
            }

            // Claw open/close controls
            if (gamepad2.dpad_down) {
                if (openClaw.getPosition() != 0.1) {
                    openClaw.setPosition(0.1);
                }
            }
            if (gamepad2.dpad_up) {
                if (openClaw.getPosition() != 0.04) {
                    openClaw.setPosition(0.04);
                }
            }

            // Drone launcher control
            if (gamepad2.b) {
                launcher.setPosition(0.95);
            }

            // Claw angle controls
            double currentAngle;
            if (gamepad2.left_stick_y > 0.1) {
                currentAngle = clawAngle.getPosition();
                clawAngle.setPosition(currentAngle + 0.00025);
            }
            if (gamepad2.left_stick_y < -0.1) {
                currentAngle = clawAngle.getPosition();
                if (currentAngle > abortAngle) {
                    clawAngle.setPosition(levelAngle);
                } else if (currentAngle <= abortAngle) {
                    clawAngle.setPosition(currentAngle - 0.00025);
                }
            }

            // Precision mode toggle
            newState = gamepad1.a;
            if (newState && !oldState) {
                precisionMode = !precisionMode;
            }
            oldState = newState;

            // Updated driving controls
            double drive = -gamepad1.left_stick_y; // Forward/backward
            double strafe = gamepad1.left_stick_x; // Left/right movement
            double rotate = 0;

            // Rotation controls
            if (gamepad1.right_trigger > 0) {
                rotate = gamepad1.right_trigger; // Clockwise rotation
            } else if (gamepad1.left_trigger > 0) {
                rotate = -gamepad1.left_trigger; // Counter-clockwise rotation
            }

            // Strafe controls
            if (gamepad1.right_bumper) {
                strafe = 1.0; // Strafe right
            } else if (gamepad1.left_bumper) {
                strafe = -1.0; // Strafe left
            }

            // Apply precision mode if active
            if (precisionMode) {
                drive *= 0.5;
                strafe *= 0.5;
                rotate *= 0.5;
            }

            // Drive the robot
            if (gamepad1.x) {
                mecanum.brake(10);
            } else {
                mecanum.drive(drive, strafe, rotate);
            }

            // Telemetry
            telemetry.addData("precision mode", precisionMode);
            telemetry.addData("current angle", clawAngle.getPosition());
            telemetry.addData("current angle of open close", openClaw.getPosition());
            telemetry.addData("current position of the launcher servo", launcher.getPosition());
            telemetry.addData("Drive", drive);
            telemetry.addData("Strafe", strafe);
            telemetry.addData("Rotate", rotate);
            telemetry.update();
        }
    }
}
