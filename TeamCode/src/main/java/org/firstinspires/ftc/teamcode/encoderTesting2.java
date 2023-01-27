package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Encoder Test 2")

@Disabled

public class encoderTesting2 extends LinearOpMode {

    DcMotor motor;
    DcMotor liftTester;
    //double ticks = 537.7;
    //double newTarget;
    //double rotationsNeeded;

    //int lowPole = (int) (rotationsNeeded * ticks);
    //int finished;

    @Override

    public void runOpMode() throws InterruptedException {

        motor = hardwareMap.get(DcMotor.class, "lift");
        liftTester = hardwareMap.get(DcMotor.class, "2lift");
        telemetry.addData("Hardware: ", "Initialized");

        //motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //motor.setTargetPosition(0);

       // liftTester.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       // liftTester.setTargetPosition(0);

       // motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //liftTester.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        telemetry.addData("Current Position: ", motor.getCurrentPosition());

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            //telemetry.addData("Current Position: ", motor.getCurrentPosition());

            //motor.setPower(0);

            if (gamepad1.x) {

                //back to ground
                RaiseLift(.5,2, -1);
                Thread.sleep(1000);
                Thread.sleep(1000);
                liftTester.setPower(0);

                //motor.setTargetPosition(0);
                //motor.setPower(0.9);
                //motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                //to low pole

                //finished = motor.getCurrentPosition();
               // telemetry.addData("Location", finished);

            }



            if (gamepad1.a) {

                //lowPole
                RaiseLift(1,2, 1);
                //Thread.sleep(1000);
                liftTester.setPower(0);

                /*
                //encoder(1);
                rotationsNeeded = 20;
                int lowPole = (int) (rotationsNeeded * 537.7);

                motor.setTargetPosition(lowPole);
                motor.setPower(0.9);
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                //to low pole
                finished = motor.getCurrentPosition();

                while (motor.isBusy()) {
                    telemetry.addData("Location", finished);
                }
                */

            }

            if (gamepad1.b) {

                //middle pole
                RaiseLift(1,3, 1);
                Thread.sleep(1000);
                Thread.sleep(1000);
                liftTester.setPower(0);

                //motor.setTargetPosition(0);
                //motor.setPower(0.9);
                //motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                //to low pole

                //finished = motor.getCurrentPosition();
                // telemetry.addData("Location", finished);

            }

            if (gamepad1.y) {

                //top pole
                RaiseLift(1,4, 1);
                //Thread.sleep(1000);
                //Thread.sleep(1000);
                liftTester.setPower(0);

                //motor.setTargetPosition(0);
                //motor.setPower(0.9);
                //motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                //to low pole

                //finished = motor.getCurrentPosition();
                // telemetry.addData("Location", finished);

            }

            telemetry.addData("Current Position: ", motor.getCurrentPosition());
        }

    }

    public void RaiseLift(double power, double totalSeconds, int Direction) throws InterruptedException {

        //For driving forward or backward
        // declare variables for this method (power, totalSeconds (milliseconds) & Direction)
        //For forwards set direction = 1 (In method call)
        // For backwards set direction = -1 (In method call)
        //example: driveStraight(1, 5, 1) means drive straight at 100% power, for 5 seconds, in forward direction
        //example: driveStraight(1, 5, -1) means drive straight at 100% power, for 5 seconds, in backwards direction

        motor.setPower(power * Direction);

        Thread.sleep((long) totalSeconds);

    } //End DriveStraight Method
    


}

