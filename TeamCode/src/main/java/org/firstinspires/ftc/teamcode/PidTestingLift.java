package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
//@Disabled

public class PidTestingLift extends LinearOpMode {

    public DcMotor motorFrontRight = null;
    public DcMotor motorFrontLeft = null;
    public DcMotor motorBackRight = null;
    public DcMotor motorBackLeft = null;

    public Servo claw;

    //the reason we use DcMotor Ex instead of DcMotor is for extra speed
    public DcMotorEx lift;

    //here we establish some variables
    double liftreset = 0;
    double intergalSum =0;
    //this three are the ones that you will tune to improve the PID
    double kp =.01;
    double ki =0;
    double kd =0;
    double reach;

    ElapsedTime timer = new ElapsedTime();
    private double lastError =0;

    @Override
    public void runOpMode() throws InterruptedException {
        motorFrontRight = hardwareMap.get(DcMotor.class, "motorFrontRight");
        motorFrontLeft = hardwareMap.get(DcMotor.class, "motorFrontLeft");
        motorBackRight = hardwareMap.get(DcMotor.class, "motorBackRight");
        motorBackLeft = hardwareMap.get(DcMotor.class, "motorBackLeft");
        lift = hardwareMap.get(DcMotorEx.class, "lift");

        claw=hardwareMap.servo.get("claw");

//so even though encoders are necessary for the lift to go to correct positions, using run with encoders slows down the motors by 20%

        // lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorBackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setDirection(DcMotorSimple.Direction.FORWARD);

        motorBackLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        motorBackRight.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFrontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        motorFrontRight.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

       // telemetry.addData("lift", lift.getCurrentPosition() - liftreset);
        //telemetry.update();

        while (opModeIsActive()) {

            double ForwardLeft = gamepad1.left_stick_y;
            double TurnRight = gamepad1.right_stick_x;
            double StrafeLeft = gamepad1.left_stick_x;

            double positionPid;
            positionPid = lift.getCurrentPosition() - liftreset;
            telemetry.addData("lift", positionPid);

            telemetry.update();

            motorFrontLeft.setPower(+ForwardLeft - StrafeLeft - TurnRight);
            motorFrontRight.setPower(+ForwardLeft + StrafeLeft + TurnRight);
            motorBackLeft.setPower(+ForwardLeft + StrafeLeft - TurnRight);
            motorBackRight.setPower(+ForwardLeft - StrafeLeft + TurnRight);

//ground position
            if (gamepad1.x) {
                //double power = pid_tick(0,lift.getCurrentPosition());
                //lift.setPower(power);
                groundLevel(0.35,5,-1);
//high position
            } else if(gamepad1.y) {
                double power = pid_tick(300,lift.getCurrentPosition());
               lift.setPower(power);

//mid position
            } else if(gamepad1.b) {
                double power = pid_tick(55000,lift.getCurrentPosition());
                lift.setPower(power);
//low position
            } else if(gamepad1.a) {
                double power = pid_tick(30000,lift.getCurrentPosition());
                lift.setPower(power);

            }

               // lift.setPower(0);
//only include this line if you want slides to stay up when button is pressed
                else {
                    lift.setPower(0);
                    }

            if(gamepad1.right_bumper){
                //open
                claw.setPosition(0);
                //claw.setPower(0.2);
               // Thread.sleep(500);
               // claw.setPower(0);
            } else if(gamepad1.left_bumper){
                //close
                claw.setPosition(0.8);
                //claw.setPower(-0.2);
                //Thread.sleep(500);
               // claw.setPower(0);
            } else {
                //claw.setPower(0);
            }

            if(gamepad1.dpad_down){
                //stop
                //claw.setPower(0);
            }



        }
    }

    public double pid_tick(double reference, double state) {
        double error = reference - state;
        intergalSum += error * timer.seconds();
        double derivative = (error-lastError) / timer.seconds();
        lastError = error;
        timer.reset();

        double output = (error * kp) + (derivative * kd) + (intergalSum * ki);
        return output;
    }

    public void groundLevel(double power, long totalSeconds, int Direction) throws InterruptedException{
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



