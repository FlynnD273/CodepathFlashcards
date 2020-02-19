package flynn.codepathflashcards;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Flashcard
{
    Flashcard(String question, String[] answers, int correct)
    {
        setAnswers(answers);
        _correct = correct;
        _question = question;
    }

    private String _question;
    String getQuestion()
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
    int getCorrect()
    {
        return _correct;
    }

    String[] randomize()
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
