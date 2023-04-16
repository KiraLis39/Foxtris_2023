package ru.foxtris.utils;

public class MatrixUtil {
    public static int[][] rotate(int[][] matrix) {
        int[] tmpArray = new int[9];
        int indexArray = 0;

        for (int[] row : matrix) {
            for (int elem : row) {
                tmpArray[indexArray++] = elem;
            }
        }

        indexArray = 0;
        for (int i = 2; i >= 0; i--) {
            for (int j = 0; j < 3; j++) {
                matrix[j][i] = tmpArray[indexArray++];
            }
        }

        return matrix;
    }

    private MatrixUtil() {
    }
}
