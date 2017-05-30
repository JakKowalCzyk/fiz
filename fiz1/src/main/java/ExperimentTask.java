import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by JKowalczyk on 2017-05-30.
 */
public class ExperimentTask {

    private static final int SPINS_SIZE = 100;
    private static final int MONTE_CARLO_STEPS = 150000;
    private static final String FILE_NAME = "C:/users/t/temperature_chart";
    private static final String SHHEET_NAME = "temperature_";
    private static final String EXTENSION = ".xlsx";


    private Integer[][] spins = new Integer[SPINS_SIZE][SPINS_SIZE];
    private Double temperature;


    public ExperimentTask(Double temperature) {
        this.temperature = temperature;
    }

    public void startExperiment() throws IOException {
        XSSFWorkbook xssfWorkbook = createWorkbook();
        XSSFSheet sheet = createSheet(xssfWorkbook);
        fillSpins();
        fillSheet(sheet);
        saveWorkbook(xssfWorkbook);
    }

    private void saveWorkbook(XSSFWorkbook xssfWorkbook) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(FILE_NAME + temperature + EXTENSION));
        xssfWorkbook.write(fileOutputStream);
        fileOutputStream.close();
    }

    private void fillSpins() {
        for (int i = 0; i < spins.length; i++) {
            for (int j = spins.length - 1; j >= 0; j--) {
                spins[i][j] = new Random().nextBoolean() ? 1 : -1;
            }
        }
    }

    private XSSFWorkbook createWorkbook() throws IOException {
        return new XSSFWorkbook();
    }

    private XSSFSheet createSheet(XSSFWorkbook xssfWorkbook) {
        XSSFSheet sheet = xssfWorkbook.createSheet(SHHEET_NAME + temperature);
        XSSFRow xssfRow = sheet.createRow(0);
        xssfRow.createCell(0).setCellValue("Monte Carlo Step:");
        xssfRow.createCell(1).setCellValue("Magnetism:");
        return sheet;
    }

    private void fillSheet(XSSFSheet sheet) {
        int cellId = 1;
        for (int i = 0; i < MONTE_CARLO_STEPS; i++) {
            touchSpins();
            if (i % 100 == 0) {
                setMagnetism(i, sheet, cellId);
                cellId++;
            }
        }
    }

    private void setMagnetism(int step, XSSFSheet sheet, int cellId) {
        double magnetism = getMagnetism();
        XSSFRow xssfRow = sheet.createRow(cellId);
        xssfRow.createCell(0).setCellValue(step);
        xssfRow.createCell(1).setCellValue(magnetism);
    }

    private double getMagnetism() {
        double sumSpins = 0;
        for (int i = 0; i < SPINS_SIZE; i++) {
            for (int j = 0; j < SPINS_SIZE; j++) {
                sumSpins += spins[i][j];
            }
        }
        return Math.abs(sumSpins) / Math.pow(SPINS_SIZE, 2);
    }

    private void touchSpins() {
        for (int i = 0; i < Math.pow(SPINS_SIZE, 2); i++) {
            touchSpin();
        }
    }

    private void touchSpin() {
        int xLocation = new Random().nextInt(SPINS_SIZE);
        int yLocation = new Random().nextInt(SPINS_SIZE);
        int currentSpin = spins[xLocation][yLocation];
        invertSpins(xLocation, yLocation, currentSpin);
    }

    private void invertSpins(int xLocation, int yLocation, int currentSpin) {
        int sum = getSum(xLocation, yLocation);
        double deltaE = getDeltaE(currentSpin, sum);
        if (deltaE < 0) {
            invertSpin(xLocation, yLocation);
        } else if (isRandom(deltaE)) {
            invertSpin(xLocation, yLocation);
        }
    }

    private int getSum(int xLocation, int yLocation) {
        int right = getRight(xLocation, yLocation);
        int left = getLeft(xLocation, yLocation);
        int bottom = getBottom(xLocation, yLocation);
        int top = getTop(xLocation, yLocation);
        return getNeighboursSum(right, left, bottom, top);
    }

    private int getNeighboursSum(int right, int left, int bottom, int top) {
        return left + right + top + bottom;
    }

    private double getDeltaE(int currentSpin, int sum) {
        int ep = -currentSpin * sum;
        int ek = currentSpin * sum;
        return (double) (ek - ep);
    }

    private void invertSpin(int xLocation, int yLocation) {
        spins[xLocation][yLocation] = -spins[xLocation][yLocation];
    }

    private boolean isRandom(double deltaE) {
        double exp = Math.exp(-deltaE / temperature);
        return new Random().nextDouble() <= exp;
    }

    private int getRight(int xLocation, int yLocation) {
        if (isMaxRightOrBottom(xLocation)) {
            return spins[xLocation + 1][yLocation];
        }
        return spins[0][yLocation];
    }

    private int getLeft(int xLocation, int yLocation) {
        if (isMaxLeftOrTop(xLocation)) {
            return spins[xLocation - 1][yLocation];
        }
        return spins[SPINS_SIZE - 1][yLocation];
    }

    private int getBottom(int xLocation, int yLocation) {
        if (isMaxRightOrBottom(yLocation)) {
            return spins[xLocation][yLocation + 1];
        }
        return spins[xLocation][0];
    }


    private int getTop(int xLocation, int yLocation) {
        if (isMaxLeftOrTop(yLocation)) {
            return spins[xLocation][yLocation - 1];
        }
        return spins[xLocation][SPINS_SIZE - 1];
    }

    private boolean isMaxRightOrBottom(int xLocation) {
        return xLocation + 1 != SPINS_SIZE;
    }

    private boolean isMaxLeftOrTop(int xLocation) {
        return (xLocation - 1) != -1;
    }
}
