import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {

    private Picture picture;
    private double[][] energy;

    public SeamCarver(Picture picture) {
        validateForNull(picture);
        this.picture = new Picture(picture);

        calculateEnergy();
    }  // create a seam carver object based on the given picture

    public Picture picture() {
        return picture;
    } // current picture

    public int width() {
        return picture.width();
    }  // width of current picture

    public int height() {
        return picture.height();
    } // height of current picture

    public double energy(int x, int y) {
        validateCoordinates(x, y);

        return computeEnergyOfPixel(x, y);
    } // energy of pixel at column x and row y

    private double computeEnergyOfPixel(int x, int y) {

        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) return 1000;

        Color xColorLeft = picture.get(x - 1, y);
        Color xColorRight = picture.get(x + 1, y);

        int xRedDiff = xColorLeft.getRed() - xColorRight.getRed();
        int xGreenDiff = xColorLeft.getGreen() - xColorRight.getGreen();
        int xBlueDiff = xColorLeft.getBlue() - xColorRight.getBlue();

        int xResult = xRedDiff * xRedDiff + xGreenDiff * xGreenDiff + xBlueDiff * xBlueDiff;


        Color yColorTop = picture.get(x, y - 1);
        Color yColorBottom = picture.get(x, y + 1);

        int yRedDiff = yColorTop.getRed() - yColorBottom.getRed();
        int yGreenDiff = yColorTop.getGreen() - yColorBottom.getGreen();
        int yBlueDiff = yColorTop.getBlue() - yColorBottom.getBlue();

        int yResult = yRedDiff * yRedDiff + yGreenDiff * yGreenDiff + yBlueDiff * yBlueDiff;

        return Math.sqrt(xResult + yResult);
    }

    private void validateCoordinates(int x, int y) {
        if (x < 0 || x > width() - 1 || y < 0 || y > height() - 1) {
            throw new IllegalArgumentException(String.format("width = %s, x = %s \nheight = %s, y = %s", width(), x, height(), y));
        }
    }

    private void validateForNull(Object object) {
        if (object == null) throw new IllegalArgumentException("object is null");
    }

    private void validateSeamCorrectness(int[] seam) {
//        throw new IllegalArgumentException("seam is incorrect!");
    }

    public int[] findHorizontalSeam() {

        int[] seam;
        rotatePicture();
        seam = findVerticalSeam();
        rotatePicture();

        return seam;


    }// sequence of indices for horizontal seam

    private void rotatePicture() {
        Picture oldPicture = picture;
        picture = new Picture(height(), width());
        for (int col = 0; col < oldPicture.height(); col++) {
            for (int row = 0; row < oldPicture.width(); row++) {
                picture.set(col, row, oldPicture.get(row, col));
            }
        }
        calculateEnergy();
    }

    private void calculateEnergy() {
        energy = new double[width()][height()];
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                energy[x][y] = energy(x, y);
            }
        }
    }

    public int[] findVerticalSeam() {
        int[][] parent = new int[width()][height()];
        double[] distTo = new double[width()];
        double[] oldDistTo = new double[width()];
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++) {
                relax(col, row, distTo, oldDistTo, parent);
            }
            System.arraycopy(distTo, 0, oldDistTo, 0, width());
        }

        double min = oldDistTo[0];
        int index = 0;
        for (int i = 0; i < oldDistTo.length; i++) {
            double d = oldDistTo[i];
            if (min > d) {
                min = d;
                index = i;
            }
        }

        int[] seam = new int[height()];
        seam[height() - 1] = index;

        for (int i = height() - 2; i >= 0; i--) {
            seam[i] = parent[index][i + 1];
            index = seam[i];
        }

        return seam;
    }// sequence of indices for vertical seam

    private void relax(int col, int row, double[] distTo, double[] oldDistTo, int[][] parent) {
        if (row == 0) {
            parent[col][row] = -1;
            distTo[col] = 1000;

            return;
        }

        if (col == 0) {  // only 2 vertices
            double a = oldDistTo[col];
            double b = oldDistTo[col + 1];

            double min = Math.min(a, b);

            distTo[col] = min + energy(col, row);
            if (a == min) {
                parent[col][row] = col;
            } else {
                parent[col][row] = col + 1;
            }

            return;
        }

        if (col == width() - 1) {    // only 2 vertices
            double a = oldDistTo[col];
            double b = oldDistTo[col - 1];

            double min = Math.min(a, b);

            distTo[col] = min + energy[col][row];

            if (a == min) {
                parent[col][row] = col;
            } else {
                parent[col][row] = col - 1;
            }

            return;
        }

        // 3 vertices

        double left = oldDistTo[col - 1];
        double mid = oldDistTo[col];
        double right = oldDistTo[col + 1];

        double min = Math.min(Math.min(left, mid), right);

        distTo[col] = min + energy[col][row];
        if (min == left) {
            parent[col][row] = col - 1;
        } else if (min == mid) {
            parent[col][row] = col;
        } else {
            parent[col][row] = col + 1;
        }
    }


    public void removeHorizontalSeam(int[] seam) {
        validateForNull(seam);
        validateSeamCorrectness(seam);

        rotatePicture();
        removeVerticalSeam(seam);
        rotatePicture();


    } // remove horizontal seam from current picture

    public void removeVerticalSeam(int[] seam) {
        validateForNull(seam);
        validateSeamCorrectness(seam);

        Picture oldPicture = picture;
        picture = new Picture(width()-1, height());
        for (int row = 0; row < oldPicture.height(); row++) {
            for (int col = 0; col < oldPicture.width(); col++) {
                if(seam[row]==col) continue;

                if(col>seam[row]) {
                    picture.set(col-1, row, oldPicture.get(col,row));
                } else {
                    picture.set(col, row, oldPicture.get(col,row));
                }
            }
        }
        calculateEnergy();
    } // remove vertical seam from current picture


    public static void main(String[] args) {

        String fileName = args[0];

        SeamCarver seamCarver = new SeamCarver(new Picture(fileName));

        System.out.print("Vertical seam : ");
        System.out.println(Arrays.toString(seamCarver.findVerticalSeam()));

        System.out.print("Horizontal seam : ");
        System.out.println(Arrays.toString(seamCarver.findHorizontalSeam()));

    }
}