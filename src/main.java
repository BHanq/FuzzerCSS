import static java.lang.Thread.sleep;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class main {
    static String outputName = "outputBenja";
    static int outputNumberFile = 1;
    static int inputNumber = 0;
    static byte[] magicNb,version, author, authorHeader,WidthHeightHeader,Width,Height,ColTaSizeHeader,ColTaSize,ColTableHeader,ColTable,commentHeader,comment,Pixels,AABytes;
    private static final Logger LOGGER = Logger.getLogger("Fuzzer_Log");


    public static void main(String[] args) {
        try {
            LOGGER.addHandler(new FileHandler("FuzzLog.log"));
            checkMagicNumber();
            checkVersionNumber();
            checkHeader();
            checkTextfield();
            checkHeightWidth();
            checkColTableSize();
            checkPixelsLength();
            checkWrongPixels();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check all textfields (Author & Comment)
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws IOException
     */
    private static void checkTextfield() throws InterruptedException, TimeoutException, IOException {
        checkAuthorSize();
        checkCommentSize();
    }

    /**
     * Check some bound of the magic number
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws IOException
     */
    public static void checkMagicNumber() throws InterruptedException, TimeoutException, IOException {
        fillWithGoodParam();
        for(short i = -10 ; i <10; i++) { //Try some value from -32760 to 32760
            magicNb= shortToByte( (short)(i*(short)3276),true);
            writeToFile();
            if(executeConverter()){
                LOGGER.info("Magic number field crashed with a value of " +  i + " in decimal and " + String.format( "%h",i) + " in hexa" );
                return;
            }
        }
    }

    /**
     * Check all version possible
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws IOException
     */
    public static void checkVersionNumber() throws InterruptedException, TimeoutException, IOException {
        fillWithGoodParam();
        for(Byte i = Byte.MIN_VALUE ; i <Byte.MAX_VALUE; i++) {
            version= new byte[]{i,0};
            writeToFile();
            if(executeConverter()){
                LOGGER.info("Version number crashed with a value of " + i + " in decimal and " + String.format( "%h",i) + " in hexa");
                return;
            }
        }
    }

    /**
     * Check All Header possible
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws IOException
     */
    public static void checkHeader() throws InterruptedException, TimeoutException, IOException {
        fillWithGoodParam();
        for(Byte i = Byte.MIN_VALUE ; i <Byte.MAX_VALUE; i++) {
            commentHeader= new byte[]{i};
            writeToFile();
            if(executeConverter()){
                LOGGER.info("Header field crashed with a value of " + i + " in decimal and " + String.format( "%h",i) + " in hexa" );
                return;
            }
        }
    }

    /**
     * Check the size of a author name field, between 0 and 100000 characters
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws IOException
     */
    public static void checkAuthorSize() throws InterruptedException, TimeoutException, IOException {
        fillWithGoodParam();
        for(int i = 0 ; i <10000; i+=10) {
            author = new byte[i];
            Arrays.fill(author,(byte) 02 );
            if(author.length >0)
                author[author.length-1] = (byte) 00;
            writeToFile();
            if(executeConverter()){
                LOGGER.info("Author field crashed with a length of " + i + " characters");
                return;
            }
        }
    }

    /**
     * Check the size of a comment field, between 0 and 100000 characters
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws IOException
     */
    public static void checkCommentSize() throws InterruptedException, TimeoutException, IOException {
        fillWithGoodParam();
        for(int i = 0 ; i <100000; i+=10) {
            comment = new byte[i];
            Arrays.fill(comment,(byte) 01 );
            if(comment.length >0)
            comment[comment.length-1] = (byte) 00;
            writeToFile();
            if(executeConverter()){
                LOGGER.info("Comment field crashed with a length of " + i + " characters");
                return;
            }
        }
    }

    /**
     * Check all the kind of variation for the height & the width dema
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws IOException
     */
    public static void  checkHeightWidth() throws InterruptedException, TimeoutException, IOException {
        checkOnlyWidth();
        checkOnlyHeight();
        checkBothWidthHeight();

    }

    /**
     * Check different value for the width & the height
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    private static void checkBothWidthHeight() throws InterruptedException, TimeoutException, IOException {
        fillWithGoodParam();
        for(int i = -500000 ; i <500000; i+=10000) {
            Width = intToByte(i,true);
            Height = Width;
            writeToFile();
            if(executeConverter()){
                LOGGER.info("Width & Height fields crashed with both a value of " + i );
                return;
            }
        }
    }

    /**
     * Check different value for the width only (without touching to height)
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    private static void checkOnlyWidth() throws IOException, InterruptedException, TimeoutException {
        fillWithGoodParam();
        for(int i = -100000 ; i <100000; i+=1000) {
            Width = intToByte(i,true);
            writeToFile();
            if(executeConverter()){
                LOGGER.info("Width field crashed with a value of " + i );
                return;
            }
        }
    }

    /**
     * Check different value for the Height only (without touching to width)
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    private static void checkOnlyHeight() throws IOException, InterruptedException, TimeoutException {
        fillWithGoodParam();
        for(int i = -100000 ; i <100000; i+=1000) {
            Height = intToByte(i,true);
            writeToFile();
            if(executeConverter()){
                LOGGER.info("Height field crashed with a value of " + i );
                return;
            }
        }
    }

    /**
     * Check if the Color Table Size Crash with a big size
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws IOException
     */
    public static void checkColTableSize() throws InterruptedException, TimeoutException, IOException {
        fillWithGoodParam();
        for(short i = -100 ; i <100; i++) { //Try some value from -32760 to 32760
            int j=i*10;
            if(i==-100){
                j=Integer.MIN_VALUE;
            }else if(i==-99){
                j=Integer.MAX_VALUE;
            }
            ColTaSize= intToByte( j,true);
            writeToFile();
            if(executeConverter()){
                LOGGER.info("Color Table Size field crashed with a value of " +  i + " in decimal and " + String.format( "%h",i) + " in hexa" );
                return;
            }
        }
    }

    /**
     * Check if the converter crash with a different Pixels length than the expected pixels length (Got with Width*Height)
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    private static void checkPixelsLength() throws IOException, InterruptedException, TimeoutException {
        fillWithGoodParam();
        for(int i = 0 ; i <1000000; i=(i+1)*2) {
            Pixels = new byte[i];
            Arrays.fill(Pixels,(byte) 01 );
            writeToFile();
            if(executeConverter()){
                LOGGER.info("Pixels field crashed with a length of " + i + " pixels");
                return;
            }
        }
    }

    /**
     * Check with wrong pixels number
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    private static void checkWrongPixels() throws IOException, InterruptedException, TimeoutException {
        fillWithGoodParam();
        for(Byte i = Byte.MIN_VALUE ; i <Byte.MAX_VALUE; i++) {
                Pixels = new byte[4];
                Arrays.fill(Pixels, i);
                writeToFile();
                if (executeConverter()) {
                    LOGGER.info("Pixels field crashed with a pixel with the value of " +  i + " in decimal and " + String.format( "%h",i) + " in hexa");
                    return;
                }

        }
    }

    /**
     * Execute the converter
     * @return true if the converter crashed with the actual values
     * @throws IOException
     */
    public static boolean executeConverter()
            throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String outputRealName = outputName + ".img";
        Process process = runtime.exec("./converter_linux_x8664 inputBenja" + inputNumber + ".img " + outputRealName); //Execute the converter on our generated file
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line ;
        Boolean error =false;
        while ((line = stdInput.readLine ()) != null) {
            if(line.equals("*** The program has crashed ***")){
                error=true;
            }
        }
        if (error) { //Program crashed
            executeCommand("mv  inputBenja" +inputNumber + ".img inputFile" + outputNumberFile + ".img"); //Change the name of the input file
            outputNumberFile++;
            return true;
        } else { //If the program doesnt crash
            executeCommand("rm " + outputName + ".img"); //Remove input & output file
           executeCommand("rm  inputBenja" +inputNumber +".img");
        }
        return false;
    }

    /**
     * Fill all the field with good values
     */
    public static void fillWithGoodParam(){
        magicNb = hexStringToByteArray("ABCD");//shortToByte((short) i, true);//
        version = shortToByte((short) 100, true);//hexStringToByteArray("6400");//
        authorHeader = hexStringToByteArray("01");//randomByteGenerator(10000);//
        author = hexStringToByteArray("0152616D696E00");//randomByteGenerator(10000);//
        WidthHeightHeader = hexStringToByteArray("02");
        Width = intToByte( 2, true);//hexStringToByteArray("02000000");//intToByte(4,2,true);
        Height = intToByte( 2, true);
        ColTaSizeHeader = hexStringToByteArray("0A");
        ColTaSize = intToByte( 2, true);//hexStringToByteArray("08000000");
        ColTableHeader = hexStringToByteArray("0B");
        ColTable = hexStringToByteArray("FFFFFFFFFFFFFFFF");
        commentHeader = hexStringToByteArray("0C");
        comment = hexStringToByteArray("0C48656C6C6F00");
        Pixels = hexStringToByteArray("00010100");
        AABytes = hexStringToByteArray("AA");
    }

    /**
     * Write all fields to the file
     */
    public static void writeToFile(){
        try{
            inputNumber++;
            FileOutputStream os = new FileOutputStream("inputBenja" + inputNumber + ".img");
            os.write(magicNb);
            os.write(version);
            os.write(authorHeader);
            os.write(author);
            os.write(WidthHeightHeader);
            os.write(Width);
            os.write(Height);
            os.write(ColTaSizeHeader);
            os.write(ColTaSize);
            os.write(ColTableHeader);
            os.write(ColTable);
            os.write(commentHeader);
            os.write(comment);
            os.write(Pixels);
            os.close();
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    /**
     * Execute a shell command
     * @param commandLine the actual command to execute
     */
    public static void executeCommand(final String commandLine){
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(commandLine);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return;
    }

    /**
     * Convert Int to Byte array
     * @param transformInt the actual int to convert
     * @param toLittleEndian should we convert in little endian or not
     * @return the byte array of the converted value
     */
public static byte[] intToByte(int transformInt, boolean toLittleEndian){
    ByteBuffer byteBuffer = ByteBuffer.allocate(4);
    if (toLittleEndian){
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    byteBuffer.putInt(transformInt);
    return byteBuffer.array();
}

    /**
     * Convert Short to Byte array
     * @param transformShort the actual short to convert
     * @param toLittleEndian should we convert in little endian or not
     * @return the byte array of the converted value
     */
    public static byte[] shortToByte(short transformShort, boolean toLittleEndian){
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        if (toLittleEndian){
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        byteBuffer.putShort(transformShort);
        return byteBuffer.array();
    }


    /**
     * Random byte generator
     * @param length The length of the byte array
     * @return the byte array containing random bytes
     */
    static byte[] randomByteGenerator(int length) {
        Random rd = new Random();
        byte[] arr = new byte[length];
        rd.nextBytes(arr);
        return arr;
    }

    /**
     * Tranform string to corresponding byte array.
     * @param s a String representing bytes
     * @return the byte array which contains the hexadecimal code in the string s
     */
    static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}
