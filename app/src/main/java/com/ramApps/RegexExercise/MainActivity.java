package com.ramApps.RegexExercise;
 
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity { 
    
	public static final int FLAG_REGEX_STYLE_FOREGROUND = 2;
	public static final int FLAG_REGEX_STYLE_BACKGROUND = 4;

	private Toolbar toolbar;
	private LinearLayout llRegexList;
	private Button btnRemoveRegex;
	private FloatingActionButton fabApplyRegex;
	private EditText etPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
		addListeners();
		AddRegexInput(getColor(R.color.primary), FLAG_REGEX_STYLE_FOREGROUND);
		setSupportActionBar(toolbar);
    }

	private void init() {
		toolbar = findViewById(R.id.activity_main_Toolbar);
		llRegexList = findViewById(R.id.activitymainLinearLayoutRegexes);
		btnRemoveRegex = findViewById(R.id.activitymainButtonRemoveRegexInput);
		fabApplyRegex = findViewById(R.id.activitymainFabApply);
		etPreview = findViewById(R.id.activitymainEditTextPreview);
	}

	private void addListeners() {
		
		btnRemoveRegex.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					llRegexList.removeViewAt(llRegexList.getChildCount()-1);
					if(llRegexList.getChildCount() == 1) btnRemoveRegex.setVisibility(View.GONE);
				}
			});
		
		fabApplyRegex.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					etPreview.setText(etPreview.getText().toString());
					if(etPreview.getText().toString().equals("")) return;
					for(int i = 0; i < llRegexList.getChildCount(); i++) {
						String regex = ((EditText) ((LinearLayout) llRegexList.getChildAt(i)).getChildAt(1)).getText().toString();
						if(!regex.equals("")) {
							applyRegex(regex,
							Integer.parseInt(((LinearLayout) llRegexList.getChildAt(i)).getChildAt(0).getTag() + ""),
							((TextView) ((LinearLayout) llRegexList.getChildAt(i)).getChildAt(0)).getText().toString().equals("F")? FLAG_REGEX_STYLE_FOREGROUND : FLAG_REGEX_STYLE_BACKGROUND);
						}
					}
				}
			});
	}

	private void applyRegex(String regex, int color, int flag) {
		try{
			Pattern ptr = Pattern.compile(regex);
			Matcher mchr = ptr.matcher(etPreview.getText());
			SpannableString ss = new SpannableString(etPreview.getText());
			while(mchr.find()) {
				if(flag == FLAG_REGEX_STYLE_FOREGROUND) {
					ss.setSpan(new ForegroundColorSpan(color), mchr.start(), mchr.end(), ss.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else {
					ss.setSpan(new BackgroundColorSpan(color), mchr.start(), mchr.end(), ss.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			etPreview.setText(ss);
		} catch(PatternSyntaxException e) {
			etPreview.setText("========== REGEX SYNTAX ERROR ==========\n" + e.toString() + "\n========== Please fix that and try again ;) ==========\n\n" + etPreview.getText());
		}
	}
	
	private void AddRegexInput(int color, int styleFlag) {
		LayoutParams etlp = new LayoutParams(LayoutParams.MATCH_PARENT, (int) getPixelDimension(56));
		etlp.setMargins((int) getPixelDimension(10), (int) getPixelDimension(2), (int) getPixelDimension(10), (int) getPixelDimension(2));
		
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(ll.HORIZONTAL);
		ll.setLayoutParams(etlp);
		
		TextView tv = new TextView(this);
		tv.setText(styleFlag == FLAG_REGEX_STYLE_FOREGROUND? "F" : "B");
		tv.setTextSize(28);
		tv.setTextColor(0x88888888);
		tv.setGravity(Gravity.CENTER);
		GradientDrawable gdCircle = new GradientDrawable();
		gdCircle.setShape(GradientDrawable.OVAL);
		gdCircle.setColor(color);
		tv.setBackground(gdCircle);
		tv.setTag(color + "");
		tv.setLayoutParams(new LayoutParams((int) getPixelDimension(56), (int) getPixelDimension(56)));
		((LayoutParams) tv.getLayoutParams()).setMarginEnd((int) getPixelDimension(10));
		tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					selectRegexStyle(view);
				}
			});
		
		EditText et = new EditText(this);
		et.setSingleLine();
		et.requestFocus();
		et.setTextColor(getColor(R.color.onSurfaceVariant));
		et.setBackgroundResource(R.drawable.text_input_bg);
		et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		et.setOnEditorActionListener(new OnEditorActionListener(){

				@Override
				public boolean onEditorAction(TextView tv, int i, KeyEvent ke) {
					if(llRegexList.getChildCount() == 5)
						return false;
					
					int red = new Random().nextInt(256);
					int green = new Random().nextInt(256);
					int blue = new Random().nextInt(256);
					AddRegexInput(Color.rgb(red, green, blue), FLAG_REGEX_STYLE_FOREGROUND);
					return true;
				}
			});
		
		ll.addView(tv);
		ll.addView(et);
		llRegexList.addView(ll);
		
		if(llRegexList.getChildCount() > 1)
			btnRemoveRegex.setVisibility(View.VISIBLE);
	}

	private void selectRegexStyle(final View viewStylePreview) {
		View v = getLayoutInflater().inflate(R.layout.style_selection_dialog, null); 
		final ColorSelectionView csv = v.findViewById(R.id.style_selection_dialog_ColorSelectionView);
		csv.setDefaultColor(Integer.parseInt(((TextView) viewStylePreview).getTag() + ""));
		final RadioGroup rg = v.findViewById(R.id.styleselectiondialogRadioGroupStyleTarget);
		if(((TextView) viewStylePreview).getText().equals("F")) {
			rg.check(R.id.styleselectiondialogRadioButtonForeground);
		} else {
			rg.check(R.id.styleselectiondialogRadioButtonBackground);
		}
		
		AlertDialog dialog = new AlertDialog.Builder(this)
			.setTitle("Select regex style:")
			.setView(v)
			.setPositiveButton("Select", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dia, int which) {
					GradientDrawable gdCircle = new GradientDrawable();
					gdCircle.setShape(GradientDrawable.OVAL);
					gdCircle.setColor(csv.getSelectedColor());
					viewStylePreview.setBackground(gdCircle);
					viewStylePreview.setTag(csv.getSelectedColor() + "");
					
					((TextView) viewStylePreview).setText((rg.getCheckedRadioButtonId() == R.id.styleselectiondialogRadioButtonForeground)? "F" : "B");
				}
			})
			.setNegativeButton("Cancel", null)
			.create();
		dialog.show();
	}
	
	private float getPixelDimension(int dp) {
		return dp * getResources().getDisplayMetrics().density;
	}
	
} 
