
import hanquin.utils.Utils;

import static java.nio.file.StandardOpenOption.*;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Array;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.Random;

public class main {
    static String outputName = "outputBenja";
    static int outputNumberFile = 1;

    public static void main(String[] args) {
        //Magic Number
        // Convert the string to a
        // byte array.
        String s = "Hello World! ";
        byte data[];
        Path p = Paths.get("./inputBenja.img");
        List<Integer> array = new LinkedList<Integer>();


        try {
            OutputStream os = new FileOutputStream("inputBenja.img");
            //Magic Number
            byte[] magicNb = hexStringToByteArray("ABCD");

            byte[] version = hexStringToByteArray("6400");//
            byte[] author = hexStringToByteArray("0152616D696E00");
            byte[] WidthHeight = hexStringToByteArray("020200000000000000");
            byte[] ColTaSize = hexStringToByteArray("0A02000000");
            byte[] ColTable = hexStringToByteArray("0B00000000FFFFFF00");
            byte[] comment = randomByteGenerator(1300);//hexStringToByteArray("0C48656C6C6F00");
            byte[] Pixels = hexStringToByteArray("00010100");


            os.write(magicNb);
            os.write(version);
            os.write(author);
            os.write(WidthHeight);
            os.write(ColTaSize);
            os.write(ColTable);
            os.write(comment);
            os.write(Pixels);


            os.close();
            try {//./converter_linux_x8664
                String outputRealName = outputName + outputNumberFile + ".img";
                executeCommandLine("./converter_linux_x8664 inputBenja.img " + outputRealName, false, false, 1);
            } catch (Exception e) {
                System.out.println("Error in Exec Command");
            }
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
            System.out.println(line);
            if(line.equals("*** The program has crashed ***")){
                error=true;
            }
        }
        Runtime runtime2 = Runtime.getRuntime();
        if (error) { //Program crashed
            Process process2 = runtime2.exec("mv  inputBenja.img inputFile" + outputNumberFile + ".img");
            outputNumberFile++;
            System.out.println("On en a TROUVE 1 !!!!!!!!!!!!!!!");
        } else {
            runtime2.exec("rm " + outputName + outputNumberFile + ".img");
            //runtime2.exec("rm  inputBenja.img");
            System.out.println("rm " + outputName + outputNumberFile + ".img");

        }
        if (!process.waitFor(timeout, TimeUnit.MINUTES)) {
            //timeout - kill the process.
            process.destroy(); // consider using destroyForcibly instead
        }
    }

    private static class Worker extends Thread {
        private final Process process;
        private Integer exit;

        private Worker(Process process) {
            this.process = process;
        }

        public void run() {
            try {
                exit = process.waitFor();
            } catch (InterruptedException ignore) {
                return;
            }
        }
    }

    public static byte[] randomByteGenerator(int length) {
        String TotHex = "";
        int actualLength = length;
        Random random = new Random();
        do {
            int maxLength;
            if(actualLength>4){maxLength = 4; } else {maxLength=actualLength;}
            int val = random.nextInt((int) Math.pow(16, maxLength * 2));
            String Hex = Integer.toHexString(val);
            if (Hex.length() % 2 == 1) {
                Hex = "0" + Hex;
            }
            actualLength -= 4; //Int allow max 4 bytes
            TotHex += Hex;
        } while (TotHex.length() != length * 2);
        System.out.println(TotHex);
        return hexStringToByteArray(TotHex);
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
