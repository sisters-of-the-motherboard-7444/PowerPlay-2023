package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Encoder Test ")
@Disabled

public class encoderTesting extends OpMode {

    DcMotor motor;
    double ticks = 537.7;
    double newTarget;


    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, "lift");
        telemetry.addData("Hardware: ", "Initialized");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override

    public void loop() {

        motor.setPower(0);

        if(gamepad1.x){
                   groundLevel();
         }

        if(gamepad1.a){
            encoder(1);
            //to low pole
        }

        if(gamepad1.b){
            encoder(2);
            //to mid pole
        }

        if(gamepad1.y){
            //encoder(25);
            int highPole = 10000;
            motor.setTargetPosition(highPole);
            motor.setPower(0.9);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            telemetry.addData("Desired Position - high", highPole);

            //to high pole
        }

        if (gamepad1.right_bumper) {
            int higherPosition;
            higherPosition = motor.getCurrentPosition();
            motor.setTargetPosition(higherPosition + 100);
            motor.setPower(0.9);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            //telemetry.addData("Desired Position - high", highPole);
        }



        telemetry.addData("Current Position: ", motor.getCurrentPosition());

    }

    public void encoder(int turnage){
        //turnage is number of full rotations from current position
        //from ground level need
        // turnage =     to get to high pole
        //turnage =      to get to mid pole
        // turnage =     to get to low pole
        newTarget = ticks/turnage;
        motor.setTargetPosition((int)newTarget);
        motor.setPower(0.9);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void groundLevel(){
        motor.setTargetPosition(0);
        motor.setPower(0.8);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

}