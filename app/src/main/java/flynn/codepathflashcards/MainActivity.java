package flynn.codepathflashcards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class MainActivity extends AppCompatActivity
{
    Drawable question_drawable;
    Resources res;

    Button btn;

    TextView flashcard_question;

    Button answer0;
    Button answer1;
    Button answer2;

    String[] questions;
    String[] answers;

    String[] possible_answers;
    String correct_answer;
    int flashcard_number = 0;
    boolean answered = false;
    Random r = new Random();

    void setDrawableColor(Drawable d, int c)
    {
        d.setColorFilter(new PorterDuffColorFilter(c, PorterDuff.Mode.SRC_IN));
    }

    public void answer_OnClick(View v)
    {
        Button tv = (Button) v;
        if(!answered)
        {
            flashcard_question.setText(correct_answer);
            if(correct_answer.equals(tv.getText().toString()))
                setDrawableColor(question_drawable, res.getColor(R.color.colorAnswerRight));
            else
                setDrawableColor(question_drawable, res.getColor(R.color.colorAnswerWrong));
            answered = true;

            btn.setVisibility(Button.VISIBLE);
        }
    };

    public void next_question(View v)
    {
        if(++flashcard_number == questions.length)
            flashcard_number = 0;
        flashcard_question.setText(questions[flashcard_number]);
        setDrawableColor(question_drawable, res.getColor(R.color.colorQuestion));
        loadAnswers();
        answered = false;
        btn.setVisibility(Button.INVISIBLE);
    }

    void loadAnswers()
    {
        possible_answers = answers[flashcard_number].split(";", 3);
        Integer[] i = new Integer[]{0,1,2};
        List<Integer> l = Arrays.asList(i);
        Collections.shuffle(l);
        answer0.setText(possible_answers[l.get(0)]);
        answer1.setText(possible_answers[l.get(1)]);
        answer2.setText(possible_answers[l.get(2)]);
        correct_answer = possible_answers[0];
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

        questions = res.getStringArray(R.array.questions);
        answers = res.getStringArray(R.array.answers);

        question_drawable = getDrawable(R.drawable.round_corner).mutate();
        setDrawableColor(question_drawable, res.getColor(R.color.colorQuestion));

        flashcard_question.setText(questions[flashcard_number]);
        flashcard_question.setBackground(question_drawable);
        //flashcard_question.setBackgroundColor(getColor(R.color.colorQuestion));

        ArrayList<TextView> l = new ArrayList<TextView>();

        btn = (Button)findViewById(R.id.nextButton);

        loadAnswers();
    }
}
