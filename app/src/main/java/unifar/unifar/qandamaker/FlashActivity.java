package unifar.unifar.qandamaker;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Space;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class FlashActivity extends AppCompatActivity implements Fragment_flash.OnFragmentInteractionListener, ParentActivityFinishInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        Intent intent = getIntent();
        String question_name = intent.getStringExtra("question_name");
        String answer_name = intent.getStringExtra("answer_name");

        Bundle bundle = new Bundle();
        bundle.putString("question_name",question_name);
        bundle.putString("answer_name",answer_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarOnFlashActivity);
        toolbar.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.colorPrimaryDark));
        toolbar.setTitleTextColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.white));
        toolbar.setTitle(MainActivity.mainValue);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Fragment_flash fragment_flash = Fragment_flash.newInstance();
        fragment_flash.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container_detail_flash, fragment_flash);
        fragmentTransaction.commit();
        showFirstTutorials();
    }

    private void showFirstTutorials() {
        MaterialShowcaseSequence materialShowcaseSequence = new MaterialShowcaseSequence(this, "40012");
        Space dummy = (Space) findViewById(R.id.dummyOnFlash);
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        materialShowcaseSequence.setConfig(config);

        materialShowcaseSequence.addSequenceItem(dummy,
                "ここでは、問題文と解答を自動で切り替えて暗記できます",
                getString(R.string.ok));
        materialShowcaseSequence.addSequenceItem(dummy,
                "声に出したり  頭の中で読んだり  \n自分に合った方法で暗記しましょう",
                getString(R.string.ok));
        materialShowcaseSequence.addSequenceItem(dummy,
                "切り替えるタイミングは  下のバーで調整できます",
                getString(R.string.ok));
        materialShowcaseSequence.start();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public void finishParentActivity(){
        finish();
    }

}
