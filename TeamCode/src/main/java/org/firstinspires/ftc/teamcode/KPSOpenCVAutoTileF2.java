/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

/*
 *
 * This is an example LinearOpMode that shows how to use
 * a Modern Robotics Color Sensor.
 *
 * The op mode assumes that the color sensor
 * is configured with a name of "sensor_color".
 *
 * You can use the X button on gamepad1 to toggle the LED on and off.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */
@Autonomous(name = "KPS: TileF2Start", group = "Sensor")
//@Disabled

public class KPSOpenCVAutoTileF2 extends LinearOpMode {

  HardwarePowerPlay Wall_E = new HardwarePowerPlay();

  //ColorSensor colorSensor;    // Hardware Device Object

  private OpenCvCamera webcam;
  AprilTagDetectionPipeline aprilTagDetectionPipeline;

  static final double FEET_PER_METER = 3.28084;

  // Lens intrinsics
  // UNITS ARE PIXELS
  // NOTE: this calibration is for the C920 webcam at 800x448.
  // You will need to do your own calibration* for other configurations!
  double fx = 578.272;
  double fy = 578.272;
  double cx = 402.145;
  double cy = 221.506;

  // UNITS ARE METERS
  double tagsize = 0.166;

  int numFramesWithoutDetection = 0;

  final float DECIMATION_HIGH = 3;
  final float DECIMATION_LOW = 2;
  final float THRESHOLD_HIGH_DECIMATION_RANGE_METERS = 1.0f;
  final int THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION = 4;

  @Override
  public void runOpMode() throws InterruptedException {

    // get a reference to our ColorSensor object.
    //colorSensor = hardwareMap.get(ColorSensor.class, "sensor_color");

    //get a reference to our Webcam

    Wall_E.InitializeRobot(hardwareMap);

    int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
    webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
    aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

    webcam.setPipeline(aprilTagDetectionPipeline);
    webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
      @Override
      public void onOpened() {
        webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
      }

      @Override
      public void onError(int errorCode) {
        /*
         * This will be called if the camera could not be opened
         */
        telemetry.addLine(String.format("\nCamera had an error=%d", errorCode));
      }
    });

    telemetry.addLine("Waiting for start");
    telemetry.update();

    // wait for the start button to be pressed.
    waitForStart();

    telemetry.setMsTransmissionInterval(50);

    // while the op mode is active, loop and read the RGB data.
    // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
    while (opModeIsActive()) {
      // Calling getDetectionsUpdate() will only return an object if there was a new frame
      // processed since the last time we called it. Otherwise, it will return null. This
      // enables us to only run logic when there has been a new frame, as opposed to the
      // getLatestDetections() method which will always return an object.
      ArrayList<AprilTagDetection> detections = aprilTagDetectionPipeline.getDetectionsUpdate();

      // If there's been a new frame...

      int signalCone = 0;

      if (detections != null) {
        telemetry.addData("FPS", webcam.getFps());
        telemetry.addData("Overhead ms", webcam.getOverheadTimeMs());
        telemetry.addData("Pipeline ms", webcam.getPipelineTimeMs());
        telemetry.addData("Pipeline ms", webcam.getPipelineTimeMs());


        // If we don't see any tags
        if (detections.size() == 0) {
          numFramesWithoutDetection++;

          // If we haven't seen a tag for a few frames, lower the decimation
          // so we can hopefully pick one up if we're e.g. far back
          if (numFramesWithoutDetection >= THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION) {
            aprilTagDetectionPipeline.setDecimation(DECIMATION_LOW);
          }
        }
        // We do see tags!
        else {
          numFramesWithoutDetection = 0;

          // If the target is within 1 meter, turn on high decimation to
          // increase the frame rate
          if (detections.get(0).pose.z < THRESHOLD_HIGH_DECIMATION_RANGE_METERS) {
            aprilTagDetectionPipeline.setDecimation(DECIMATION_HIGH);
          }

          for (AprilTagDetection detection : detections) {
            // TODO: make info accessible outside of this class: int aprilTagId = detection.id;

            signalCone = 0; //Check
            signalCone = detection.id;//Check

            telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
            telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x * FEET_PER_METER));
            telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y * FEET_PER_METER));
            telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z * FEET_PER_METER));
            telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", Math.toDegrees(detection.pose.yaw)));
            telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", Math.toDegrees(detection.pose.pitch)));
            telemetry.addLine(String.format("Rotation Roll: %.2f degrees", Math.toDegrees(detection.pose.roll)));
          }
        }

        telemetry.update();
      }

      if (signalCone == 1) {

        //place cone in terminal
        Wall_E.StopMotion(1500);

        Wall_E.DriveSideways(.4, 800, -1); //Forward Motion
        Thread.sleep(1000);

        //move to Section 1
        Wall_E.DriveSideways(.4, 100, 1); //Forward Motion

        Wall_E.StopMotion(400);

        Wall_E.DriveStraight(.5, 500, 1);

        Wall_E.StopMotion(1000);

        //Rotate 90 degrees (requirement for autonomous
        Wall_E.CenterSpin(0.5, 250, 1);

        Wall_E.StopMotion(1500);

      }

      if (signalCone == 2) {

        //place cone in terminal
        Wall_E.DriveSideways(.6, 2000, -1); //Forward Motion
        Thread.sleep(1500);

        //move to Section 2
        Wall_E.DriveSideways(.6, 2000, 1);

        Wall_E.StopMotion(1500);

        //Rotate 90 degrees (requirement for autonomous
        Wall_E.CenterSpin(0.5, 500, 1);

        Wall_E.StopMotion(1500);

      }

      if (signalCone == 3) {

        //place cone in terminal
        //Wall_E.DriveSideways(.6, 2000, -1); //Forward Motion
        //Thread.sleep(1500);

        //move to Section 1
        //Wall_E.DriveSideways(.6, 2000, 1);

        //Wall_E.StopMotion(1500);

        //Rotate 90 degrees (requirement for autonomous
        Wall_E.CenterSpin(0.5, 2500, 1);

        //Wall_E.StopMotion(1500);

      }




      sleep(20);
    }
  }
}
