package seng202.team10.io;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team10.models.Crash;
import seng202.team10.exceptions.DataImportException;
/**
 * Class handling the importing of crash data from CSV files.
 *
 * @author Neil Alombro
 * @author Zipporah Price
 * @author Angelica Silva
 */

public class CrashCsvImporter {

    private static final Logger log = LogManager.getLogger(CrashCsvImporter.class);

    /**
     * List of all the crashes as Point objects from the given file object.
     *
     * @param file a file containing the crash data
     * @return points list of all crashes from the given file
     * @throws DataImportException in case of CSV validation error or IO error
     */
    public List<Crash> crashListFromFile(File file) throws DataImportException {
        List<Crash> pointList = new ArrayList<Crash>();

        try (FileReader reader = new FileReader(file)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                csvReader.skip(1);
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    if (!Objects.equals(line[0], "")) {
                        Crash currentPoint = crashFromString(line);
                        if (currentPoint != null) {
                            pointList.add(currentPoint);
                        }
                    }
                }
                return pointList;
            } catch (CsvValidationException e) {
                log.error(e);
                throw new DataImportException("Invalid CSV format.");
            }
        } catch (IOException e) {
            log.error(e);
            throw new DataImportException("Error reading the file.");
        }
    }


    private int changeEmptyToZero(String string) {
        if (string != "" && string != null) {
            return Integer.parseInt(string);
        } else {
            return 0;
        }
    }

    /**
     * Takes a list of strings representing variables.
     * From the crash data CSV file and returns a Point object.
     *
     * @param crashVariables a list of strings representing variables
     * @return Point object initialised with given crashVariables
     */
    private Crash crashFromString(String[] crashVariables) {
        try {
            int objectId = changeEmptyToZero(crashVariables[0]);
            boolean bicycleInvolved = changeEmptyToZero(crashVariables[2]) > 0;
            boolean busInvolved = changeEmptyToZero(crashVariables[4]) > 0;
            boolean carInvolved = changeEmptyToZero(crashVariables[5]) > 0;
            int crashYear = changeEmptyToZero(crashVariables[14]);
            String crashLocation1 = crashVariables[9];
            String crashLocation2 = crashVariables[10];
            String severity = crashVariables[12];
            boolean holiday = !Objects.equals(crashVariables[22], "");
            boolean mopedInvolved = changeEmptyToZero(crashVariables[28]) > 0;
            boolean motorcycleInvolved = changeEmptyToZero(crashVariables[29]) > 0;
            boolean parkedVehicleInvolved = changeEmptyToZero(crashVariables[35]) > 0;
            boolean pedestrianInvolved = changeEmptyToZero(crashVariables[36]) > 0;
            String region = crashVariables[39];
            boolean schoolBusInvolved = changeEmptyToZero(crashVariables[44]) > 0;
            int speedLimit = changeEmptyToZero(crashVariables[47]);
            boolean trainInvolved = changeEmptyToZero(crashVariables[57]) > 0;
            boolean truckInvolved = changeEmptyToZero(crashVariables[59]) > 0;
            String weather = crashVariables[65];
            float longitude = Float.parseFloat(crashVariables[68]);
            float latitude = Float.parseFloat(crashVariables[67]);

            return new Crash.Builder(objectId)
                    .speedLimit(speedLimit)
                    .year(crashYear)
                    .location1(crashLocation1)
                    .location2(crashLocation2)
                    .severity(severity)
                    .region(region)
                    .weather(weather)
                    .longitude(longitude)
                    .latitude(latitude)
                    .bicycleInvolved(bicycleInvolved)
                    .busInvolved(busInvolved)
                    .carInvolved(carInvolved)
                    .holiday(holiday)
                    .mopedInvolved(mopedInvolved)
                    .motorcycleInvolved(motorcycleInvolved)
                    .parkedVehicleInvolved(parkedVehicleInvolved)
                    .pedestrianInvolved(pedestrianInvolved)
                    .schoolBusInvolved(schoolBusInvolved)
                    .trainInvolved(trainInvolved)
                    .truckInvolved(truckInvolved)
                    .build();
        } catch (NumberFormatException e) {
            log.error(e);
        }
        return null;
    }

}
