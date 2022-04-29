import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * ChatFilter
 *
 *
 * @author Phillip
 *
 * This is the filter so no bad words are sent. All filtered words are recorded in the "badwords.txt" file
 *
 * @version 4/26/2020
 */
public class ChatFilter {

    public ArrayList<String> arrayList = new ArrayList<>();
    private String badWordsFileName;

    public ChatFilter(String badWordsFileName) {
        this.badWordsFileName = badWordsFileName;
    }

    public String filter(String msg) {
        String[] message;

        message = msg.split(" ");
        String msg2 = "";

        try {
            File f = new File(badWordsFileName);

            FileReader fr = new FileReader(f);
            BufferedReader bfr = new BufferedReader(fr);

            String line;

            while (true) {
                line = bfr.readLine();
                if (line == null || line.equals("")) {
                    break;
                }
                String line2 = line.toLowerCase();
                arrayList.add(line2);
            }
            bfr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        int size;

        for (int i = 0; i < message.length; i++) {
            String msg3 = message[i].toLowerCase();

            if (arrayList.contains(msg3)) {
                size = message[i].length();
                message[i] = "";

                for (int j = 0; j < size; j++) {
                    message[i] = message[i] + "*";
                }
            }
            msg2 = msg2 + message[i];
            if (i != message.length - 1)
                msg2 = msg2 + " ";
        }


        return msg2;
    }

    public void printFile(String badWordsFileName1) {
        try {
            File f = new File(badWordsFileName1);

            FileReader fr = new FileReader(f);
            BufferedReader bfr = new BufferedReader(fr);

            String line;

            while (true) {
                line = bfr.readLine();
                if (line == null || line.equals("")) {
                    break;
                }
                System.out.print(line + "\n");
            }

            bfr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}