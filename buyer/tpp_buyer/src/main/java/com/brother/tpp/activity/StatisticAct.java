package com.brother.tpp.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.tianpingpai.buyer.R;
import com.brother.tpp.adapter.TimeSpinnerAdapter;
import com.brother.tpp.net.HttpUtil;
import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.PriceFormat;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.tools.TLog;
import com.brother.tpp.vo.ErrorInfo;
import com.brother.tpp.vo.Statistic_category;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.loopj.android.http.JsonHttpResponseHandler;


/** 买家 统计界面 饼状图
 * @author lwr
 *
 */
public class StatisticAct extends FragmentActivity implements OnChartValueSelectedListener {

	protected static final String TAG = "StatisticAct";
	private ErrorInfo er = new ErrorInfo();
	private Spinner sp_year, sp_month;
	private TimeSpinnerAdapter yearAdapter = new TimeSpinnerAdapter(this);
	private TimeSpinnerAdapter monthAdapter = new TimeSpinnerAdapter(this);
	private boolean is_frist = false;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	private PieChart mChart;
	private TextView tv_category_info, tv_category_mny;
	private Typeface tf;
	private ArrayList<String> xVals;
	private ArrayList<Entry> yVals1;
	private float total_y;
	private int mYear;
	private int mMonth;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_statistic);

		String data_str = sdf.format(new Date());
		String [] year_month = data_str.split("-");

		mYear = Integer.parseInt(year_month[0]);
		mMonth = Integer.parseInt(year_month[1]);
		TLog.w(TAG, "57------------year="+mYear+",month="+mMonth);

		initView();

	}


	private void initView(){

		sp_year = (Spinner) findViewById(R.id.sp_year);
		sp_month = (Spinner) findViewById(R.id.sp_month);

		int selectionYear = mYear-2015<0?0:mYear-2015;
		TLog.w(TAG, "97--------"+selectionYear);

		String[] strYear = new String[selectionYear+1];
		for(int j=0; j<=selectionYear; j++){
			int year = 2015+j;
			strYear[j] = year+"年";
		}

		yearAdapter.setData(strYear);
		monthAdapter.setData(new String[] {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"});
		sp_year.setAdapter(yearAdapter);
		sp_month.setAdapter(monthAdapter);
		//		sp_year.setProt("选择年份");
		sp_year.setSelection(selectionYear);
		sp_month.setSelection(mMonth-1);
		if(strYear.length == 1)
			sp_year.setClickable(false);
		sp_year.setOnItemSelectedListener(ItemSelectedListener);
		sp_month.setOnItemSelectedListener(ItemSelectedListener);

		mChart = (PieChart) findViewById(R.id.chart1);
//		mChart.setCenterText("");
		tv_category_info = (TextView) findViewById(R.id.tv_category_info);
		tv_category_mny = (TextView) findViewById(R.id.tv_category_mny);
		mChart.setUsePercentValues(true);

		// change the color of the center-hole
		// mChart.setHoleColor(Color.rgb(235, 235, 235));
		mChart.setHoleColorTransparent(true);

		tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

		mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));

		mChart.setHoleRadius(60f);

		mChart.setDescription("");

		mChart.setDrawCenterText(true);

		mChart.setDrawHoleEnabled(true);

		mChart.setRotationAngle(0);
		// enable rotation of the chart by touch
		mChart.setRotationEnabled(true);

		// add a selection listener
		mChart.setOnChartValueSelectedListener(this);
		mChart.setCenterText("");

		Legend l = mChart.getLegend();
		l.setPosition(LegendPosition.RIGHT_OF_CHART);
		l.setXEntrySpace(7f);
		l.setYEntrySpace(5f);

	}

	List<Statistic_category> data;

	private void getData(){
		if(!UserManager.getInstance().isLoggedIn()){
			return;
		}

		String accessToken = UserManager.getInstance().getCurrentUser().getAccessToken();
        RequestParams params = new RequestParams();
        if(accessToken == null){
            params.add("accessToken", "");
        }else{
            params.add("accessToken", accessToken);
        }
        params.add("year", String.valueOf(mYear));
        params.add("month", String.valueOf(mMonth));

		HttpUtil.get(URLUtil.GET_BUYER_STATISTICS, params, new JsonHttpResponseHandler(){

			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				super.onSuccess(statusCode, headers, response);

				mChart.setVisibility(View.VISIBLE);
				TLog.w(TAG, "64------------"+response.toString());
				er.errorState = response.optInt("statusCode");
				if(er.errorState == URLUtil.DATA_STATE_SUCCESS){
					JSONArray array = response.optJSONArray("result");

					if( array != null ){

						data = new ArrayList<>();
						float total_mny = 0;
						for(int i=0;i<array.length();i++){

							JSONObject obj = array.optJSONObject(i);
							Statistic_category item = new Statistic_category();
							item.setCategory_name(obj.optString("category_name"));

							item.setCategory_id(obj.optInt("category_id"));
							float item_mny = (float)obj.optDouble("mny");
							item.setMny( item_mny );
							item.setPercent(obj.optDouble("percent"));
							data.add(item);
							total_mny += item_mny;

						}

						mChart.setCenterText("总支出\n"+total_mny+"元");
						setData(data.size());
						mChart.animateXY(1500, 1500);
						tv_category_info.setText("总支出");
						tv_category_mny.setText(total_mny+"元");

						TLog.w(TAG, "112-------------"+data);

					}else{
						if(data != null){

							data.clear();

						}

						setData(0);
						mChart.setCenterText("总支出\n0元");
						tv_category_info.setText("总支出");
						tv_category_mny.setText("0元");
						Toast.makeText(getApplicationContext(), "当前月份可能没有订单,请看看其他月份", Toast.LENGTH_LONG).show();
					}
				}else{
					er.errorMsg = response.optString("statusDesc");
//					onDataRequestError(er);
				}
			}
		});

	}

	/**
	 *  spinner 年月份选择监听
	 */
	private OnItemSelectedListener ItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if( !is_frist ){
				is_frist = true;
			}else{

				final String str_year = sp_year.getSelectedItem().toString().trim();
				String year_str = str_year.substring(0, str_year.length()-1);
				mYear = Integer.parseInt(year_str);
				TLog.w(TAG, "60------------"+year_str);

				final String str_month = sp_month.getSelectedItem().toString().trim();
				String month_str = str_month.substring(0, str_month.length()-1);
				mMonth = Integer.parseInt(month_str);
				TLog.w(TAG, "126------------"+month_str);
				getData();

			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};
	private ArrayList<Integer> colors;


	public void onClickStatistic(View v){
		switch (v.getId()) {
		case R.id.common_back:
			finish();
			break;
		default:
			break;
		}

	}

	private void setData(int count) {
		yVals1 = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			yVals1.add(new Entry(data.get(i).getMny(), i));
		}

		total_y = get_total_Y();

		xVals = new ArrayList<>();

		for (int i = 0; i < count; i++){
			xVals.add(data.get(i).getCategory_name());
		}
		PieDataSet dataSet = new PieDataSet(yVals1, "各品类分布图");
		dataSet.setSliceSpace(3f);

		colors = new ArrayList<>();

		colors.add(Color.rgb(201, 112, 39));
		colors.add(Color.rgb(11, 150, 37));
		colors.add(Color.rgb(0, 210, 255));
		colors.add(Color.rgb(137, 68, 44));
		colors.add(Color.rgb(255, 72, 0));
		colors.add(Color.rgb(216, 195, 68));
		colors.add(Color.rgb(130, 130, 130));
		colors.add(Color.rgb(221, 0, 90));
		colors.add(ColorTemplate.getHoloBlue());

		for (int c : ColorTemplate.VORDIPLOM_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.JOYFUL_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.COLORFUL_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.LIBERTY_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.PASTEL_COLORS)
			colors.add(c);

		dataSet.setColors(colors);
		PieData data = new PieData(xVals, dataSet);
		data.setValueFormatter(new PercentFormatter());
		data.setValueTextSize(11f);
		data.setValueTextColor(Color.WHITE);
		data.setValueTypeface(tf);
		mChart.setData(data);
		// undo all highlights
		mChart.highlightValues(null);
		mChart.invalidate();
	}


	private float get_total_Y(){
		float total_y = 0;
		for(int j = 0;j<yVals1.size(); j ++){
			total_y += yVals1.get(j).getVal();
		}
		return total_y;
	}

	private float get_cur_y(int index){
		float cur_y = 0;
		for(int j = 0;j<index; j ++){
			cur_y += yVals1.get(j).getVal();
		}
		return cur_y;

	}

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		if (e == null)
			return;
		int xIndex = e.getXIndex();
		float cur_val = e.getVal();
		Log.w("VAL SELECTED",
				"Value: " + cur_val + ", xIndex: " + xIndex+ ", xStr: " + xVals.get(xIndex)
				+ ", DataSet index: " + dataSetIndex);

		float cur_y = get_cur_y(xIndex);// 偏移量

		float angle = ( ( ( 1 - (cur_y)/total_y) ) * 360) + 90;

		// 再减去 选中 所占 角度, 右移 -
		float right_angle = ((float)(0.5 * cur_val)/total_y) * 360;
		angle = angle - right_angle;
		mChart.spin(1000, mChart.getRotationAngle(), angle);
		Statistic_category item = data.get(xIndex);
		double percent_d = item.getPercent()*100;
		float percent = PriceFormat.formatPrice((float)(percent_d));
		tv_category_info.setText(item.getCategory_name()+"  "+percent+"%");
		tv_category_info.setTextColor(colors.get(xIndex));
		tv_category_mny.setText(item.getMny() + "元");
		tv_category_mny.setTextColor(colors.get(xIndex));
	}

	@Override
	public void onNothingSelected() {
		Log.w("PieChart", "nothing selected");
	}

}
