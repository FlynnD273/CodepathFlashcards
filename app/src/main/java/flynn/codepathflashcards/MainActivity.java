package flynn.codepathflashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
{
    Drawable rounded_drawable;
    Resources res;

    TextView flashcard_question;

    Button button_next;
    Button[] answer_views;

    ArrayList<Flashcard> flashcards;

    ArrayList<UserAnswer> user_answers;

    int answer_num = -1;
    int flashcard_number = 0;

    boolean end_screen = false;
    boolean answered = false;
    boolean show_answers = true;

    Flashcard current_flashcard;

    public void reset_flashcards()
    {
        String[] questions = res.getStringArray(R.array.questions);
        String[] answers = res.getStringArray(R.array.answers);
        flashcards = new ArrayList<Flashcard>();


        for(int i = 0; i < 1; i++)//answers.length; i++)
        {
            flashcards.add(new Flashcard(questions[i], answers[i].split(";")));
        }
    }

    public void resetFlashcardApp()
    {
        flashcard_question = findViewById(R.id.flashcard_content);

        answer_views = new Button[]{findViewById(R.id.answer0), findViewById(R.id.answer1), findViewById(R.id.answer2)};
        for (Button answer_view : answer_views) {
            answer_view.setBackgroundColor(res.getColor(R.color.colorWhite));
        }

        rounded_drawable = res.getDrawable(R.drawable.round_corner);
        button_next = findViewById(R.id.next_button);

        user_answers = new ArrayList<>();
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

            while(user_answers.size() <= flashcard_number)
                user_answers.add(new UserAnswer("", "", false));

            flashcard_question.setText(current_flashcard.getAnswers()[current_flashcard.getCorrectIndex()]);
            rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
            flashcard_question.setBackground(rounded_drawable);
            user_answers.set(flashcard_number, new UserAnswer(current_flashcard.getCorrectAnswer(), current_flashcard.getAnswers()[answer_num], false));

            if (answer_num == current_flashcard.getCorrectIndex())
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredRight));
                user_answers.get(flashcard_number).setIsCorrect(true);
            }
            else
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredWrong));
                answer_views[current_flashcard.getCorrectIndex()].setBackgroundColor(res.getColor(R.color.colorAnswerRight));
                user_answers.get(flashcard_number).setIsCorrect(false);
            }

            answered = true;
            button_next.setVisibility(VISIBLE);
        }
    }

    public void nextQuestion(View v)
    {
        flashcard_number++;
        if(flashcard_number == flashcards.size())
        {
            displayEndScreen();
        }
        else if (end_screen)
        {
            resetFlashcardApp();
        }
        else
        {
            current_flashcard = loadFlashcard(flashcard_number);
            displayFlashcard(current_flashcard, true);
        }
    }

    public Flashcard loadFlashcard(int card_number)
    {
        current_flashcard = flashcards.get(flashcard_number);
        return current_flashcard;
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
        if(flashcard_number < flashcards.size())
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
        end_screen = true;
        int num_correct_answers = 0;
        for(UserAnswer userAnswer : user_answers)
        {
            if(userAnswer.isCorrect())
                num_correct_answers++;
        }

        flashcard_question.setText(getString(R.string.endText, String.valueOf(num_correct_answers), String.valueOf(flashcards.size())));
        rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
        flashcard_question.setBackground(rounded_drawable);

        show_answers = false;
        updateVisibility();

        button_next.setVisibility(VISIBLE);
        button_next.setText(getString(R.string.Restart));
    }

    public void updateLayoutState()
    {
        if(end_screen)
        {
            displayEndScreen();
        }
        else if(answered)
        {
            displayFlashcard(current_flashcard, false);
            answered = true;
            flashcard_question.setText(current_flashcard.getAnswers()[current_flashcard.getCorrectIndex()]);
            rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
            flashcard_question.setBackground(rounded_drawable);

            if (answer_num == current_flashcard.getCorrectIndex())
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredRight));
            }
            else
            {
                answer_views[answer_num].setBackgroundColor(res.getColor(R.color.colorAnsweredWrong));
                for(int i = 0; i < answer_views.length; i++)
                {
                    answer_views[current_flashcard.getCorrectIndex()].setBackgroundColor(res.getColor(R.color.colorAnswerRight));
                }
            }

            button_next.setVisibility(VISIBLE);
        }
        else
        {
            displayFlashcard(current_flashcard, false);
        }
    }

    public void addFlashcardLayout(View v)
    {
        Intent intent = new Intent(MainActivity.this, AddFlashcardActivity.class);
        MainActivity.this.startActivityForResult(intent, 0);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of item position
        outState.putInt("flashcard_number", flashcard_number);
        outState.putInt("answer_num", answer_num);
        outState.putBoolean("answered", answered);
        outState.putBoolean("end_screen", end_screen);
        outState.putBoolean("show_answers", show_answers);
        outState.putSerializable("flashcard", current_flashcard);
        outState.putSerializable("user_answers", user_answers);
        outState.putSerializable("flashcards", flashcards);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 0)
        {
            flashcards.add((Flashcard) data.getExtras().getSerializable("flashcards"));
            //flashcards.add(new Flashcard("qwe", new String[] { "0", "1", "2" }));
        }
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        answer_num = savedInstanceState.getInt("answer_num");
        flashcard_number = savedInstanceState.getInt("flashcard_number");
        user_answers = (ArrayList<UserAnswer>) savedInstanceState.getSerializable("user_answers");
        answered = savedInstanceState.getBoolean("answered");
        end_screen = savedInstanceState.getBoolean("end_screen");
        current_flashcard = (Flashcard)savedInstanceState.getSerializable("flashcard");
        flashcards = (ArrayList<Flashcard>)savedInstanceState.getSerializable("flashcards");

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

        reset_flashcards();
        resetFlashcardApp();
    }
}
