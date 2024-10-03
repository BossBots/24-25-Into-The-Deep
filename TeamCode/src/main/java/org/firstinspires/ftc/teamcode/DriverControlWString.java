package org.firstinspires.ftc.teamcode;


import com.qualcomm.hardware.bosch.BHI260IMU;
// not usedimport com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp
public class DriverControlWString extends LinearOpMode {

    private DcMotor linearSlideMotor;

    private DcMotor leftString;

    private DcMotor rightString;
    private Mecanum mecanum;
    private Servo clawAngle;

    private Servo openClaw;

    private Servo launcher;
    //private Servo joint;
//    private boolean oldState = false;
    private boolean currentState;
    private boolean droneState;
    //    private boolean clawAction = false;
    private boolean oldState = false;
    private boolean newState;
    private boolean precisionMode = false;

    private double abortAngle = 0.60;
    private double levelAngle = 0.53;

    //private double storePix = 0.25;
    @Override
    public void runOpMode() throws InterruptedException {

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

        leftString = hardwareMap.get(DcMotor.class, "leftString");
        rightString = hardwareMap.get(DcMotor.class, "rightString");

        clawAngle = hardwareMap.get(Servo.class, "angleServo");
        clawAngle.setPosition(levelAngle);

        openClaw = hardwareMap.get(Servo.class, "clawServo");
        openClaw.setPosition(0.875);

        launcher = hardwareMap.get(Servo.class, "launcher");

        waitForStart();

        while (opModeIsActive()) {
            //linear  slide controls, moves linear slide along with the two motors (normal)
            if (Math.abs(gamepad2.right_stick_y)<0.1) {
                linearSlideMotor.setPower(0);
                linearSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                leftString.setPower(0);
                leftString.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                rightString.setPower(0);
                rightString.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            } else {
                linearSlideMotor.setPower(0.25 * 0.75*gamepad2.right_stick_y);
                leftString.setPower(0.25*0.75*gamepad2.right_stick_y);
                rightString.setPower(-0.25*0.75*gamepad2.right_stick_y);
            }

            //for endgame, press x on gamepad 2 to disable the linear slide, and keep the two motors working
            currentState = gamepad2.x;
            if(currentState){
                linearSlideMotor.setPower(0);
                linearSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                if (Math.abs(gamepad2.right_stick_y)<0.1) {
                    linearSlideMotor.setPower(0);
                    linearSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    leftString.setPower(0);
                    leftString.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    rightString.setPower(0);
                    rightString.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                } else {
                    linearSlideMotor.setPower(0);
                    linearSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    leftString.setPower(0.25*0.75*gamepad2.right_stick_y);
                    rightString.setPower(-0.25*0.75*gamepad2.right_stick_y);
                }
            }
            //toggles which motors are disabled.
            //  If the linear slide is previously disabled and the string motors are previously enabled, this will enable the linear slide
            //      and disable the two string motors.

            //  If the linear slide is previously enabled and the string motors are previously disabled, this will disable the linear slide
            //      and enable the two string motors.
            currentState = gamepad2.y;
            if (currentState){
                if((leftString.getPower() == 0.0) && (rightString.getPower() == 0.0)){
                    if (Math.abs(gamepad2.right_stick_y)<0.1) {
                        leftString.setPower(0.25*0.75*gamepad2.right_stick_y);
                        rightString.setPower(-0.25*0.75*gamepad2.right_stick_y);

                    }
                }
                else{
                    leftString.setPower(0);
                    leftString.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    rightString.setPower(0);
                    rightString.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }
            }

            //claw 0.25 is level, 0.25 0 is dropping off pixel, 0.25> is facing down
            currentState= gamepad2.a;
            if(currentState) {
                clawAngle.setPosition(0.35);
            }

            currentState = gamepad2.dpad_down;
            if(currentState) {
                if(openClaw.getPosition() != 0.808)
                {openClaw.setPosition(0.808);}
            }
            currentState = gamepad2.dpad_up;
            if(currentState){
                if(openClaw.getPosition() != 0.875)
                {openClaw.setPosition(0.875);}
            }


            droneState = gamepad2.b;
            if(droneState){
                launcher.setPosition(0.5);
            }

            double currentAngle;
//            while (Math.abs(gamepad2.right_stick_x)>0.1) {
//                clawPosition = claw.getPosition();
//                claw.setPosition(clawPosition-0.001);
//                claw.wait(10);
//            }

            while((gamepad2.left_stick_y)>0.1){
                currentAngle = clawAngle.getPosition();
                clawAngle.setPosition(currentAngle + 0.00025);
            }
            while((gamepad2.left_stick_y)<-0.1){
                currentAngle = clawAngle.getPosition();
                if (currentAngle <= abortAngle){
                    clawAngle.setPosition(currentAngle - 0.00025); //negative equals turning counterclockwise for now
                }

            }

//            currentState= gamepad2.b;
            //not used right now

//            if(currentState){
//                joint.setPosition(0.1); //twist left hypothetically
//            }
//            else{
//                joint.setPosition(0.3); //twist right hypothetically
//            }

//            (!oldState && currentState){
//                clawAction = !clawAction;
//            }
//            oldState= currentState;
//            if(clawAction){
//                claw.setPosition(0.4); // closed
//            } else {
//                claw.setPosition(0.1); // open
//            } */
            //driving

            newState = gamepad1.a;
            if (newState && !oldState) {
                precisionMode = !precisionMode;
            }
            oldState = newState;

            if (gamepad1.x) {
                mecanum.brake(10);
            } else if (precisionMode) {
                mecanum.drive( 0.5 * (gamepad1.right_trigger - gamepad1.left_trigger),
                        0.5 * (gamepad1.left_stick_x), 0.5 * (gamepad1.right_stick_x));
            } else {
                mecanum.drive(gamepad1.right_trigger - gamepad1.left_trigger,
                        gamepad1.left_stick_x, gamepad1.right_stick_x);
            }

            telemetry.addData("precision mode", precisionMode);
            telemetry.addData("current angle", clawAngle.getPosition());
            telemetry.update();
        }
    }
}
