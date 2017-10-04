package unifar.unifar.qandamaker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Space;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class DetailQuizActivity extends AppCompatActivity implements Fragment_flash.OnFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public ViewPager mViewPager;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_quiz);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_detail_quiz);
        mViewPager.setId(R.id.container_detail_quiz);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(MainActivity.int_listview_position);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarOnDetailQuizActivity);
        toolbar.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.colorPrimaryDark));
        toolbar.setTitleTextColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.white));
        toolbar.setTitle(MainActivity.mainValue);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showFirstTutorials();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                Log.i("Ads", "onAdClosed");
            }
        });


    }

    private void showFirstTutorials() {
        MaterialShowcaseSequence materialShowcaseSequence = new MaterialShowcaseSequence(this, "3001");
        Space dummy = (Space) findViewById(R.id.dummyOnDetailQuiz);
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        materialShowcaseSequence.setConfig(config);

        materialShowcaseSequence.addSequenceItem(dummy,
                "ここでは、問題文を暗記したり  \n問題  解答  タグを確認できます",
                getString(R.string.ok));
        materialShowcaseSequence.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            MyApplication.adCount++;
            if (mInterstitialAd.isLoaded()) {
                if (MyApplication.adCount%5 == 2) {
                    mInterstitialAd.show();
                }
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private TextView textview_switch_name;
        private TextView textview_tag_name;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail_quiz, container, false);
            textview_switch_name = (TextView) rootView.findViewById(R.id.questionName);
            textview_tag_name = (TextView) rootView.findViewById(R.id.textView_tag);
            final Bundle bundle = getArguments();
            //MainActivity.reloadLists();
            final List<String> question_Name = MainActivity.getQlistData();
            final List<String> answer_Name = MainActivity.getAlistData();
            final List<String> tag_Name = MainActivity.getTaglistData();
            final String str_question_name = String.valueOf(question_Name.get(bundle.getInt(ARG_SECTION_NUMBER)-1));
            final String str_answer_name = String.valueOf(answer_Name.get(bundle.getInt(ARG_SECTION_NUMBER)-1));
            final String str_tag_name = String.valueOf(tag_Name.get(bundle.getInt(ARG_SECTION_NUMBER)-1));
            textview_switch_name.setText(str_question_name);
            textview_tag_name.setText(getString(R.string.tag_header, str_tag_name));
            final Button changebutton = (Button) rootView.findViewById(R.id.changeButton);
            changebutton.setText(R.string.display_answer);
            changebutton.setOnClickListener(new View.OnClickListener() {
                int flag_change_button = 1;
                @Override
                public void onClick(View v) {
                    switch (flag_change_button){
                        case 1:
                            textview_switch_name.setText(str_answer_name);
                            changebutton.setText(getString(R.string.display_question));
                            flag_change_button =2;
                            break;
                        case 2:
                            textview_switch_name.setText(str_question_name);
                            changebutton.setText(getString(R.string.display_answer));
                            flag_change_button =1;
                            break;
                    }

                }
            });
            final Button button_to_flash =(Button)rootView.findViewById(R.id.learnButton);
            button_to_flash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyApplication.getAppContext(),FlashActivity.class);

                    intent.putExtra("question_name",str_question_name);
                    intent.putExtra("answer_name",str_answer_name);

                    startActivity(intent);

                }
            });


            Log.d("onqbook",String.valueOf(bundle.getInt(ARG_SECTION_NUMBER)-1));
            return rootView;
        }
    }





    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment findFragmentByPosition(ViewPager viewPager,
                                               int position) {
            return (Fragment) instantiateItem(viewPager, position);
        }
        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            // 何もしない！!
        }
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return MainActivity.getQlistData().size();
        }

    }
}
