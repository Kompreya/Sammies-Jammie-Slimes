package kombee.sammiejamslimes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;

public class JSONFileLoader {
    private static final Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();

    public static SammieJamSlimeData[] loadSlimeData(String filePath) {
        try {
            FileReader fileReader = new FileReader(filePath);
            JsonArray jsonArray = jsonParser.parse(fileReader).getAsJsonArray();

            SammieJamSlimeData[] slimeDataArray = new SammieJamSlimeData[jsonArray.size()];

            int i = 0;
            for (JsonElement element : jsonArray) {
                slimeDataArray[i] = gson.fromJson(element, SammieJamSlimeData.class);
                i++;
            }

            return slimeDataArray;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
