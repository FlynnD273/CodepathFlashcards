package flynn.codepathflashcards;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
public class Flashcard implements Serializable
{
    Flashcard(String question, String[] answers)
    {
        setAnswers(answers);
        _correctIndex = 0;
        _question = question;
    }

    @Ignore
    Flashcard(String question, String[] answers, int correct)
    {
        setAnswers(answers);
        _correctIndex = correct;
        _question = question;
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "question")
    private String _question;

    public String getQuestion()
    {
        return _question;
    }

    @ColumnInfo(name = "answers")
    @TypeConverters({Converter.class})
    private String[] _answers;
    String[] getAnswers()
    {
        return _answers;
    }
    private void setAnswers ( String[] answer )
    {
        _answers = answer;
    }

    private int _correctIndex;
    public int getCorrectIndex()
    {
        return _correctIndex;
    }
    public void setCorrectIndex(int index)
    {
        _correctIndex = index;
    }

    public String getCorrectAnswer() {
        return getAnswers()[getCorrectIndex()];
    }

    public String[] randomize()
    {
        Integer[] intArray = new Integer[getAnswers().length];
        for(int i = 0; i < intArray.length; i++)
            intArray[i] = i;
        List<Integer> l = Arrays.asList(intArray);
        Collections.shuffle(l);
        intArray = (Integer[])l.toArray();
        String[] shuffledArray = new String[intArray.length];
        boolean first = true;
        for(int i = 0; i < l.size(); i++)
        {
            shuffledArray[i] = getAnswers()[intArray[i]];
            if(intArray[i] == _correctIndex && first)
            {
                _correctIndex = i;
                first = false;
            }
        }
        setAnswers(shuffledArray);

        return shuffledArray;
    }

/*
    //@PrimaryKey
    @NonNull
    @ColumnInfo(name = "uuid")
    private String uuid = "";

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }*/
}
