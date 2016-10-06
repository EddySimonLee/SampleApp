package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.AlphabeticIndex;
import android.support.v7.app.AppCompatActivity;
//AppCompatActivity : Activity클래스의 서브 클래스이며 안드로이드 구버전과의 호환성 위해 제공
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity { //최신

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEAT[] = {"cheat1","cheat2","cheat3","cheat4","cheat5"};
    private static final int REQUEST_CODE_CHEAT=0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_oceans,true),
            new Question(R.string.question_mideast,false),
            new Question(R.string.question_africa,false),
            new Question(R.string.question_america,true),
            new Question(R.string.question_asia,true),
    };

    private int mCurrentIndex =0;// new int[5];
    private boolean[] mIsCheater = {false,false,false,false,false};


    private void updateQuestion(){
        //Log.d(TAG,"Updating question text for question #"+mCurrentIndex,new Exception());
        int question = mQuestionBank[mCurrentIndex].getTextResId();

        mQuestionTextView.setText(question);

    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if(mIsCheater[mCurrentIndex]){
            messageResId=R.string.judgement_toast;
        } else{
            if(userPressedTrue == answerTrue){
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this,messageResId,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) { //onCreate는 액티비티만들면 기본으로 들어가있다.매개변수 bundle은 나중에 쓸일이 있을 것.
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        mQuestionTextView = (TextView)findViewById(R.id.question_text_view );
        //xml에서 정의한 id 가 R에 넘어가고, 꺼내올때는 r에서 꺼내오는 식인가?
        //mQuestionTextView.setOnClickListener(new View.OnClickListener(){
        //    @Override
        //    public void onClick(View v) {
        //        mCurrentIndex = (mCurrentIndex+1)%mQuestionBank.length;
        //        updateQuestion();
        //    }
        //});

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v ){
                checkAnswer(true);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer(false);
            }
        });
        //위젯의 리소스ID를 인자로 받아서 해당 위젯의 View객체를 반환한다

        mNextButton = (Button)findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex+1)%mQuestionBank.length;
               // mIsCheater=false;p.133 챌린지 두번째 문제까지는 이게 있었다
                //왜냐면 배열이 아니고 int 로 존재했기에, 문제마다 커닝 여부를 기록해야하므로 false로 초기화 해줄 필요있었음.
                //하지만 이제 5자리의 배열로 만들었고, 해당 문제로 돌아오면 기억해야 하므로 초기화 안하고
                //기억은 onsaveinstancestate 메서드 통해 세이브인스턴스에 에 추가하는 걸로 OK.
                updateQuestion();
            }
        });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //answerTrue(문제별 정답)을 extra로 가지고 있는 Intent(quizactivity에서 호출하는 CheatActivity)생성
                //intent(context, class)인데 전자는 현재 액티비티, 후자는 호출하는 액티비티 인듯
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent i = CheatActivity.newIntent(QuizActivity.this,answerIsTrue);

                startActivityForResult(i,REQUEST_CODE_CHEAT);//req_code_cheat를 리퀘스트 코드로 하는 인텐트 i를 넘긴다
                //결과코드인 REQUEST_CODE_CHEAT는 부모액티비티에 반환된다!
                //이 값이 있다는건 나중에 어디선가 OnActivityResult가 호출된다는 건데, 그곳의 첫 인자값이 이것이다!
                //즉, cheat button을 누르는 액티비티에 의해 촉발된 것임을 알게해주는 식별자 역할.
            }
        });

        if(savedInstanceState !=null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            for(int i=0;i<mQuestionBank.length;i++){
                mIsCheater[(mCurrentIndex+i)%5]
                        = savedInstanceState.getBoolean(KEY_CHEAT[(mCurrentIndex+i)%5],false);//이미 savedInstanceState가 호출되었었다면
                //즉, 기기를 돌리거나 하는 행위로 onCreate가 다시 되거나 했을때 -> 현재 문항 및 커닝 여부를 저장
            }


        }

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //순서를 보면, 현 액티비티에서(부모?) intent 형성해서 requestCode(여기선 REQUEST_CODE_CHEAT)담아서 startActivityForResult 실행
        //이 메서드는 startActivty와 같이, 변수로 넘긴 intent내용으로 말미암아 안드로이드OS한테 특정 엑티비티를 실행하라는 신호를 보내고
        //우리가 형성한 그 intent는 결국 intent(quizactivty에서 촉발된 cheatActivty를 실행할 것을 변수에 담음)+extra(문제별 정답)을 담고 있으므로
        //                                                      (이때 EXTRA_ANSWER_IS_TRUE라는 이름으로 해당 정답을 꺼낼 수 있게 만들어놔서, getBooleanvalue통해 판별가능하게끔 한다)
        //안드로이드는 CheatActivity의 oncreate를 실행시킨다. 그 결과 cheatacitivty가 showbyid로 띄운 textview와 button을 띄우게 되고
        //getintent().getbooleanextra()를 통해서 문제별 정답을 해당 boolean 변수인 mAnswerIsTrue를 통해 뽑아서,
        //리스너를 걸어놓은 Button을 누르는 순간, mAnswerIsTrue가 보여지게 된다. 그리고 이 순간 setAnswerShownresult가 true로 설정되는데,
        //이는 빈껍질의(null) intent와 true값의 extra가 붙은 채로 setResult된다
        //그리고 사용자가 back 버튼 누르는 순간 여기있는 onActivityResult객체가 생성되어서 requestCode,resultCode, Intent 이 세가지를 차례대로 검사하면 된다.
        //최종적으로 아래 3가지가 다 통과되면 mIsCheater 내용에 따라 추가로 토스트 띄워주고, nextButton을 사용자가 누르면 false로 다시 초기화 해준다.
        if(resultCode!= Activity.RESULT_OK){//setResult 내용 판별 1
            return;
        }
        if(requestCode==REQUEST_CODE_CHEAT){ //cheat Button 에 의해 눌렸는지 판단
            if(data==null){ //setResult 내용 판별 2
                return;
            }
            mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data); //부정행위 했으면 true 저장됨
        }/////->> 왜 result ok 는 단독으로 판단하고, 나머지 두개를 묶어서 보는 지 모르겠음.

        super.onActivityResult(requestCode, resultCode, data);
    }

    //mCurrentIndex 값 기억하기
    @Override //기기구성 등 변경시 내용저장해놓는 곳, onstop, onpause, ondestroy 실행 전에 호출된다
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG,"onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);
        for(int i=0;i<mQuestionBank.length;i++){
            savedInstanceState.putBoolean(KEY_CHEAT[(mCurrentIndex+i)%5],mIsCheater[(mCurrentIndex+i)%5]); //용진이는 어레이로처리해서 어레이 자체를 putExtra함
            //onSaveInstanceState와 onCreate의 if(savedstate!=null) 부분 둘다 포문을 돌려줘야했다.
            //KEYCHEAT 과 mIsCheater 모두 QuizActivity안에 있는 전역변수인데
            //기기구성변동(기기 돌리는것 등)이 일어나면 onCreate가 실행되는건지
            //아니면 QuizActivity 전체가 실행되므로 onCreate가 실행되게 되는건지 -> 이런 경우라면 둘다 포문인거 이해 ㅇㅇ
        }
        //mIsCheater 값을 지워서 커닝여부 지우는 걸 방지하기 위함
        //원래 int misCheater 였는데 배열로 바꿈 -> 각각의 문제마다 bool 값을 달리하여 기억하기 위함

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy() called");
    }
}