package flynn.codepathflashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    Drawable rounded_drawable;
    Resources res;

    Button button_next;

    TextView flashcard_question;

    Button answer0;
    Button answer1;
    Button answer2;

    Button[] answer_views;

    String[] questions;
    String[] answers;

    int answer_num = -1;
    Integer flashcard_number = 0;
    boolean answered = false;
    boolean show_answers = true;
    Integer correct_answers = 0;

    Boolean end_screen = false;

    Flashcard flashcard;

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

            if (answer_num == flashcard.getCorrect())
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredRight));
                correct_answers++;
            }
            else
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredWrong));
                for(int i = 0; i < answer_views.length; i++)
                {
                    answer_views[flashcard.getCorrect()].setBackgroundColor(res.getColor(R.color.colorAnswerRight));
                }
            }

            answered = true;
            button_next.setVisibility(Button.VISIBLE);
        }
    }

    public void nextQuestion(View v)
    {
        if(flashcard_number + 1 == questions.length)
        {
            flashcard_question.setText(getString(R.string.endText, correct_answers.toString(), String.valueOf(questions.length)));
            rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
            flashcard_question.setBackground(rounded_drawable);

            for (Button answer_view : answer_views) {
                answer_view.setVisibility(Button.INVISIBLE);
            }

            button_next.setText(getString(R.string.Restart));
            flashcard_number++;
            end_screen = true;
        }
        else if (end_screen)
        {
            correct_answers = 0;
            flashcard_number = 0;
            displayFlashcard(loadFlashcard(flashcard_number), true);
            end_screen = false;
        }
        else
        {
            flashcard = loadFlashcard(++flashcard_number);
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
        button_next.setVisibility(Button.INVISIBLE);

        if(randomize)
            card.randomize();
        for (int i = 0; i < answer_views.length; i++) {
            answer_views[i].setBackgroundColor(res.getColor(R.color.colorWhite));
            answer_views[i].setVisibility(Button.VISIBLE);
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
            ((ImageView)findViewById(R.id.visibility_icon)).setImageResource(R.drawable.eye_hide);
            for (Button answer_view : answer_views)
            {
                answer_view.setVisibility(Button.VISIBLE);
            }
        }
        else
        {
            ((ImageView)findViewById(R.id.visibility_icon)).setImageResource(R.drawable.eye_show);
            for (Button answer_view : answer_views)
            {
                answer_view.setVisibility(Button.INVISIBLE);
            }
        }
    }

    public void updateLayoutState()
    {
        if(end_screen)
        {
            flashcard_question.setText(getString(R.string.endText, correct_answers.toString(), String.valueOf(questions.length)));
            rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
            flashcard_question.setBackground(rounded_drawable);

            show_answers = false;
            updateVisibility();

            button_next.setVisibility(Button.VISIBLE);
            button_next.setText(getString(R.string.Restart));
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

            button_next.setVisibility(Button.VISIBLE);
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
        outState.putInt("answer_num", answer_num);
        outState.putBoolean("answered", answered);
        outState.putInt("flashcard_number", flashcard_number);
        outState.putBoolean("show_answers", show_answers);
        outState.putInt("correct_answers", correct_answers);
        outState.putString("flashcard_question", flashcard.getQuestion());
        outState.putStringArray("flashcard_answers", flashcard.getAnswers());
        outState.putInt("flashcard_correct", flashcard.getCorrect());
        outState.putBoolean("end_screen", end_screen);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        answer_num = savedInstanceState.getInt("answer_num");
        answered = savedInstanceState.getBoolean("answered");
        flashcard_number = savedInstanceState.getInt("flashcard_number");
        correct_answers = savedInstanceState.getInt("correct_answers");
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
        res = getResources();

        flashcard_question = findViewById(R.id.flashcard_content);

        answer0 = findViewById(R.id.answer0);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer_views = new Button[]{answer0, answer1, answer2};


        rounded_drawable = res.getDrawable(R.drawable.round_corner);

        questions = res.getStringArray(R.array.questions);
        answers = res.getStringArray(R.array.answers);



        button_next = findViewById(R.id.next_button);


        for(int i = 0; i < 3; i++)
        {
            answer_views[i].setBackgroundColor(res.getColor(R.color.colorWhite));
        }


        displayFlashcard(loadFlashcard(flashcard_number), true);
    }
}
