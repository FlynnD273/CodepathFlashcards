package flynn.codepathflashcards;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converter
{
        @TypeConverter
        public String[] stringToStringArray(String value) {
            List<String> langs = Arrays.asList(value.split("\n"));
            return (String[])langs.toArray();
        }

        @TypeConverter
        public String stringArrayToString(String[] cl) {
            StringBuilder value = new StringBuilder();

            for (String string :cl)
                value.append(string).append("\n");

            value.setLength(value.length()-1);

            return value.toString();
        }
}
