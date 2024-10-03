package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class ServoTest extends LinearOpMode {
    private Servo clawAngle;
    private double releasePos = 0.4;
    private double storePix = 0.35;
    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize clawAngleservos
        clawAngle= hardwareMap.get(Servo.class, "angleClaw");

        // Wait for the start button to be pressed
        waitForStart();

        while (opModeIsActive()) {
            // Open the claw
            clawAngle.setPosition(storePix);
            sleep(1000); // Wait for 1 second

            // Close the claw
            clawAngle.setPosition(releasePos);
            sleep(1000); // Wait for 1 second
        }
    }
}
