package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@TeleOp
//@Disabled

public class PowerPlayTeleOp_Thursday extends LinearOpMode {

    public Servo claw;

    //the reason we use DcMotor Ex instead of DcMotor is for extra speed
    public DcMotorEx lift;

    //here we establish some variables
    double liftreset = 0;
    double intergalSum = 0;
    //this three are the ones that you will tune to improve the PID
    double kp = 1;//edit to increase to be faster (original .01)
    double ki = 0;
    double kd = 0;
    double reach;

    ElapsedTime timer = new ElapsedTime();
    private double lastError = 0;

    @Override



    public void runOpMode() throws InterruptedException {

        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        DcMotor motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        DcMotor motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        DcMotor motorBackRight = hardwareMap.dcMotor.get("motorBackRight");

        Servo claw=hardwareMap.servo.get("claw");
        lift = hardwareMap.get(DcMotorEx.class, "lift");

        // Reverse the right side motors
        // Reverse left motors if you are using NeveRests
        motorFrontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        motorBackRight.setDirection(DcMotorSimple.Direction.FORWARD);
        motorBackLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // so even though encoders are necessary for the lift to go to correct positions, using run with encoders slows down the motors by 20%
        // lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorBackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setDirection(DcMotorSimple.Direction.FORWARD);

        // Retrieve the IMU from the hardware map
        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        // Technically this is the default, however specifying it is clearer
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        // Without this, data retrieving from the IMU throws an exception
        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            double positionPid;
            positionPid = lift.getCurrentPosition() - liftreset;
            telemetry.addData("lift", positionPid);

            telemetry.update();



            double y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;

            // Read inverse IMU heading, as the IMU heading is CW positive
            double botHeading = -imu.getAngularOrientation().firstAngle;

            double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
            double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio, but only when
            // at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            motorFrontLeft.setPower(frontLeftPower);
            motorBackLeft.setPower(backLeftPower);
            motorFrontRight.setPower(frontRightPower);
            motorBackRight.setPower(backRightPower);

            //ground position
            if (gamepad2.x) {
                //double power = pid_tick(0,lift.getCurrentPosition());
                //lift.setPower(power);
                groundLevel(0.35, 5, -1);
                //high position
            } else if (gamepad2.y) {
                double power = pid_tick(300, lift.getCurrentPosition());
                lift.setPower(power);

                //mid position
            } else if (gamepad2.b) {
                double power = pid_tick(55000, lift.getCurrentPosition());
                lift.setPower(power);
                //low position
            } else if (gamepad2.a) {
                double power = pid_tick(30000, lift.getCurrentPosition());
                lift.setPower(power);
            }

            // lift.setPower(0);
//only include this line if you want slides to stay up when button is pressed
        //    else {
        //        lift.setPower(0);
         //   }

            if (gamepad2.right_bumper) {
                //open
                claw.setPosition(0.4);
                telemetry.addData("Servo Position", claw.getPosition());

                //claw.se`tPower(0.2);
                // Thread.sleep(500);
                // claw.setPower(0);
            } else if (gamepad2.left_bumper) {
                //close
                claw.setPosition(1);
                telemetry.addData("Servo Position", claw.getPosition());
                //claw.setPower(-0.2);
                //Thread.sleep(500);
                // claw.setPower(0);
            } else {
                //claw.setPower(0);
            }

            telemetry.update();
            telemetry.addData("Servo Position", claw.getPosition());

            if (gamepad1.dpad_down) {
                //stop
                //claw.setPower(0);
            }
        }
    }

    public double pid_tick(double reference, double state) {
        double error = reference - state;
        intergalSum += error * timer.seconds();
        double derivative = (error - lastError) / timer.seconds();
        lastError = error;
        timer.reset();

        double output = (error * kp) + (derivative * kd) + (intergalSum * ki);
        return output;
    }

    public void groundLevel(double power, long totalSeconds, int Direction) throws InterruptedException {
        //lift.setTargetPosition(0);
        lift.setPower(power * Direction);
        //lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
/*
    public void raiseSlide (double power, double inches, int direction) {
        //Double variable that states how far lift moves for one motor rotation
        double inchesPerRotation = 4.2;
        //constant expressing the ticks in a GoBilda motor rotation
        double oneRotationTicks = 537.3;
        //Computation for desired location in terms of ticks
        double totalMovement = (oneRotationTicks/inchesPerRotation)*inches;
        //Set motor to use encoders
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //Sets target position of motor to the saved tick count
        lift.setTargetPosition((int)totalMovement);
        //sets the power of the motor
        lift.setPower(power * direction);
        //Moves motor so that lift goes to desire position
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
*/

}