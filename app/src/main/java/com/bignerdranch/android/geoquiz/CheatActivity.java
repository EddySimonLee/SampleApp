package com.bignerdranch.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE="com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";
    private boolean mAnswerIsTrue;
    private TextView mAnswerTextView;
    private Button mShowAnswer;
    private Boolean iden;
    private static final String IDEN= "identifier";

    private TextView mAPIView;
    private static int THE_API ;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue){
        Intent i = new Intent(packageContext,CheatActivity.class);
        i.putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue); //EXTRA_ANSWER_IS_TURE라는 키값으로 answerisTRUE를 저장함. 아래 getBooleanExtra에서 꺼내서 판별함
        return i;
    }

    //붙어잇는 extra값을 가져온다.
    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN,false);//boolean 으로 설정한 부정행위 extra 가져오기. 없다면 디폴트인 false 방출
        //intent 안에 getxxxxExtra 메서드가 구현되어 있다. xxxx는 각종 자료형.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat); //activity_cheat.xml에 정의된 레이아웃의 리소스ID를 인자로 전달한 것
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false);
        //이때, 액티비티를 시작시켰던 intent를 반환(이는 stactActivity를 호출할때 인자로 쓰였던 i 이다)

        mAnswerTextView = (TextView)findViewById(R.id.answer_text_view);

        mShowAnswer = (Button)findViewById(R.id.show_answer_button);

        //p.146 빌드버전 보여주기
        mAPIView = (TextView)findViewById(R.id.show_api_level);
        THE_API = Build.VERSION.SDK_INT;
        mAPIView.append(THE_API+"");



        if(savedInstanceState !=null){
            if(mAnswerIsTrue){
                mAnswerTextView.setText(R.string.true_button);
            } else {
                mAnswerTextView.setText(R.string.false_button);
            }

            setAnswerShownResult(savedInstanceState.getBoolean(IDEN));
        } //첫번째 정답보기 클릭 시 아래의 onClick이 실행되고 setAnswerResult가 첫번째로 실행
        //만일 여기서 장치회전을 여러번하면 위의 if문이 여러번 시행되는데 setAnswerShownResult가 여러번 실행될 수 있다
        //하지만 중복되어도 큰 문제는 없는듯. 어차피똑같은 data.putExtra(~) 이 계속 실행된다.

        mShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mAnswerIsTrue){
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }

                setAnswerShownResult(true);//여기서 메서드 통해 extra로 true가 저장되고, setResult로 까지 이어짐
                //이후 사용자가 back 버튼을 눌렀을때 자동적으로 onActivityResult가 오버라이드된것 실행되고..


                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){

                    //이하 버튼을 감추는 원형의 애니메이션 보여주는 api21(롤리팝) 코드.
                    int cx = mShowAnswer.getWidth()/2;
                    int cy = mShowAnswer.getHeight()/2;
                    float radius = mShowAnswer.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswer,cx,cy,radius,0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswer.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();

                } else{
                    mShowAnswer.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) { //버튼이 한번이라도 눌렸으면 saveinstance에 저장 ->기기구성 변경시에도 기억
        super.onSaveInstanceState(outState);
        outState.putBoolean(IDEN,true);
    }

    //mShowAnswer 버튼의 리스너에 구현된 메서드로, 버튼 눌렀을 시 부정행위를 했다는 것(EXTRA_ANSWER_SHOWN)을 체크하는 역할
    private void setAnswerShownResult(boolean isAnswerShown){
        Intent data = new Intent();//putExtra 위해서 빈 껍질 Intent 생성한 듯
        data.putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown);
        setResult(RESULT_OK,data); // RESULT_OK 혹은 RESULT_CANCEL 보통 둘중하나로
    }
}