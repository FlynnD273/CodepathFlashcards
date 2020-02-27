package flynn.codepathflashcards;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Flashcard implements Serializable
{
    Flashcard(String question, String[] answers)
    {
        setAnswers(answers);
        _correct = 0;
        _question = question;
    }

    Flashcard(String question, String[] answers, int correct)
    {
        setAnswers(answers);
        _correct = correct;
        _question = question;
    }

    private String _question;

    public String getQuestion()
    {
        return _question;
    }

    private String[] _answers;
    String[] getAnswers()
    {
        return _answers;
    }
    private void setAnswers ( String[] answer )
    {
        _answers = answer;
    }

    private int _correct;
    int getCorrectIndex()
    {
        return _correct;
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
        String[] shuffledArray = new String[intArray.length];
        for(int i = 0; i < l.size(); i++)
        {
            shuffledArray[i] = getAnswers()[intArray[i]];
            if(intArray[i] == 0)
                _correct = i;
        }
        setAnswers(shuffledArray);

        return shuffledArray;
    }
}
