import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * DataGen
 */
public class DataGen {
    private final CharSequence DELIMITER = ",";
    private String DDL_LOCATION;
    private String CSV_LOCATION;

    // random string
    private final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private final int STRING_SIZE = 6;
    private final StringBuilder sb = new StringBuilder(STRING_SIZE);
    // random timestamp
    private final long offset = Timestamp.valueOf("2010-01-01 00:00:00").getTime();
    private final long end = Timestamp.valueOf("2020-01-01 00:00:00").getTime();
    // random date
    private final LocalDate from = LocalDate.of(2010, 1, 1);
    private final LocalDate to = LocalDate.of(2020, 1, 1);

    private int numRecords;
    private boolean cleanDLL = false;
    private ArrayList<DDLType> ddl;
    private Random random = new Random();
    private ArrayList<String> buffer;

    private enum DDLType {
        tinyint,
        smallint,
        iint,
        bigint,
        ffloat,
        ddouble,
        sstring,
        date,
        timestamp,
        bboolean,
    }

    public DataGen(int records) {
        numRecords = records;
        ddl = new ArrayList<>();
    }

    public DataGen(int records, boolean clnDDL) {
        numRecords = records;
        cleanDLL = clnDDL;
        ddl = new ArrayList<>();
    }

    public void runProgram() {
        long startTime = clock();
        DDL_LOCATION = "./DDL.txt";
        CSV_LOCATION = "./data.csv";
        readFile();
        createCSV();
        printElapsedTime(startTime);
    }

    public void runSample() {
        long startTime = clock();
        if (!cleanDLL) {
            DDL_LOCATION = "./sample/sampleDDL.txt";
            CSV_LOCATION = "./sample/sampledata.csv";
        } else {
            DDL_LOCATION = "./sample/sampleDDLClean.txt";
            CSV_LOCATION = "./sample/sampledataClean.csv";
        }
        readFile();
        createCSV();
        printElapsedTime(startTime);
    }

    /** 
     * Reads DDL and validates input. Allocates buffer if successful.
     */
    private void readFile() {
        try (BufferedReader file = new BufferedReader(new FileReader(DDL_LOCATION))) {
            String line;
            while ((line = file.readLine()) != null) {
                line = line.toLowerCase();
                
                // Optional advanced mode to clean DDL
                if (cleanDLL) {
                    line = line.trim();
                    line = line.replaceAll(",", "");
                    line = line.split(" ")[1];
                }

                switch (line) {
                    case "tinyint":
                        ddl.add(DDLType.tinyint); break;
                    case "smallint":
                        ddl.add(DDLType.smallint); break;
                    case "int":
                        ddl.add(DDLType.iint); break;
                    case "bigint":
                        ddl.add(DDLType.bigint); break;
                    case "float":
                        ddl.add(DDLType.ffloat); break;
                    case "double":
                        ddl.add(DDLType.ddouble); break;
                    case "string":
                        ddl.add(DDLType.sstring); break;
                    case "date":
                        ddl.add(DDLType.date); break;
                    case "timestamp":
                        ddl.add(DDLType.timestamp); break;
                    case "boolean":
                        ddl.add(DDLType.bboolean); break;
                    default:
                        System.out.println("Check the DDL file!");
                        throw new Exception("DDLType: '" + line + "' is not supported!");
                }
                buffer = new ArrayList<>(ddl.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createCSV() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(CSV_LOCATION));
            while (numRecords > 0) {
                String row = generateRow();
                out.append(row);
                out.newLine();
                numRecords--;
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Error with CSV location!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private String generateRow() throws Exception {
        for (DDLType type : ddl) {
            switch (type) {
                case tinyint:
                    buffer.add(Integer.toString(random.nextInt(Byte.MAX_VALUE + 1)));
                    break;
                case smallint:
                    buffer.add(Integer.toString(random.nextInt(Short.MAX_VALUE + 1)));
                    break;
                case iint:
                    buffer.add(Integer.toString(random.nextInt()));
                    break;
                case bigint:
                    buffer.add(Long.toString(random.nextLong()));
                    break;
                case ffloat:
                    buffer.add(Float.toString(random.nextFloat()));
                    break;
                case ddouble:
                    buffer.add(Double.toString(random.nextDouble()));
                    break;
                case sstring:
                    buffer.add(randomString());
                    break;
                case date:
                    long days = from.until(to, ChronoUnit.DAYS);
                    long randomDays = ThreadLocalRandom.current().nextLong(days + 1);
                    LocalDate randomDate = from.plusDays(randomDays);
                    buffer.add(randomDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    break;
                case timestamp:
                    long diff = end - offset + 1;
                    Timestamp randTimestamp = new Timestamp(offset + (long) (Math.random() * diff));
                    buffer.add(randTimestamp.toString());
                    break;
                case bboolean:
                    buffer.add(Boolean.toString(random.nextBoolean()));
                    break;
                default:
                    throw new Exception("DDLType not added in function!");
            }
        }
        String out = String.join(DELIMITER, buffer);
        buffer.clear();
        return out;
    }

    private String randomString() {
        sb.setLength(0);
        for (int i = 0; i < STRING_SIZE; i++) {
            char c = alphabet[random.nextInt(alphabet.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    private long clock() {
        return System.currentTimeMillis();
    }

    private void printElapsedTime(long startTime) {
        System.out.println((clock() - startTime) / 1000.0f + " secs");
    }

    public static void runTests() {
        DataGen testGen = new DataGen(10);
        testGen.runSample();
        DataGen testGenClean = new DataGen(10, true);
        testGenClean.runSample();
    }

    public static void main(String[] args) throws Exception {
        DataGen gen;
        switch (args.length) {
            case 1:
                if (!"test".equals(args[0])) {
                    gen = new DataGen(Integer.parseInt(args[0]));
                    gen.runProgram();
                } else {
                    System.out.println("Running Sample DDLs");
                    runTests();
                }
                break;
            case 2:
                if ("clean".equals(args[1])) {
                    gen = new DataGen(Integer.parseInt(args[0]), true);
                    gen.runProgram();
                } else {
                    throw new Exception("'" + args[1] + "' is an invalid argument!");
                }
                break;
            default:
                throw new Exception("Invalid number of arguments!");
        }
    }
}
