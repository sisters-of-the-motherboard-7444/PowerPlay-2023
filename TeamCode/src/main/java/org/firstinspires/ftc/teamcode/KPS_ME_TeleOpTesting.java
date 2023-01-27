package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
//@Disabled

public class KPS_ME_TeleOpTesting extends LinearOpMode {

    @Override

    public void runOpMode() throws InterruptedException {

        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        DcMotor motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        DcMotor motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        DcMotor motorBackRight = hardwareMap.dcMotor.get("motorBackRight");
        DcMotor lift = hardwareMap.dcMotor.get("lift");



        // Reverse the right side motors for goBilda
        // Reverse left motors if you are using NeveRests
        motorFrontRight.setDirection(DcMotorSimple.Direction.REVERSE); //Competition Bot
        motorBackRight.setDirection(DcMotorSimple.Direction.REVERSE);  //Competition Bot
        //motorBackLeft.setDirection(DcMotorSimple.Direction.REVERSE);  //Practice Bot
        //motorFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);  //Practice Bot

        //Lift Motor
        lift.setDirection(DcMotorSimple.Direction.FORWARD);
        lift.setPower(0);

        // reset the motor encoder so that it reads zero ticks
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Added to tell motor to use encoder position when buttons pressed
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // position of the dr4b when it is on the **ground
        int liftGroundPosition = 50;

        // position of dr4b when it is **low
        int liftLowPosition =75;

        // position of dr4b when it is **mid
        int liftMidPosition = 125;

        // position of dr4b when it is **high
        int liftHighPosition = 175;

        // sets the starting position of the arm to the down position
        lift.setTargetPosition(liftGroundPosition);
        //lift.setPower(0);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

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

            // Remember, this is reversed! (original code had -gamepad1 on double y= took out in 22-23
            double y = gamepad1.left_stick_y;
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

            // if the x button is pressed, move to **ground
            if (gamepad1.x) {
                lift.setTargetPosition(liftGroundPosition);
                lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift.setPower(.5);
            }

            // if the y button is pressed, move to **high
            if (gamepad1.y) {
                lift.getCurrentPosition();
                lift.setTargetPosition(liftHighPosition);
                lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift.setPower(.5);
            }
            //else {
            //   rightLift.setPower(0);
            //   leftLift.setPower(0);
            // }

            // if the b button is pressed, move to **mid
            if (gamepad1.b) {
                lift.setTargetPosition(liftMidPosition);
                lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift.setPower(.5);
            }

            // if the a button is pressed, move to **low
            if (gamepad1.a) {
                lift.setTargetPosition(liftLowPosition);
                lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                //lift.setPower(0.5);
                lift.setPower(0);
            }

            // get the current position of the motor
            double position = lift.getCurrentPosition();

            // get the target position of the motor
            double desiredPosition = lift.getTargetPosition();

            // show the position of the motor on telemetry
            telemetry.addData("Encoder Position", position);

            // show the target position of the motor on telemetry
            telemetry.addData("Desired Position", desiredPosition);

            telemetry.update();

            //manually move lift up
            if (gamepad1.left_bumper) {
                lift.setPower(-.25);
            }

            //manually move lift down
            if (gamepad1.right_bumper) {
                lift.setPower(.25);
            }



        } //end of active op mode


    } // end of run op mode
}