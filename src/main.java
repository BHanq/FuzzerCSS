import static java.lang.Thread.sleep;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.Random;

public class main {
    static String outputName = "outputBenja";
    static int outputNumberFile = 1;
    static int inputNumber = 0;
    static byte[] magicNb,version, author,WidthHeightHeader,Width,Height,ColTaSizeHeader,ColTaSize,ColTableHeader,ColTable,commentHeader,comment,Pixels,AABytes;

    public static void main(String[] args) {
        try {


//             for(short i = Short.MIN_VALUE ; i <Short.MAX_VALUE; i++) {//for(short i = 80 ; i <103; i++) {//
            for(int i = 0; i <400000; i=i+50000) {//for(short i = 80 ; i <103; i++) {//
                //for (int j = 0; j < 258; j++) {
                    inputNumber++;
                    FileOutputStream os = new FileOutputStream("inputBenja" + inputNumber + ".img");
                    System.out.println("");
                    System.out.print(i + " ");


                    //Magic Number
                    magicNb = hexStringToByteArray("ABCD");//shortToByte((short) i, true);//

                    version = shortToByte((short) 100, true);//hexStringToByteArray("6400");//
                    author = hexStringToByteArray("0152616D696E00");//randomByteGenerator(10000);//
                 WidthHeightHeader = hexStringToByteArray("02");
                     Width = intToByte(4, i, true);//hexStringToByteArray("02000000");//intToByte(4,2,true);
                     //Height = intToByte(4, i, true);
                     ColTaSizeHeader = hexStringToByteArray("0A");
                    ColTaSize = intToByte(4, 2, true);//hexStringToByteArray("08000000");

                     ColTableHeader = hexStringToByteArray("0B");
                      ColTable = hexStringToByteArray("FFFFFFFFFFFFFFFF");


                      commentHeader = hexStringToByteArray("0C");
                      comment = hexStringToByteArray("0C48656C6C6F00");

                      Pixels = hexStringToByteArray("00010100");
                      AABytes = hexStringToByteArray("AA");
//                    comment = new byte[1800];
//                Arrays.fill(comment,(byte) 1);

                    os.write(magicNb);
                    os.write(version);
                    os.write(author);
                    os.write(WidthHeightHeader);
                    os.write(Width);
                    os.write(Width);
                    os.write(ColTaSizeHeader);
                    os.write(ColTaSize);
                    os.write(ColTableHeader);
                    os.write(ColTable);
                    os.write(commentHeader);
                    os.write(comment);
                os.write(Pixels);
//                Pixels = hexStringToByteArray("00");
//                for(int width= 0 ;width<32000;width++){
//                    for(int height= 0 ;height<1;height++){
//
//                        os.write(Pixels);
//
//                    }
//                    Pixels = hexStringToByteArray("01");
//                }
                    os.close();

                    try {//./converter_linux_x8664
                        String outputRealName = outputName + ".img";
                        executeCommandLine("./converter_linux_x8664 inputBenja" + inputNumber + ".img " + outputRealName, false, false, 10);
                    } catch (Exception e) {
                        System.out.println("Error in Exec Command");
                    }
                }
            //}
        } catch (IOException x) {
            System.err.println(x);
        }
    }


    public static void executeCommandLine(final String commandLine,
                                         final boolean printOutput,
                                         final boolean printError,
                                         final long timeout)
            throws IOException, InterruptedException, TimeoutException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(commandLine);
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));
        String line ;
        Boolean error =false;
        while ((line = stdInput.readLine ()) != null) {
            System.out.print(line);
            if(line.equals("*** The program has crashed ***")){
                error=true;
            }
        }
        Runtime runtime2 = Runtime.getRuntime();
        if (error) { //Program crashed
            executeCommand("mv  inputBenja" +inputNumber + ".img inputFile" + outputNumberFile + ".img");
            outputNumberFile++;
            System.out.print("On en a TROUVE 1 !!!!!!!!!!!!!!!");
        } else {
            //System.out.print("Pas de crash");
            runtime2.exec("rm " + outputName + ".img");
            //runtime2.exec("rm  inputBenja" +inputNumber +".img");
            //System.out.println("rm " + outputName + outputNumberFile + ".img");

        }
        if (!process.waitFor(timeout, TimeUnit.MINUTES)) {
            //timeout - kill the process.
            process.destroy(); // consider using destroyForcibly instead
        }
    }

    public static void executeCommand(final String commandLine){
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(commandLine);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return;
    }

public static byte[] intToByte(int size,int transformInt, boolean toLittleEndian){
    ByteBuffer byteBuffer = ByteBuffer.allocate(size);
    if (toLittleEndian){
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    byteBuffer.putInt(transformInt);
    byte[] bytes =byteBuffer.array();

//    for (byte b : bytes) {
//        System.out.format("0x%x ", b);
//    }
    return byteBuffer.array();
}

public static byte[] shortToByte(short transformShort, boolean toLittleEndian){
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        if (toLittleEndian){
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        byteBuffer.putShort(transformShort);
        return byteBuffer.array();
    }

    public static byte[] randomByteGenerator(int length) {
        Random rd = new Random();
        byte[] arr = new byte[length];
        rd.nextBytes(arr);
        return arr;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}
