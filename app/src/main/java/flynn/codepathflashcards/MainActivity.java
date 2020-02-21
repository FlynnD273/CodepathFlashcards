package flynn.codepathflashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
{
    Drawable rounded_drawable;
    Resources res;

    TextView flashcard_question;

    Button button_next;
    Button[] answer_views;

    String[] questions;
    String[] answers;

    UserAnswer[] user_answers;

    int answer_num = -1;
    int flashcard_number = 0;

    boolean end_screen = false;
    boolean answered = false;
    boolean show_answers = true;

    Flashcard flashcard;

    public void resetFlashcardApp()
    {
        res = getResources();


        flashcard_question = findViewById(R.id.flashcard_content);

        answer_views = new Button[]{findViewById(R.id.answer0), findViewById(R.id.answer1), findViewById(R.id.answer2)};
        for (Button answer_view : answer_views) {
            answer_view.setBackgroundColor(res.getColor(R.color.colorWhite));
        }

        rounded_drawable = res.getDrawable(R.drawable.round_corner);
        button_next = findViewById(R.id.next_button);

        questions = res.getStringArray(R.array.questions);
        answers = res.getStringArray(R.array.answers);
        user_answers = new UserAnswer[questions.length];
        flashcard_number = 0;
        end_screen = false;

        displayFlashcard(loadFlashcard(flashcard_number), true);
        updateVisibility();
    }

    public void answerOnClick(View v)
    {
        if(!answered)
        {
            Button tv = (Button) v;

            answer_num = 0;
            for(int i = 0; i < answer_views.length;i++)
            {
                if(answer_views[i] == tv)
                {
                    answer_num = i;
                    break;
                }
            }

            flashcard_question.setText(flashcard.getAnswers()[flashcard.getCorrect()]);
            rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
            //flashcard_question.setBackground(rounded_drawable);
            user_answers[flashcard_number] = new UserAnswer(answers[flashcard.getCorrect()], answers[answer_num], false);

            if (answer_num == flashcard.getCorrect())
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredRight));
                user_answers[flashcard_number].setIsCorrect(true);
            }
            else
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredWrong));
                answer_views[flashcard.getCorrect()].setBackgroundColor(res.getColor(R.color.colorAnswerRight));
                user_answers[flashcard_number].setIsCorrect(false);
            }

            answered = true;
            button_next.setVisibility(VISIBLE);
        }
    }

    public void nextQuestion(View v)
    {
        flashcard_number++;
        if(flashcard_number == questions.length)
        {
            displayEndScreen();
        }
        else if (end_screen)
        {
            resetFlashcardApp();
        }
        else
        {
            flashcard = loadFlashcard(flashcard_number);
            displayFlashcard(flashcard, true);
        }
    }

    public Flashcard loadFlashcard(int card_number)
    {
        flashcard = new Flashcard(questions[card_number], answers[card_number].split(";"), 0);
        return flashcard;
    }

    public void displayFlashcard(Flashcard card, boolean randomize)
    {
        ((ImageView)findViewById(R.id.visibility_icon)).setImageResource(R.drawable.eye_hide);
        show_answers = true;
        flashcard_question.setText(card.getQuestion());
        rounded_drawable.setColorFilter(res.getColor(R.color.colorQuestion), PorterDuff.Mode.SRC_ATOP);
        flashcard_question.setBackground(rounded_drawable);

        button_next.setText(getString(R.string.NextQuestion));
        answered = false;
        button_next.setVisibility(INVISIBLE);

        if(randomize)
            card.randomize();
        for (int i = 0; i < answer_views.length; i++) {
            answer_views[i].setBackgroundColor(res.getColor(R.color.colorWhite));
            answer_views[i].setVisibility(VISIBLE);
            answer_views[i].setText(card.getAnswers()[i]);
        }
    }

    public void toggleAnswers(View v)
    {
        if(flashcard_number < questions.length)
        {
            show_answers = !show_answers;
            updateVisibility();
        }
    }

    public void updateVisibility()
    {
        if(show_answers && !end_screen)
        {
            ((ImageView)findViewById(R.id.visibility_icon)).setVisibility(VISIBLE);
            ((ImageView)findViewById(R.id.visibility_icon)).setImageResource(R.drawable.eye_hide);
            for (Button answer_view : answer_views)
            {
                answer_view.setVisibility(VISIBLE);
            }
        }
        else
        {
            if(end_screen)
                ((ImageView) findViewById(R.id.visibility_icon)).setVisibility(INVISIBLE);

            ((ImageView)findViewById(R.id.visibility_icon)).setImageResource(R.drawable.eye_show);

            for (Button answer_view : answer_views)
                answer_view.setVisibility(INVISIBLE);
        }
    }

    public void displayEndScreen()
    {
        int num_correct_answers = 0;
        for(UserAnswer userAnswer : user_answers)
        {
            if(userAnswer.isCorrect())
                num_correct_answers++;
        }

        flashcard_question.setText(getString(R.string.endText, String.valueOf(num_correct_answers), String.valueOf(questions.length)));

        show_answers = false;
        updateVisibility();

        button_next.setVisibility(VISIBLE);
        button_next.setText(getString(R.string.Restart));

        end_screen = true;
    }

    public void updateLayoutState()
    {
        if(end_screen)
        {
            displayEndScreen();
        }
        else if(answered)
        {
            displayFlashcard(flashcard, false);
            answered = true;
            flashcard_question.setText(flashcard.getAnswers()[flashcard.getCorrect()]);
            rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
            flashcard_question.setBackground(rounded_drawable);

            if (answer_num == flashcard.getCorrect())
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredRight));
            }
            else
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredWrong));
                for(int i = 0; i < answer_views.length; i++)
                {
                    answer_views[flashcard.getCorrect()].setBackgroundColor(res.getColor(R.color.colorAnswerRight));
                }
            }

            button_next.setVisibility(VISIBLE);
        }
        else
        {
            displayFlashcard(flashcard, false);
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of item position
        outState.putInt("flashcard_number", flashcard_number);
        outState.putInt("flashcard_correct", flashcard.getCorrect());
        outState.putInt("answer_num", answer_num);
        outState.putBoolean("answered", answered);
        outState.putBoolean("end_screen", end_screen);
        outState.putBoolean("show_answers", show_answers);
        outState.putString("flashcard_question", flashcard.getQuestion());
        outState.putStringArray("flashcard_answers", flashcard.getAnswers());
        outState.putSerializable("user_answers", user_answers);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        answer_num = savedInstanceState.getInt("answer_num");
        flashcard_number = savedInstanceState.getInt("flashcard_number");
        user_answers = (UserAnswer[]) savedInstanceState.getSerializable("user_answers");
        answered = savedInstanceState.getBoolean("answered");
        end_screen = savedInstanceState.getBoolean("end_screen");
        flashcard = new Flashcard(savedInstanceState.getString("flashcard_question"), savedInstanceState.getStringArray("flashcard_answers"), savedInstanceState.getInt("flashcard_correct"));

        updateLayoutState();
        show_answers = savedInstanceState.getBoolean("show_answers");
        updateVisibility();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetFlashcardApp();
    }
}
