package ru.cardio.helpers;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.cardio.core.jpa.entity.User;
import ru.cardio.core.jpa.entity.UserCard;
import ru.cardio.exceptions.CardioException;
import ru.cardio.json.additionals.ResponseConstants;
import ru.cardio.json.entity.SimpleUser;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class UserHelper {

    private static Gson gson;
    private static TypeAdapter<Date> DateTypeAdapter = new TypeAdapter<Date>() {

        @Override
        public void write(JsonWriter out, Date value)
                throws IOException {
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

    public static String getJsonFromSimpleUser(SimpleUser su) {
        String s = gson.toJson(su);
        System.out.println("getJsonFromSimpleUser: s = " + s);
        return s;
    }

    public static SimpleUser getSimpleUserFromJson(String json) throws CardioException {
        System.out.println("getSimpleUserFromJson: json = " + json);
        try {
            return gson.fromJson(json, SimpleUser.class);
        } catch (Exception e) {
            throw new CardioException(e.getMessage());
        }
    }

    public static UserCard getUserCardFromSimpleUser(SimpleUser su) throws CardioException {
        if (su == null) {
            throw new CardioException("user is not specified", ResponseConstants.NORMAL_ERROR_CODE);
        }
        UserCard uc = new UserCard(su.getFirstName(), su.getLastName(), su.getDescription(), su.getDiagnosis(), su.getAbout());
        return uc;
    }

    /**
     * specifing just FIRSNAME, LASTNAME, DESCRIPTION, DIAGNOSIS, ABOUT
     *
     * @param json
     * @return
     * @throws CardioException
     */
    public static UserCard getUserCardFromSimpleUserJson(String json) throws CardioException {
        SimpleUser su = getSimpleUserFromJson(json);
        return getUserCardFromSimpleUser(su);
    }

    public static User getUserFromSimpleUser(SimpleUser su) throws CardioException {
        if (su == null) {
            throw new CardioException("user is not specified", ResponseConstants.NORMAL_ERROR_CODE);
        }
        User u = new User(su.getEmail(), su.getPassword(), su.getFirstName(), su.getLastName(), su.getDepartment(), su.getStatusMessage());
        return u;
    }

    /**
     * specifing just EMAIL, PASSWORD, FIRSNAME, LASTNAME, DEPARTMENT
     *
     * @param json
     * @return
     * @throws CardioException
     */
    public static User getUserFromSimpleUserJson(String json) throws CardioException {
        SimpleUser su = getSimpleUserFromJson(json);
        return getUserFromSimpleUser(su);
    }
}
