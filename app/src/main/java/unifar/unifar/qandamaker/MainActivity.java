package unifar.unifar.qandamaker;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

// TODO:OnPause()時のBundleの実装
// TODO:賢いレビュー催促
public class MainActivity extends AppCompatActivity implements DialogListener, ExamDialogFragment.OnFragmentInteractionListener {

    public static final int INT_QfileLinesPerOneQuestion = 100;
    public static final int INT_QfileQuestionIndex = 0;
    public static final int INT_QfileAnswerIndex = 1;
    public static final int INT_QfileTagIndex = 2;
    public static final int INT_QfileAnswerHistoryIndex1 = 3;
    public static final int INT_QfileAnswerHistoryIndex2 = 4;
    public static final int INT_QfileAnswerHistoryIndex3 = 5;
    public static final int INT_QfileLastIndex = 5;
    public static int int_listview_position;
    private static List<String> listData;
    private static List<String> qlistData;
    private static List<String> alistData;
    private static List<String> taglistData;
    private static List<Boolean[]> historyData;
    public static List<Question> mainQuestionsDataBuffer;
    public static String mainValue;
    public static String mainValue_longclick;
    public static String mainValueOn2;
    public static String[] arraystr_qbook_names;
    public static HashMap<String, String> hashTemp;
    ImageView editOnListView;
    static ArrayAdapter simp;
    static QBookListAdapter qsimp;
    public CustomizedDialog_questionbook customizedDialog_questionbook;
    public static ListView R_id_listview;
    public static int int_onLonglistView_Position;
    public static int int_onListViewPositionOn2;
    public MainActivity mainActivity;
    FragmentManager fragmentManager = getFragmentManager();
    Toolbar toolbar;
    FloatingActionButton fab;
    ShowcaseConfig config;
    MaterialShowcaseSequence materialShowcaseSequenceFirst;
    MaterialShowcaseSequence materialShowcaseSequenceSecond;
    Space dummy;
    MaterialShowcaseSequence materialShowcaseSequenceThird;
    MaterialShowcaseSequence materialShowcaseSequenceForth;
    MaterialShowcaseSequence materialShowcaseSequenceFifth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.viewFlag = 1;
        int_listview_position = -1;
        listData = new ArrayList<>();
        qlistData = new ArrayList<>();
        alistData = new ArrayList<>();
        taglistData = new ArrayList<>();
        historyData = new ArrayList<>();
        hashTemp = new HashMap<>();
        arraystr_qbook_names = this.fileList();
        mainQuestionsDataBuffer = new ArrayList<>();
        mainActivity = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.colorPrimaryDark));
        toolbar.setTitleTextColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        setSupportActionBar(toolbar);
        R_id_listview = (ListView) findViewById(R.id.listView);
        simp = new QBooksListAdapter(this,
                R.layout.qbooksitem,
                listData
        );

        qsimp = new QBookListAdapter(this,
                R.layout.questionslistitem,
                mainQuestionsDataBuffer,
                fragmentManager
        );


        inputQbookFiles();
        toQbooksList();
        R_id_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int_onLonglistView_Position = position;
                if (MyApplication.viewFlag == 1) {
                    mainValue_longclick = listData.get(int_onLonglistView_Position);
                }
                if (MyApplication.viewFlag == 2) {
                    mainValue_longclick = qlistData.get(int_onLonglistView_Position);
                }

                Log.d("OnQbook", String.valueOf(mainValue_longclick));

                DialogFragment dialogFragment = MyAlarm.newInstance();
                dialogFragment.show(getFragmentManager(), "Alart");


                return true;
            }
        });
        R_id_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int_listview_position = position;
                if (MyApplication.viewFlag == 1) {
                    //クイズの画面に遷移
                    mainValue = listData.get(int_listview_position);
                    toQlist();

                    return;
                }
                if (MyApplication.viewFlag == 2) {
                    mainValueOn2 = qlistData.get(int_listview_position);
                    Intent intent = new Intent(MyApplication.getAppContext(), DetailQuizActivity.class);
                    startActivity(intent);
                }
            }
        });

        config = new ShowcaseConfig();
        config.setDelay(500);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        customizedDialog_questionbook = CustomizedDialog_questionbook.newInstance();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.bundle.putBoolean(CustomizedDialog_questionbook.IsRecreatedKeyStr, false);
                reloadLists();
                FragmentManager fragmentManager = getFragmentManager();
                customizedDialog_questionbook.show(getFragmentManager(), "firstInputDialog");
            }
        });
        materialShowcaseSequenceFifth = new MaterialShowcaseSequence(this, "100511");
        dummy = (Space) findViewById(R.id.dummyView);
        showFirstTutorial();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        reloadAdapter();

    }

    void showFirstTutorial(){
        materialShowcaseSequenceFirst = new MaterialShowcaseSequence(this,"10011");
        materialShowcaseSequenceFirst.setConfig(config);

        materialShowcaseSequenceFirst.addSequenceItem(dummy,
                "一問一答作成アプリ「Cotton」を\nインストールいただき  \nありがとうございます！\nこれから 簡単なチュートリアルが始まります",
                getString(R.string.ok));

        materialShowcaseSequenceFirst.addSequenceItem(fab,
                "新しい問題集を追加するには  \nこの[+]ボタンを押してください",
                getString(R.string.ok));

        materialShowcaseSequenceFirst.start();

    }


    void showQbookAddedTutorial(){
            materialShowcaseSequenceSecond = new MaterialShowcaseSequence(this,"10021");

            materialShowcaseSequenceSecond.setConfig(config);

            materialShowcaseSequenceSecond.addSequenceItem(dummy,
                    "問題集が追加できました！ \n 問題集では  問題を作成して試験をすることができます",
                    getString(R.string.ok));

            materialShowcaseSequenceSecond.addSequenceItem(dummy,
                    "左の○はその問題集ごとの正答率を表しています\n  赤  オレンジ  黄  緑  青の順番で良くなっています",
                    getString(R.string.ok));


            materialShowcaseSequenceSecond.addSequenceItem(dummy,
                    "早速クリックしてみましょう！",
                    getString(R.string.ok));
            materialShowcaseSequenceSecond.start();
        }
    void showQuestionTutorial() {
                 materialShowcaseSequenceThird = new MaterialShowcaseSequence(this, "10031");

            materialShowcaseSequenceThird.setConfig(config);

            materialShowcaseSequenceThird.addSequenceItem(dummy,
                "ここでは問題を追加  削除  編集\nすることができます",
                getString(R.string.ok));

            materialShowcaseSequenceThird.addSequenceItem(fab,
                "問題には  問題文  解答  タグ  を設定できます  \n同じタグがつけられた問題の解答は試験で選択肢になります",
                getString(R.string.ok));

            materialShowcaseSequenceThird.addSequenceItem(fab,
                "タグ機能は、同じ問題集の中で使うことができます",
                getString(R.string.ok));

        materialShowcaseSequenceThird.addSequenceItem(fab,
                "新しい問題を追加するには\nこの[+]ボタンを押してください",
                    getString(R.string.ok));

        materialShowcaseSequenceThird.start();
        }

    void showQuestionAddedTutorial(){
            materialShowcaseSequenceForth = new MaterialShowcaseSequence(this,"10041");
            View examView = findViewById(R.id.item_school);
            materialShowcaseSequenceForth.setConfig(config);

            materialShowcaseSequenceForth.addSequenceItem(dummy,
                "問題が追加できました！",
                getString(R.string.ok));
            materialShowcaseSequenceForth.addSequenceItem(examView,
                "試験をするにはこのアイコンをクリックします\n試験では  選択式の問題が出題されます",
                 getString(R.string.ok));
            materialShowcaseSequenceForth.addSequenceItem(dummy,
                "左の３つの丸は、最近３回の試験で正解した回数を表しています",
                getString(R.string.ok));
            materialShowcaseSequenceForth.addSequenceItem(dummy,
                "問題を編集するには、右の鉛筆アイコンをクリックします",
                getString(R.string.ok));
            materialShowcaseSequenceForth.addSequenceItem(dummy,
                "問題を削除するには、問題を長押しします",
                getString(R.string.ok));
            materialShowcaseSequenceForth.addSequenceItem(dummy,
                "では  問題をクリックしてみましょう",
                getString(R.string.ok));

        materialShowcaseSequenceForth.start();
        }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (MyApplication.viewFlag) {
            case 1:
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_main, menu);
                break;
            case 2:
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_main_questions, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                toQbooksList();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.item_school:
                showExamFragment();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    public void onClickOk() {
        //
        Log.d("onqbook", "onClickOk();");
        if (MyApplication.viewFlag == 1) {
            listData.clear();
            makefiles(MyApplication.bundle.getString("questionStr"));
            inputQbookFiles();
            showQbookAddedTutorial();
        }
        if (MyApplication.viewFlag == 2) {
            outPutDataToFile();
            MyApplication.bundle.putBoolean("isEditMode", false);
            MyApplication.bundle.putBoolean(CustomizedDialog_questionbook.IsRecreatedKeyStr, false);
            showQuestionAddedTutorial();
        }
        if (MyApplication.viewFlag == 3) {
            if (MyApplication.bundle.getBoolean(CustomizedDialog_questionbook.IsRecreatedKeyStr)) {
                Fragment firstDialog = getFragmentManager().findFragmentByTag("firstInputDialog");
                getFragmentManager().beginTransaction().remove(firstDialog).commit();
            }
        }

    }

    @Override
    public void onClickOkOnEditMode(Question question) {

        if (MyApplication.viewFlag == 2) {
            replace100Lines(mainValue, int_onListViewPositionOn2, question);
            MyApplication.bundle.putBoolean(CustomizedDialog_questionbook.IsRecreatedKeyStr, false);

        }
        if (MyApplication.viewFlag == 3) {
            if (MyApplication.bundle.getBoolean(CustomizedDialog_questionbook.IsRecreatedKeyStr)) {
                Fragment firstDialog = getFragmentManager().findFragmentByTag("firstInputDialog");
                getFragmentManager().beginTransaction().remove(firstDialog).commit();
            }
        }
    }


    private void outPutDataToFile() {
        outputtoFile(mainValue, MyApplication.bundle.getString("questionStr"));
        outputtoFile(mainValue, MyApplication.bundle.getString("answerStr"));
        outputtoFile(mainValue, MyApplication.bundle.getString("str_tag_name"));
        outputtoFile(mainValue, "false");
        outputtoFile(mainValue, "false");
        outputtoFile(mainValue, "false");
        for (int i = 0; i < INT_QfileLinesPerOneQuestion - 1 - INT_QfileLastIndex; i++) {
            outputtoFile(mainValue, String.valueOf(i));
        }
        reloadLists();
    }

    public static void onClickOk_myalarm() {
        Log.d("OnQBookBoxOkClick", "ポジション:" + String.valueOf(int_onLonglistView_Position));
        Log.d("OnQBookBoxOkClick", "データ:" + String.valueOf(plusTxt(mainValue_longclick)));
        if (MyApplication.viewFlag == 1) {
            MyApplication.getAppContext().deleteFile(plusTxt(mainValue_longclick));
            listData.clear();
            inputQbookFiles();
            simp.notifyDataSetChanged();
        }
        if (MyApplication.viewFlag == 2) {
            delete100Line(mainValue, int_onLonglistView_Position);
            qsimp.notifyDataSetChanged();
        }

        /* Snackbar.make(findViewById(R.id.activityMain_relativeLayout), "Snackbar test", Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();

        */
    }

    static void outputtoFile(String file, String str) {
        removeExtension(file);
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = MyApplication.getAppContext().openFileOutput(plusTxt(file), MODE_APPEND);
            fileOutputStream.write(str.getBytes());
            fileOutputStream.write(13);
            fileOutputStream.write(10);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    static void outputtoFileByList(String file, List<String> list) {
        removeExtension(file);
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = MyApplication.getAppContext().openFileOutput(plusTxt(file), MODE_APPEND);
            for (int i = 0; i < list.size(); i++) {
                fileOutputStream.write(list.get(i).getBytes());
                fileOutputStream.write(13);
                fileOutputStream.write(10);
            }
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static ArrayList<Question> makeQuestionsArrayFromFile(String file) {
        ArrayList<Question> questionArrayList = new ArrayList<>();
        Question questionForExam = new Question();
        ArrayList<String> textBuffer = inputFromFileToArray(file);
        Boolean resultsBuffer[] = new Boolean[3];
        if (textBuffer.size() > 99) {
            for (int i = 0; i < textBuffer.size(); i++) {
                switch (i % INT_QfileLinesPerOneQuestion) {
                    case INT_QfileQuestionIndex:
                        questionForExam.setQuestionName(textBuffer.get(i));
                        break;
                    case INT_QfileAnswerIndex:
                        questionForExam.setAnswerName(textBuffer.get(i));

                        break;
                    case INT_QfileTagIndex:
                        questionForExam.setTagName(textBuffer.get(i));
                        break;
                    case INT_QfileAnswerHistoryIndex1:
                        resultsBuffer[0] = returnBooleanByString(CustomizedDialog_questionbook.ifNullReplace(textBuffer.get(i)));
                        break;
                    case INT_QfileAnswerHistoryIndex2:
                        resultsBuffer[1] = returnBooleanByString(CustomizedDialog_questionbook.ifNullReplace(textBuffer.get(i)));
                        break;
                    case INT_QfileAnswerHistoryIndex3:
                        resultsBuffer[2] = returnBooleanByString(CustomizedDialog_questionbook.ifNullReplace(textBuffer.get(i)));
                        questionForExam.setResults(resultsBuffer.clone());
                        resultsBuffer[0] = null;
                        resultsBuffer[1] = null;
                        resultsBuffer[2] = null;
                        break;
                    // 6-99 lines are empty.
                    case INT_QfileLastIndex + 1:
                        i = i + INT_QfileLinesPerOneQuestion - INT_QfileLastIndex - 2;
                    case INT_QfileLinesPerOneQuestion - 1:
                        questionForExam.setIndex(i / INT_QfileLinesPerOneQuestion);
                        questionArrayList.add(questionForExam.clone());
                        questionForExam.resetAll();
                        break;
                }
            }
        }
        return questionArrayList;

    }


    static void inputfromFile(String file) {
        Question questionForExam = new Question();
        ArrayList<String> textBuffer;
        textBuffer = inputFromFileToArray(file);
        Boolean resultsBuffer[] = new Boolean[3];
        mainQuestionsDataBuffer.clear();
        if (textBuffer.size() > 99) {
            for (int i = 0; i < textBuffer.size(); i++) {
                switch (MyApplication.viewFlag) {
                    case 1:
                        Log.d("onqbook", "MyApplication.viewFlag = 1でinputfromFileを呼ばないでください");
                        break;
                    case 2:
                        switch (i % INT_QfileLinesPerOneQuestion) {
                            case INT_QfileQuestionIndex:
                                addQlistData(textBuffer.get(i));
                                questionForExam.setQuestionName(textBuffer.get(i));
                                break;
                            case INT_QfileAnswerIndex:
                                addAlistData(textBuffer.get(i));
                                questionForExam.setAnswerName(textBuffer.get(i));

                                break;
                            case INT_QfileTagIndex:
                                addTaglistData(textBuffer.get(i));
                                questionForExam.setTagName(textBuffer.get(i));
                                break;
                            case INT_QfileAnswerHistoryIndex1:
                                resultsBuffer[0] = returnBooleanByString(CustomizedDialog_questionbook.ifNullReplace(textBuffer.get(i)));
                                break;
                            case INT_QfileAnswerHistoryIndex2:
                                resultsBuffer[1] = returnBooleanByString(CustomizedDialog_questionbook.ifNullReplace(textBuffer.get(i)));
                                break;
                            case INT_QfileAnswerHistoryIndex3:
                                resultsBuffer[2] = returnBooleanByString(CustomizedDialog_questionbook.ifNullReplace(textBuffer.get(i)));
                                addHistoryData(resultsBuffer.clone());
                                questionForExam.setResults(resultsBuffer.clone());
                                resultsBuffer[0] = null;
                                resultsBuffer[1] = null;
                                resultsBuffer[2] = null;
                                break;
                            // 6-99 lines are empty.
                            case INT_QfileLinesPerOneQuestion - 1:
                                questionForExam.setIndex(i / INT_QfileLinesPerOneQuestion);
                                mainQuestionsDataBuffer.add(questionForExam.clone());
                                questionForExam.resetAll();
                                break;
                        }
                }
            }
        }
    }

    private static Boolean returnBooleanByString(String string) {
        Boolean b = false;
        if (string.equals("true")) {
            b = true;
        }
        return b;

    }

    private static void resetfiles(String file) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = MyApplication.getAppContext().openFileOutput(plusTxt(file), MODE_PRIVATE);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void makefiles(String file) {
        resetfiles(file);
    }

    public static String plusTxt(String str) {
        return str + ".txt";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (MyApplication.viewFlag == 2) {
                toQbooksList();
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void inputQbookFiles() {
        arraystr_qbook_names = MyApplication.getAppContext().fileList();
        for (String arraystr_qbook_name : arraystr_qbook_names) {
            addListData(removeExtension(arraystr_qbook_name));
        }
    }

    public static String removeExtension(String fileName) {
        String newName;
        int lastPosition = fileName.lastIndexOf('.');
        if (lastPosition > 0) {
            newName = fileName.substring(0, lastPosition);
        } else {
            newName = fileName;
        }
        return newName;
    }

    public static void delete100Line(String file, int index) {

        ArrayList<String> textBuffer = new ArrayList<>();
        ArrayList<String> textBufferBuffer = inputFromFileToArray(file);
        int delta;
        for (int i = 0; i < textBufferBuffer.size(); i++) {
            delta = i + 1 - index * INT_QfileLinesPerOneQuestion;
            if (!((delta > 0) & (delta < INT_QfileLinesPerOneQuestion + 1))) {
                textBuffer.add(textBufferBuffer.get(i));
            }
        }
        resetfiles(file);
        outputtoFileByList(file, textBuffer);
        reloadLists();
    }

    public static void replace100Lines(String file, int index, Question questionArg) {
        ArrayList<String> textBuffer = new ArrayList<>();
        ArrayList<String> textBufferBuffer = inputFromFileToArray(file);
        int delta;
        for (int i = 0; i < textBufferBuffer.size(); i++) {
            delta = i + 1 - index * INT_QfileLinesPerOneQuestion;
            if (!((delta > 0) & (delta < INT_QfileLinesPerOneQuestion + 1))) {
                textBuffer.add(textBufferBuffer.get(i));
            } else {
                switch (i % INT_QfileLinesPerOneQuestion) {
                    case INT_QfileQuestionIndex:
                        textBuffer.add(questionArg.getQuestionName());
                        break;
                    case INT_QfileAnswerIndex:
                        textBuffer.add(questionArg.getAnswerName());
                        break;
                    case INT_QfileTagIndex:
                        textBuffer.add(questionArg.getTagName());
                        break;
                    case INT_QfileAnswerHistoryIndex1:
                        textBuffer.add(questionArg.getResults()[0].toString());
                        break;
                    case INT_QfileAnswerHistoryIndex2:
                        textBuffer.add(questionArg.getResults()[1].toString());
                        break;
                    case INT_QfileAnswerHistoryIndex3:
                        textBuffer.add(questionArg.getResults()[2].toString());
                        break;
                    default:
                        textBuffer.add(textBufferBuffer.get(i));
                }
            }
        }
        resetfiles(file);
        outputtoFileByList(file, textBuffer);
        reloadLists();
    }

    public static ArrayList<String> inputFromFileToArray(String file) {
        ArrayList<String> contentsArray = new ArrayList<>();
        removeExtension(file);
        FileInputStream fileInputStream;
        String text;
        try {
            fileInputStream = MyApplication.getAppContext().openFileInput(plusTxt(file));
            String lineBuffer;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"), 10000);
            contentsArray = new ArrayList<>();
            while ((lineBuffer = bufferedReader.readLine()) != null) {
                text = lineBuffer;
                contentsArray.add(text);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentsArray;
    }

    public static void reloadLists() {
        qlistData.clear();
        alistData.clear();
        taglistData.clear();
        historyData.clear();
        mainQuestionsDataBuffer.clear();

        if (mainValue != null) {
            inputfromFile(mainValue);
        }
        reloadAdapter();
    }

    void reloadQBooksList() {
        listData.clear();
        inputQbookFiles();
        reloadAdapter();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void showExamFragment() {
        Log.d("OnQbook", "// do test");
        if (qlistData.size() != 0) {
            if (materialShowcaseSequenceFifth.hasFired()) {
                ExamDialogFragment examDialogFragment = new ExamDialogFragment();
                examDialogFragment.show(getFragmentManager(), "ExamDialogFragment");
            }else {
                showExamTutorial();
            }
        }

    }

    private void showExamTutorial() {
        materialShowcaseSequenceFifth.setConfig(config);

        materialShowcaseSequenceFifth.addSequenceItem(dummy,
                "これから試験を始めます",
                getString(R.string.ok));
        materialShowcaseSequenceFifth.addSequenceItem(dummy,
                "出題数と出題モードを設定できます",
                getString(R.string.ok));
        materialShowcaseSequenceFifth.addSequenceItem(dummy,
                "出題モード  ランダム  では適当に  おすすめ  では正解数が少ない順にテストできます",
                getString(R.string.ok));
        materialShowcaseSequenceFifth.start();
        materialShowcaseSequenceFifth.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
            @Override
            public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {

                if (i == 2) {
                    ExamDialogFragment examDialogFragment = new ExamDialogFragment();
                    examDialogFragment.show(getFragmentManager(), "ExamDialogFragment");
                }

            }
        });
    }

    public static List<String> makeAlterrnatives(String file, String tagName, String answer) {
        List<String> alternatives;
        ArrayList<String> textBuffer = inputFromFileToArray(file);
        List<String> allAnswer = new ArrayList<>();
        final int FULLALTERNATIVESAMOUNT = 5;
        int alternativesAmount;
        for (int i = 0; i < textBuffer.size(); i++) {
            if (i % 100 == INT_QfileTagIndex) {
                if (textBuffer.get(i).equals(tagName)) {
                    if (!textBuffer.get(i - 1).equals(answer)) {
                        allAnswer.add(textBuffer.get(i - 1));
                    }
                }
            }
        }
        Collections.shuffle(allAnswer);
        if (allAnswer.size() < FULLALTERNATIVESAMOUNT - 1) {
            alternativesAmount = allAnswer.size();
        } else {
            alternativesAmount = FULLALTERNATIVESAMOUNT - 1;
        }
        alternatives = allAnswer.subList(0, alternativesAmount);
        alternatives.add(answer);
        Collections.shuffle(alternatives);
        if (!(alternatives.size() < 3)) {
            alternatives.remove(alternatives.size() - 1);
            alternatives.add("この選択肢の中には無い");
        }
        return alternatives;
    }

    public static void makeAnswerHistoryByQuestionsList(String file, List<Question> questionList) {
        Collections.sort(questionList, new Comparator<Question>() {
            @Override
            public int compare(Question q1, Question q2) {
                return q1.getIndex() - q2.getIndex();
            }
        });
        List<String> textBuffer = new ArrayList<>();
        List<String> textBufferBuffer = inputFromFileToArray(file);
        List questionNameList = makeListFromQuetionArray(questionList, INT_QfileQuestionIndex);
        List questionAllNameList = makeListFromQuetionArray(mainQuestionsDataBuffer, INT_QfileQuestionIndex);

        int questionCountInArray = 0;
        int questionCountInAll = 0;


        Boolean isExists = false;
        for (int i = 0; i < textBufferBuffer.size(); i++) {
            switch (i % INT_QfileLinesPerOneQuestion) {
                case INT_QfileAnswerHistoryIndex1:
                    questionCountInAll++;
                    isExists = questionNameList.contains(questionAllNameList.get(questionCountInAll-1));
                    if (isExists) {
                        if (questionCountInArray<questionList.size()) {
                            textBuffer.add(questionList.get(questionCountInArray).getResultBuffer().toString());
                        }else {
                            isExists = false;
                        }
                        questionCountInArray++;
                    }
                    textBuffer.add(textBufferBuffer.get(i));
                    break;
                case INT_QfileAnswerHistoryIndex3:
                    if (! isExists) {
                        textBuffer.add(textBufferBuffer.get(i));
                    }
                    break;
                default:
                    textBuffer.add(textBufferBuffer.get(i));
                        /*
            textBuffer.add(textBufferBuffer.get(i));
                if (i == questionList.get(j).getIndex() * INT_QfileLinesPerOneQuestion + INT_QfileAnswerHistoryIndex1) {
                    textBuffer.remove(textBuffer.size() - 1);
                    textBuffer.add(questionList.get(j).getResultBuffer().toString());
                    textBuffer.add(textBufferBuffer.get(i));
                }
                if (i == questionList.get(j).getIndex() * INT_QfileLinesPerOneQuestion + INT_QfileAnswerHistoryIndex3) {
                    textBuffer.remove(textBuffer.size() - 1);
                }
                */
            }
        }
        resetfiles(file);
        outputtoFileByList(mainValue, textBuffer);
        reloadLists();
        reloadAdapter();
    }

    public static List makeListFromQuetionArray(List<Question> questionArrayList, int index) {
        List<Object> list = new ArrayList<>();
        if (index > INT_QfileLinesPerOneQuestion) {
            Log.d("onqbook", "index size error");
        } else {
            for (int i = 0; i < (questionArrayList.size()) * INT_QfileLinesPerOneQuestion; i++) {
                if ((i % INT_QfileLinesPerOneQuestion) == index) {
                    list.add(questionArrayList.get(i / INT_QfileLinesPerOneQuestion).getOBjectFromIndex(index));
                }
            }
        }
        return list;


    }

    public static void reloadAdapter() {
        ListAdapter currentListAdapter = R_id_listview.getAdapter();
        simp.notifyDataSetChanged();
        qsimp.notifyDataSetChanged();
        if (currentListAdapter == simp) {
            R_id_listview.setAdapter(simp);
        }
        if (currentListAdapter == qsimp) {
            R_id_listview.setAdapter(qsimp);
        }
        simp.notifyDataSetChanged();
        qsimp.notifyDataSetChanged();
    }

    private void toQlist() {
        MyApplication.viewFlag = 2;
        Log.d("OnQbook", "1 -> 2");
        reloadLists();
        Log.d("OnQbook", "to qsimp");
        TextView textView = (TextView) findViewById(R.id.emptyMessageOnMain1mk2);
        textView.setVisibility(View.INVISIBLE);
        R_id_listview.setEmptyView(findViewById(R.id.emptyMessageOnMain2mk2));
        R_id_listview.setAdapter(qsimp);
        toolbar.setTitle(mainValue);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showQuestionTutorial();
    }

    private void toQbooksList() {
        MyApplication.viewFlag = 1;
        Log.d("OnQbook", "2 -> 1");
        listData.clear();
        Log.d("OnQbook", "to simp");
        inputQbookFiles();
        TextView textView = (TextView) findViewById(R.id.emptyMessageOnMain2mk2);
        textView.setVisibility(View.INVISIBLE);
        R_id_listview.setEmptyView(findViewById(R.id.emptyMessageOnMain1mk2));
        R_id_listview.setAdapter(simp);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /**
     * dpからpixelへの変換
     *
     * @param dp
     * @param context
     * @return float pixel
     */
    public static float convertDp2Px(float dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }

    /**
     * pixelからdpへの変換
     *
     * @param px
     * @param context
     * @return float dp
     */
    public static float convertPx2Dp(int px, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return px / metrics.density;
    }

    public static List<String> getListData() {
        return listData;
    }

    public static void addListData(String s) {
        MainActivity.listData.add(s);
        simp.notifyDataSetChanged();
    }

    public static List<String> getQlistData() {
        return qlistData;
    }

    public static void addQlistData(String s) {
        MainActivity.qlistData.add(s);
        qsimp.notifyDataSetChanged();
    }

    public static List<String> getAlistData() {
        return alistData;
    }

    public static void addAlistData(String s) {
        MainActivity.alistData.add(s);
    }

    public static List<String> getTaglistData() {
        return taglistData;
    }

    public static void addTaglistData(String s) {
        MainActivity.taglistData.add(s);
    }

    public static List<Boolean[]> getHistoryData() {
        return historyData;
    }

    public static void addHistoryData(Boolean b[]) {
        MainActivity.historyData.add(b);
    }


}

