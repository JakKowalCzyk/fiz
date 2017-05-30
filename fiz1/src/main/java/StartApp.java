import java.io.IOException;

/**
 * Created by JKowalczyk on 2017-05-30.
 */
public class StartApp {

    public static void main(String[] args) throws IOException {
        Double temperature = Double.parseDouble(System.getProperty("temp"));
        ExperimentTask experimentTask = new ExperimentTask(temperature);
        experimentTask.startExperiment();
    }
}
