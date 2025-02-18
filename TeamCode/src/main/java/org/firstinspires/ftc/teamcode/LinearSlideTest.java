package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class LinearSlideTest extends LinearOpMode {

    private DcMotor linearSlideMotor;
    private Mecanum mecanum;

    @Override
    public void runOpMode() throws InterruptedException {

        mecanum = new Mecanum(
                hardwareMap.get(BHI260IMU.class, "imu"),
                hardwareMap.get(DcMotor.class, "frontLeft"),
                hardwareMap.get(DcMotor.class, "frontRight"),
                hardwareMap.get(DcMotor.class, "backRight"),
                hardwareMap.get(DcMotor.class, "backLeft")
        );

        linearSlideMotor = hardwareMap.get(DcMotor.class, "linearSlide");

        waitForStart();

        while (opModeIsActive()) {
            // Control linear slide with left stick Y
            // Note: Negating the value because pushing up on the stick returns negative values
            linearSlideMotor.setPower(-gamepad1.left_stick_y);

            // Drive control remains the same
            // mecanum.drive(gamepad1.right_trigger - gamepad1.left_trigger, gamepad1.left_stick_x, gamepad1.right_stick_x);

            // Add telemetry for linear slide power
            telemetry.addData("Linear Slide Power", -gamepad1.left_stick_y);
            telemetry.update();
        }
    }
}