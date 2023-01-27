package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Disabled

@TeleOp

public class KPSLiftOpModeTEST extends LinearOpMode {

    double ticks = 2786.2;
    double newTarget;
    private DcMotor leftLift;


    @Override

    public void runOpMode() throws InterruptedException {

        DcMotor leftLift = hardwareMap.dcMotor.get("lift");
        leftLift.setDirection(DcMotor.Direction.FORWARD);

        // reset the motor encoder so that it reads zero ticks
        // rightLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Added to tell motor to use encoder position when buttons pressed
        leftLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //rightLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // sets the starting position of the arm to the down position
        //rightLift.setTargetPosition(liftGroundPosition);
        //rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addData("Mode", "waiting");
        telemetry.update();

        waitForStart();

        telemetry.addData("Mode", "running");
        telemetry.update();


        while (opModeIsActive()) {

            if (gamepad1.a) {
                encoder(2);
            }

            telemetry.addData("Motor Ticks: ", leftLift.getCurrentPosition());

            if (gamepad1.b) {
                tracker();
            }

        }
    }
    
        public void encoder ( int turnage){
            newTarget = ticks / turnage;
            leftLift.setTargetPosition((int) newTarget);
            leftLift.setPower(0.3);
            leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }//end of encoder method

        public void tracker () {
            leftLift.setTargetPosition(0);
            leftLift.setPower(0.8);
            leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        } //end of tracker method
    }