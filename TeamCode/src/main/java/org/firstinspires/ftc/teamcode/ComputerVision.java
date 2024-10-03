package org.firstinspires.ftc.teamcode;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Core;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

public class ComputerVision {

    private OpenCvCamera phoneCam;
    private final int fractions = 12;
    private final double[][] topLeft = new double[fractions][2];
    private final double[][] botRight = new double[fractions][2];
    private int[][] avgRGB = new int[fractions][3];
    private int[] RED;
    private int[] GREEN;
    private int[] BLUE;
    private int x;

    private int[] longestSeq = new int[2];

    public ComputerVision(int camId, boolean isBlue) {
        if (isBlue) {
            RED = new int[]{0, 50};
            GREEN = new int[]{100, 240};
            BLUE = new int[]{125, 255};
        } else {
            RED = new int[]{180, 255};
            GREEN = new int[]{100, 180};
            BLUE = new int[]{180, 255};
        }
        for (int i = 0; i < fractions; i++) {
            topLeft[i][0] = 0d / 3d;
            topLeft[i][1] = ((double) i) / ((double) fractions);
            botRight[i][0] = 1d / 3d;
            botRight[i][1] = ((double) (i + 1)) / ((double) fractions);
        }
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, camId);
        phoneCam.setPipeline(new ComputerVision.Pipeline());
        phoneCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                phoneCam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }
            @Override
            public void onError(int errorCode) {
            }
        });
    }

    public int getX() {return x;}

    public int[] getAnalysis() {
        x += 1;
        longestSeq = new int[2];
        boolean[] seq = new boolean[fractions];
        for (int i = 0; i < fractions; i++) {
            seq[i] = (
                    (RED[0] < avgRGB[i][0] && avgRGB[i][0] < RED[1]) &&
                            (GREEN[0] < avgRGB[i][1] && avgRGB[i][1] < GREEN[1]) &&
                            (BLUE[0] < avgRGB[i][2] && avgRGB[i][2] < BLUE[1])
            );
        }
        int[] currentSeq = new int[2];
        boolean prevTrue = false;
        for (int i = 0; i < fractions; i++) {
            if (seq[i]) {
                if (prevTrue) {
                    currentSeq[1] = i;
                } else {
                    currentSeq[0] = i;
                    currentSeq[1] = i; // Handle single-length sequences
                }
                prevTrue = true;
            } else {
                if (prevTrue) {
                    prevTrue = false;
                    if (currentSeq[1] - currentSeq[0] > longestSeq[1] - longestSeq[0]) {
                        longestSeq[0] = currentSeq[0];
                        longestSeq[1] = currentSeq[1];
                    }
                    currentSeq = new int[2]; // Reset currentSeq after processing a sequence
                }
            }
        }
        if (prevTrue) {
            if (currentSeq[1] - currentSeq[0] > longestSeq[1] - longestSeq[0]) {
                longestSeq[0] = currentSeq[0];
                longestSeq[1] = currentSeq[1];
            }
        }
        return longestSeq;
    }

    public int getRecognition() {
        return (int) (Math.round((double) (longestSeq[1] + longestSeq[0]) / 2.)) / (fractions / 3);
    }

    public int[][] getRGB() {return avgRGB;}

    class Pipeline extends OpenCvPipeline {
        private boolean viewportPaused = false;
        private Mat YCrCb = new Mat();
        private Mat Cb = new Mat();
        private Mat[] regions = new Mat[fractions];

        private void inputToCb(Mat input) {
            Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
            Core.extractChannel(YCrCb, Cb, 2);
        }

        @Override
        public void init(Mat firstFrame) {
            inputToCb(firstFrame);
            for (int i = 0; i < fractions; i++) {
                regions[i] = firstFrame.submat(new Rect(
                        new Point(firstFrame.cols() * topLeft[i][0], firstFrame.rows() * topLeft[i][1]),
                        new Point(firstFrame.cols() * botRight[i][0], firstFrame.rows() * botRight[i][1])
                ));
            }
        }

        @Override
        public Mat processFrame(Mat input) {
            for (int i = 0; i < fractions; i++) {
                regions[i] = input.submat(new Rect(
                        new Point(input.cols() * topLeft[i][0], input.rows() * topLeft[i][1]),
                        new Point(input.cols() * botRight[i][0], input.rows() * botRight[i][1])
                ));
            }
            double[] avg;
            for (int i = 0; i < fractions; i++) {
                avg = Core.mean(regions[i]).val;
                for (int j = 0; j < 3; j++) {
                    avgRGB[i][j] = (int) avg[j];
                }
            }
            getAnalysis();
            if (longestSeq[0] != longestSeq[1]) {
                Imgproc.rectangle(
                        input,
                        new Point(
                                input.cols() * topLeft[longestSeq[0]][0],
                                input.rows() * topLeft[longestSeq[0]][1]),
                        new Point(
                                input.cols() * botRight[longestSeq[1] - 1][0],
                                input.rows() * botRight[longestSeq[1] - 1][1]),
                        new Scalar(0, 255, 0), 4);
            }

            return input;
        }

        @Override
        public void onViewportTapped() {
            viewportPaused = !viewportPaused;
            if (viewportPaused) {
                phoneCam.pauseViewport();
            } else {
                phoneCam.resumeViewport();
            }
        }
    }
}