/* *****************************************************************************
 *  Name:              Travis Davies
 *  Coursera User ID:  123456
 *  Last modified:     July 14, 2022
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;

public class SeamCarver {

    private Picture picture;
    private int H;
    private int W;


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        H = picture.height();
        W = picture.width();
        this.picture = picture;
    }

    // current picture
    public Picture picture() {
        return picture;
    }

    // width of current picture
    public int width() {
        return W;
    }

    // height of current picture
    public int height() {
        return H;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        return getEnergy(x, y);
    }

    private double getEnergy(int col, int row) {
        if (row == 0 || row == H - 1 || col == 0 || col == W - 1) {
            return 1000.0;
        }
        else {
            Color colorLeft = picture.get(col - 1, row);
            int redLeft = colorLeft.getRed();
            int greenLeft = colorLeft.getGreen();
            int blueLeft = colorLeft.getBlue();

            Color colorRight = picture.get(col + 1, row);
            int redRight = colorRight.getRed();
            int greenRight = colorRight.getGreen();
            int blueRight = colorRight.getBlue();

            Color colorUp = picture.get(col, row - 1);
            int redUp = colorUp.getRed();
            int blueUp = colorUp.getBlue();
            int greenUp = colorUp.getGreen();

            Color colorDown = picture.get(col, row + 1);
            int redDown = colorDown.getRed();
            int greenDown = colorDown.getGreen();
            int blueDown = colorDown.getBlue();

            int deltaXRed = redRight - redLeft;
            int deltaXGreen = greenRight - greenLeft;
            int deltaXBlue = blueRight - blueLeft;

            int deltaYRed = redDown - redUp;
            int deltaYGreen = greenDown - greenUp;
            int deltaYBlue = blueDown - blueUp;

            int deltaXSquared = (deltaXRed * deltaXRed) + (deltaXGreen * deltaXGreen) + (
                    deltaXBlue * deltaXBlue);
            int deltaYSquared = (deltaYRed * deltaYRed) + (deltaYGreen * deltaYGreen) + (
                    deltaYBlue * deltaYBlue);

            return Math.abs(Math.sqrt(deltaXSquared + deltaYSquared));
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] distTo = new double[H][W];
        int[][] edgeTo = new int[H][W];
        double[][] weight = new double[H][W];

        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                weight[row][col] = energy(col, row);
                if (col == 0) {
                    distTo[row][col] = 0.0;
                }
                else {
                    distTo[row][col] = Double.POSITIVE_INFINITY;
                }
            }
        }

        for (int col = 0; col < W - 1; col++) {
            for (int v = 0; v < H; v++) {
                for (int w = v - 1; w <= v + 1; w++) {
                    if (w < 0 || w > H - 1) continue;
                    if (distTo[w][col + 1] > distTo[v][col] + weight[v][col]) {
                        distTo[w][col + 1] = distTo[v][col] + weight[v][col];
                        edgeTo[w][col + 1] = v;
                    }
                }
            }
        }

        double minWeight = Double.POSITIVE_INFINITY;
        int v = Integer.MAX_VALUE;

        for (int row = 0; row < H; row++) {
            if (distTo[row][W - 1] < minWeight) {
                minWeight = distTo[row][W - 1];
                v = row;
            }
        }

        int[] horiztontalSeam = new int[W];
        horiztontalSeam[W - 1] = v;
        for (int col = W - 1; col > 0; col--) {
            horiztontalSeam[col - 1] = edgeTo[v][col];
            v = edgeTo[v][col];
        }

        return horiztontalSeam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] distTo = new double[H][W];
        int[][] edgeTo = new int[H][W];
        double[][] weight = new double[H][W];

        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                weight[row][col] = energy(col, row);
                if (row == 0) {
                    distTo[row][col] = 0.0;
                }
                else {
                    distTo[row][col] = Double.POSITIVE_INFINITY;
                }
            }
        }


        for (int row = 0; row < H - 1; row++) {
            for (int v = 0; v < W; v++) {
                for (int w = v - 1; w <= v + 1; w++) {
                    if (w < 0 || w > W - 1) continue;
                    if (distTo[row + 1][w] > distTo[row][v] + weight[row][v]) {
                        distTo[row + 1][w] = distTo[row][v] + weight[row][v];
                        edgeTo[row + 1][w] = v;
                    }
                }
            }
        }

        double minWeight = Double.POSITIVE_INFINITY;
        int v = Integer.MAX_VALUE;

        for (int col = 0; col < W; col++) {
            if (distTo[H - 1][col] < minWeight) {
                minWeight = distTo[H - 1][col];
                v = col;
            }
        }

        int[] verticalSeam = new int[H];
        verticalSeam[H - 1] = v;
        for (int row = H - 1; row > 0; row--) {
            verticalSeam[row - 1] = edgeTo[row][v];
            v = edgeTo[row][v];
        }

        return verticalSeam;
    }


    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("argument must not be null");
        if (H <= 1) throw new IllegalArgumentException(
                "cannot remove horizontal seam when height is less than or equal to 1");
        int length = seam.length;
        if (length > W) throw new IllegalArgumentException(
                "horizontal length of seam greater than width of photo");
        if (length < W) throw new IllegalArgumentException(
                "horizontal length of seam less than width of photo");
        for (int item : seam) {
            if (item < 0 || item > H - 1) throw new IllegalArgumentException(
                    "elements inside seam are outside the prescribed height range");
        }

        for (int i = 1; i < length; i++) {
            int difference = Math.abs(seam[i] - seam[i - 1]);
            if (difference > 1) throw new IllegalArgumentException(
                    " adjacent elements cannot differ by more than 1 pixel");
        }

        Picture newPicture = new Picture(W, H - 1);

        for (int col = 0; col < W; col++) {
            for (int row = 0; row < H - 1; row++) {
                if (row >= seam[col]) {
                    Color color = picture.get(col, row + 1);
                    newPicture.set(col, row, color);
                }
                else {
                    Color color = picture.get(col, row);
                    newPicture.set(col, row, color);
                }
            }
        }
        this.picture = newPicture;
        this.H--;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("argument must not be null");
        if (W <= 1) throw new IllegalArgumentException(
                "cannot remove vertical seam when width is less than or equal to 1");
        int length = seam.length;
        if (length > H) throw new IllegalArgumentException(
                "vertical length of seam greater than height of photo");
        if (length < H) throw new IllegalArgumentException(
                "vertical length of seam less than height of photo");
        for (int item : seam) {
            if (item < 0 || item > W - 1) throw new IllegalArgumentException(
                    "elements inside seam are outside the prescribed width range");
        }
        for (int i = 1; i < length; i++) {
            int difference = Math.abs(seam[i] - seam[i - 1]);
            if (difference > 1) throw new IllegalArgumentException(
                    " adjacent elements cannot differ by more than 1 pixel");
        }

        Picture newPicture = new Picture(W - 1, H);

        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W - 1; col++) {
                if (col >= seam[row]) {
                    Color color = picture.get(col + 1, row);
                    newPicture.set(col, row, color);
                }
                else {
                    Color color = picture.get(col, row);
                    newPicture.set(col, row, color);
                }
            }
        }
        this.picture = newPicture;
        this.W--;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        StdOut.println("W: " + picture.width());
        StdOut.println("H: " + picture.height());
        SeamCarver seamCarver = new SeamCarver(picture);
        int[] seam = seamCarver.findVerticalSeam();
        for (int i = 0; i < seam.length; i++) {
            double energy = seamCarver.energy(seam[i], i);
            StdOut.printf("%2d, %2d, %6.2f\n", i, seam[i], energy);
        }
        StdOut.println();
        int[] hseam = seamCarver.findHorizontalSeam();
        for (int i = 0; i < hseam.length; i++) {
            double energy = seamCarver.energy(i, hseam[i]);
            StdOut.printf("%2d, %2d, %6.2f\n", i, hseam[i], energy);
        }
        // seamCarver.removeVerticalSeam(seam);
        seamCarver.removeHorizontalSeam(hseam);
        Picture picture1 = seamCarver.picture();
        StdOut.println("W: " + picture1.width());
        StdOut.println("H: " + picture1.height());
    }

}
