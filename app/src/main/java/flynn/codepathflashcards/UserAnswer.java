package flynn.codepathflashcards;

public class UserAnswer
{
    private String correct_answer;
    public String getCorrectAnswer()
    {
        return correct_answer;
    }
    public void setCorrectAnswer ( String answer )
    {
        correct_answer = answer;
    }

    private String user_answer;
    public String getUserAnswer()
    {
        return user_answer;
    }
    public void setUserAnswer ( String answer )
    {
        user_answer = answer;
    }

    private boolean is_corrcect;
    public boolean isCorrect()
    {
        return is_corrcect;
    }
    public void setIsCorrect ( boolean correct )
    {
        is_corrcect = correct;
    }

    public UserAnswer(String correct_answer, String user_answer, boolean is_correct) {
        this.correct_answer = correct_answer;
        this.user_answer = user_answer;
        this.is_corrcect = is_correct;
    }
}
