
import hanquin.utils.Utils;

import static java.nio.file.StandardOpenOption.*;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Array;
import java.util.concurrent.TimeoutException;

public class main {
    static String outputName = "outputBenja";
    static int outputNumberFile = 1;

    public static void main(String[] args) {
        //Magic Number
        // Convert the string to a
        // byte array.
        String s = "Hello World! ";
        byte data[] ;
        Path p = Paths.get("./inputBenja.img");
        List<Integer> array= new LinkedList<Integer>();


    try
    {
        OutputStream os = new FileOutputStream("inputBenja.img");
        //Magic Number
        byte[] magicNb = hexStringToByteArray("ABCD");
        byte[] version = hexStringToByteArray("6400" );
        byte[] author = hexStringToByteArray("0152616D696E00" );
        byte[] WidthHeight = hexStringToByteArray("020200000002000000" );
        byte[] ColTaSize= hexStringToByteArray("0A02000000" );
        byte[] ColTable = hexStringToByteArray("0B00000000FFFFFF00");
        byte[] comment= hexStringToByteArray("0C48656C6C6F00" );
        byte[] Pixels= hexStringToByteArray("00010100" );


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
            String outputRealName = outputName + outputNumberFile+ ".img";
            executeCommandLine("./converter_linux_x8664 inputBenja.img "+outputRealName,false,false,10000);
        }catch (Exception e){
            System.out.println("Error in Exec Command");
        }
    }catch(IOException x) {
        System.err.println(x);
    }

    }



    public static int executeCommandLine(final String commandLine,
                                         final boolean printOutput,
                                         final boolean printError,
                                         final long timeout)
            throws IOException, InterruptedException, TimeoutException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(commandLine);
        /* Set up process I/O. */
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(process.getErrorStream()));
        //TODO
        if(stdInput.readLine().length() > 1){ //Program crashed
            System.out.println(stdInput.readLine());
            outputNumberFile++;
        }else{
            Runtime runtime2 = Runtime.getRuntime();
            Process process2 = runtime2.exec("rm " +outputName+outputNumberFile+".img");
        }
        Worker worker = new Worker(process);
        worker.start();
        try {
            worker.join(timeout);
            if (worker.exit != null)
                return worker.exit;
            else
                throw new TimeoutException();
        } catch(InterruptedException ex) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            process.destroyForcibly();
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



    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

}
