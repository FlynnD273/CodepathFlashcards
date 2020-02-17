package flynn.codepathflashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

    String correct_answer;
    Integer flashcard_number = 0;
    boolean answered = false;
    boolean show_answers = true;
    Integer correct_answers = 0;

    Flashcard flashcard;

    public void answer_OnClick(View v)
    {
        Button tv = (Button) v;
        if(!answered) {
            flashcard_question.setText(flashcard.getAnswers()[flashcard.getCorrect()]);
            rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
            flashcard_question.setBackground(rounded_drawable);

            int answer_num = -1;
            for(int i = 0; i < answer_views.length;i++)
            {
                if(answer_views[i] == tv)
                {
                    answer_num = i;
                    break;
                }
            }


            if (answer_num == flashcard.getCorrect())
            {
                tv.setBackgroundColor(res.getColor(R.color.colorAnsweredRight));
                correct_answers++;
            }
            else
            {
                tv.setBackgroundColor(res.getColor(R.color.colorAnsweredWrong));
                for(int i = 0; i < 3; i++)
                {
                    answer_views[flashcard.getCorrect()].setBackgroundColor(res.getColor(R.color.colorAnswerRight));
                }
            }

            answered = true;
            button_next.setVisibility(Button.VISIBLE);
        }
    }

    public void next_question(View v)
    {
        if(flashcard_number + 1 == questions.length)
        {
            flashcard_question.setText(getString(R.string.endText, correct_answers.toString(), String.valueOf(questions.length)));
            rounded_drawable.setColorFilter(res.getColor(R.color.colorAnswered), PorterDuff.Mode.SRC_ATOP);
            flashcard_question.setBackground(rounded_drawable);

            for (int i = 0; i < 3; i++) {
                answer_views[i].setVisibility(Button.INVISIBLE);
            }

            button_next.setText(getString(R.string.Restart));
            flashcard_number++;
        }
        else if (flashcard_number == questions.length)
        {
            correct_answers = 0;
            flashcard_number = 0;
            display_flashcard(load_flashcard(flashcard_number));
        }
        else
        {
            display_flashcard(load_flashcard(++flashcard_number));
        }
    }

    public Flashcard load_flashcard (int card_number)
    {
        flashcard = new Flashcard(questions[card_number], answers[card_number].split(";"), 0);
        return flashcard;
    }

    public void display_flashcard(Flashcard card)
    {
        ((ImageView)findViewById(R.id.visibility_icon)).setImageResource(R.drawable.eye_hide);
        show_answers = true;
        flashcard_question.setText(card.getQuestion());
        rounded_drawable.setColorFilter(res.getColor(R.color.colorQuestion), PorterDuff.Mode.SRC_ATOP);
        flashcard_question.setBackground(rounded_drawable);

        button_next.setText(getString(R.string.NextQuestion));
        answered = false;
        button_next.setVisibility(Button.INVISIBLE);

        card.randomize();
        for (int i = 0; i < 3; i++) {
            answer_views[i].setBackgroundColor(res.getColor(R.color.colorWhite));
            answer_views[i].setVisibility(Button.VISIBLE);
            answer_views[i].setText(card.getAnswers()[i]);
        }
    }

    public void toggle_answers(View v)
    {
        if(show_answers)
        {
            ((ImageView)findViewById(R.id.visibility_icon)).setImageResource(R.drawable.eye_show);
            for (Button answer_view : answer_views)
            {
                answer_view.setVisibility(Button.INVISIBLE);
            }
        }
        else
        {
            ((ImageView)findViewById(R.id.visibility_icon)).setImageResource(R.drawable.eye_hide);
            for (Button answer_view : answer_views)
            {
                answer_view.setVisibility(Button.VISIBLE);
            }
        }

        show_answers = !show_answers;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = getResources();

        flashcard_question = findViewById(R.id.flashcard_content);
        answer0 = findViewById(R.id.answer0);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);

        rounded_drawable = res.getDrawable(R.drawable.round_corner);

        questions = res.getStringArray(R.array.questions);
        answers = res.getStringArray(R.array.answers);



        button_next = findViewById(R.id.nextButton);

        answer_views = new Button[]{answer0, answer1, answer2};

        for(int i = 0; i < 3; i++)
        {
            answer_views[i].setBackgroundColor(res.getColor(R.color.colorWhite));
        }

        display_flashcard(load_flashcard(flashcard_number));
    }
}
