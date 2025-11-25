import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;



public class InputOutputTest {
    String inputFile= "utfFile.txt";
    @Test
    public void saveFile() {
        String data = "Example - ";
        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS "));
        boolean append = false;

        try (OutputStream os = new FileOutputStream(inputFile, append);
            OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_16LE)) {
            writer.write(data + formatted);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readCharsetFile() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(inputFile), StandardCharsets.UTF_16)) {
            stream.forEach(System.out::println);
        }
    }

    @Test
    public void readAnyEncodingFile()  {
        try(
            FileInputStream fis = new FileInputStream(inputFile)){
        //InputStream  is= new FileInputStream(fis, StandardCharsets.UTF_16)){

            int b;
            while ((b = fis.read()) != -1) {   // read() returns int 0-255, -1 = EOF
                String hex = Integer.toHexString(b);

                if (hex.equals("0")) {
                   continue;
                }
                System.out.printf("%c",b);
            //    System.out.printf("%02X ", b); // print as hex
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void ReadUtf16WithBom () throws Exception {
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            // Read first two bytes (the BOM)
            int b1 = fis.read();
            int b2 = fis.read();

            String encoding;
            if (b1 == 0xFE && b2 == 0xFF)
                encoding = "UTF-16BE";
            else if (b1 == 0xFF && b2 == 0xFE)
                encoding = "UTF-16LE";
            else {
                encoding = "UTF-16"; // default (will guess)
                try(FileChannel fileChannel = fis.getChannel()) {
                    fileChannel.position(0); // rewind if no BOM
                }
            }

            try (BufferedReader br =
                         new BufferedReader(new InputStreamReader(fis, encoding))) {
                String line;
                while ((line = br.readLine()) != null)
                    System.out.println(line);
            }
        }
    }

        @Test
        public  void detectBom(){

            String filePath =inputFile;
            try (FileInputStream fis = new FileInputStream(filePath)) {

                // Read first 4 bytes (max BOM length)
                byte[] bom = new byte[4];
                int n = fis.read(bom, 0, 4);

                String encoding;
                int skipBytes;

                if (n >= 4 && (bom[0] & 0xFF) == 0x00 && (bom[1] & 0xFF) == 0x00
                        && (bom[2] & 0xFF) == 0xFE && (bom[3] & 0xFF) == 0xFF) {
                    encoding = "UTF-32BE";
                    skipBytes = 4;
                }
                else if (n >= 4 && (bom[0] & 0xFF) == 0xFF && (bom[1] & 0xFF) == 0xFE
                        && (bom[2] & 0xFF) == 0x00 && (bom[3] & 0xFF) == 0x00) {
                    encoding = "UTF-32LE";
                    skipBytes = 4;
                }
                else if (n >= 3 && (bom[0] & 0xFF) == 0xEF && (bom[1] & 0xFF) == 0xBB
                        && (bom[2] & 0xFF) == 0xBF) {
                    encoding = "UTF-8";
                    skipBytes = 3;
                }
                else if (n >= 2 && (bom[0] & 0xFF) == 0xFE && (bom[1] & 0xFF) == 0xFF) {
                    encoding = "UTF-16BE";
                    skipBytes = 2;
                }
                else if (n >= 2 && (bom[0] & 0xFF) == 0xFF && (bom[1] & 0xFF) == 0xFE) {
                    encoding = "UTF-16LE";
                    skipBytes = 2;
                }
                else {
                    encoding = "No BOM (unknown or UTF-8 without BOM)";
                    skipBytes = 0;
                }

                System.out.println("Detected: " + encoding);
                System.out.println("Bytes to skip: " + skipBytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



