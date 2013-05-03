package ru.cardio.web.json.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import ru.cardio.json.additionals.JsonResponse;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class SecureResponseWrapper {

    private static Gson gson;
    private static TypeAdapter<Date> DateTypeAdapter = new TypeAdapter<Date>() {

        @Override
        public void write(JsonWriter out, Date value)
                throws IOException {
            System.out.println("write");
            if (value == null) {
                System.out.println("value is null -> out.value('')");
                out.value("");
                return;
            }
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            out.value(dateFormat.format(value));
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            System.out.println("read");
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                String result = in.nextString();
                if ("".equals(result)) {
                    return null;
                }
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    return dateFormat.parse(result);

                    //                return Long.parseLong(result);
                } catch (ParseException ex) {
                    return null;
                }
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }
    };

    static {
        gson = new GsonBuilder().registerTypeAdapter(long.class, DateTypeAdapter).setDateFormat("MM/dd/yyyy").create();
    }

    public static String getJsonResponse(JsonResponse resp) {
        return gson.toJson(resp);
    }
}
