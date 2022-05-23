
package com.example.csbd.history;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HistoryService {

    public static String loadHistory(String nickName) {

        StringBuilder sb = new StringBuilder();
        int maxLines = 100;

        List<String> result = new ArrayList<>();

        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File("history_" + nickName + ".txt"), StandardCharsets.UTF_8)) {

            String line;
            while ((line = reader.readLine()) != null && result.size() < maxLines) {
                result.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String str :
                result) {
            sb.insert(0, maxLines-- + ") " + str + "\n");
        }
        return sb.toString();
    }

    public static boolean saveHistory(String nickName, String text) {

        File history = new File(("history_" + nickName + ".txt"));

        if (!history.exists()) {
            try {
                if (!history.createNewFile()) return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            String fileName = "history_" + nickName + ".txt";
            File userHistory = new File(fileName);

            PrintWriter fileWriter1 = new PrintWriter(new FileWriter(userHistory, StandardCharsets.UTF_8, true));

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter1);
            bufferedWriter.write(text);
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}


