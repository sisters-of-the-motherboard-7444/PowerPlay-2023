package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Disabled

@TeleOp
public class LiftOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // position of the dr4b when it is on the **ground
        int liftGroundPosition = 25;

        // position of dr4b when it is **low
        int liftLowPosition = 100;

        // position of dr4b when it is **mid
        int liftMidPosition = 200;

        // position of dr4b when it is **high
        int liftHighPosition = 300;

        // find a motor in the hardware map named these
        //DcMotor rightLift = hardwareMap.dcMotor.get("rightLift");
        //DcMotor leftLift = hardwareMap.dcMotor.get("leftLift");
        DcMotor leftLift = hardwareMap.dcMotor.get("lift");

        //rightLift.setDirection(DcMotor.Direction.REVERSE);
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
        leftLift.setTargetPosition(liftGroundPosition);
        leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addData("Mode", "waiting");
        telemetry.update();

        float LeftTriggerPressed = gamepad2.left_trigger;
        float RightTriggerPressed = gamepad2.right_trigger;

        waitForStart();

        telemetry.addData("Mode", "running");
        telemetry.update();


        while (opModeIsActive()) {
            // if the x button is pressed, move to **ground
            if (gamepad2.x) {
                //rightLift.setPower(0.75);
                leftLift.setPower(1);
                //rightLift.setTargetPosition(liftGroundPosition);
                //rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftLift.setTargetPosition(liftGroundPosition);
                leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                //leftLift.setPower(0);
            }

            // if the y button is pressed, move to **high
            if (gamepad2.y) {
                //rightLift.setPower(0.75);
                leftLift.setPower(1);
                //rightLift.setTargetPosition(liftHighPosition);
                //rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftLift.setTargetPosition(liftHighPosition);
                leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftLift.setPower(0);
            }

            // if the b button is pressed, move to **mid
            if (gamepad2.b) {
                //rightLift.setPower(0.75);
                leftLift.setPower(1);
                //rightLift.setTargetPosition(liftMidPosition);
                //rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftLift.setTargetPosition(liftMidPosition);
                leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftLift.setPower(0);
            }

            // if the a button is pressed, move to **low
            if (gamepad2.a) {
                //rightLift.setPower(0.75);
                leftLift.setPower(1);
                //rightLift.setTargetPosition(liftLowPosition);
                //rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftLift.setTargetPosition(liftLowPosition);
                leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftLift.setPower(0);
            }

            // get the current position of the motor
            double position; //= rightLift.getCurrentPosition();
            position = leftLift.getCurrentPosition();

            // get the target position of the motor
            double desiredPosition;// = rightLift.getTargetPosition();
            desiredPosition = leftLift.getTargetPosition();

            // show the position of the motor on telemetry
            telemetry.addData("Encoder Current Position", position);

            // show the target position of the motor on telemetry
            telemetry.addData("Desired Position", desiredPosition);

            //telemetry.addData("velocity", leftLift.getVelocity());
            telemetry.addData("is at target", !leftLift.isBusy());

            telemetry.update();

            if(gamepad2.dpad_up) {
                leftLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                leftLift.setPower(1);
            }

            if(gamepad2.dpad_down) {
                leftLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                leftLift.setPower(-.25);
            }

        }
    }


}