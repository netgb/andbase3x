
package com.andbase.library.view.picker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.andbase.library.util.AbDateUtil;



/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 轮子工具类
 */

public class AbPickerUtil {

	public final static  List<String> MDHMList = new ArrayList<String>();
	
	/**
	 * 默认的年月日的日期选择器.
	 *
	 * @param pickerViewY  选择年的轮子
	 * @param pickerViewM  选择月的轮子
	 * @param pickerViewD  选择日的轮子
	 * @param defaultYear  默认显示的年
	 * @param defaultMonth the default month
	 * @param defaultDay the default day
	 * @param minYear    开始的年
	 * @param maxYear     结束的年
	 */
	public static void initPickerValueYMD(final AbPickerView pickerViewY, final AbPickerView pickerViewM, final AbPickerView pickerViewD,
										  int defaultYear, int defaultMonth, int defaultDay, final int minYear, int maxYear){
		
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };
		//时间选择可以这样实现
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		
		if(defaultYear <= 0){
			 defaultYear = year;
		}
		if(defaultMonth <= 0){
			defaultMonth = month;
		}
		if(defaultDay <= 0){
			defaultDay = day;
		}

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);
		
		//设置"年"的显示数据
		pickerViewY.setAdapter(new AbNumericPickerAdapter(minYear, maxYear));
		pickerViewY.setCyclic(true);// 可循环滚动
		pickerViewY.setLabel("年");  // 添加文字
		pickerViewY.setCurrentItem(defaultYear - minYear);// 初始化时显示的数据
		pickerViewY.setValueTextSize(35);
		pickerViewY.setLabelTextSize(35);
		pickerViewY.setLabelTextColor(0x80000000);
		//mWheelViewY.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
		
		// 月
		pickerViewM.setAdapter(new AbNumericPickerAdapter(1, 12));
		pickerViewM.setCyclic(true);
		pickerViewM.setLabel("月");
		pickerViewM.setCurrentItem(defaultMonth-1);
		pickerViewM.setValueTextSize(35);
		pickerViewM.setLabelTextSize(35);
		pickerViewM.setLabelTextColor(0x80000000);
		// 日
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month))) {
			pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month))) {
			pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 30));
		} else {
			// 闰年
			if (AbDateUtil.isLeapYear(year)){
				pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 29));
			}else{
				pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 28));
			}
		}
		pickerViewD.setCyclic(true);
		pickerViewD.setLabel("日");
		pickerViewD.setCurrentItem(defaultDay - 1);
		pickerViewD.setValueTextSize(35);
		pickerViewD.setLabelTextSize(35);
		pickerViewD.setLabelTextColor(0x80000000);
		//pickerViewD.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
		
		// 添加"年"监听
		AbPickerView.AbOnPickerChangedListener wheelListener_year = new AbPickerView.AbOnPickerChangedListener() {

			public void onChanged(AbPickerView wheel, int oldValue, int newValue) {
				int year_num = newValue + minYear;
				int mDIndex = pickerViewM.getCurrentItem();
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(pickerViewM.getCurrentItem() + 1))) {
					pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(pickerViewM.getCurrentItem() + 1))) {
					pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 30));
				} else {
					if (AbDateUtil.isLeapYear(year_num))
						pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 29));
					else
						pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 28));
				}
				pickerViewM.setCurrentItem(mDIndex);
				
			}
		};
		// 添加"月"监听
		AbPickerView.AbOnPickerChangedListener wheelListener_month = new AbPickerView.AbOnPickerChangedListener() {

			public void onChanged(AbPickerView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 30));
				} else {
					int year_num = pickerViewY.getCurrentItem() + minYear;
					if (AbDateUtil.isLeapYear(year_num))
						pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 29));
					else
						pickerViewD.setAdapter(new AbNumericPickerAdapter(1, 28));
				}
				pickerViewD.setCurrentItem(0);
			}
		};
		pickerViewY.addChangingListener(wheelListener_year);
		pickerViewM.addChangingListener(wheelListener_month);
		
    }

	/**
	 * 默认当前时间的月日时分的时间选择器.
	 * @param pickerViewMD  选择月日的轮子
	 * @param mWheelViewHH 选择时间的轮子
	 * @param pickerViewMM  选择分的轮
	 */
	public static void initPickerValueMDHM(final AbPickerView pickerViewMD, final AbPickerView mWheelViewHH, final AbPickerView pickerViewMM){
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		//int second = calendar.get(Calendar.SECOND);
		initPickerValueMDHM(pickerViewMD,mWheelViewHH,pickerViewMM,year,month,day,hour,minute);
	}
	
	/**
	 * 默认的月日时分的时间选择器.
	 * @param pickerViewMD  选择月日的轮子
	 * @param mWheelViewHH the m wheel view hh
	 * @param pickerViewMM  选择分的轮子
	 * @param defaultYear the default year
	 * @param defaultMonth the default month
	 * @param defaultDay the default day
	 * @param defaultHour the default hour
	 * @param defaultMinute the default minute
	 */
	public static void initPickerValueMDHM(final AbPickerView pickerViewMD, final AbPickerView mWheelViewHH, final AbPickerView pickerViewMM,
										   int defaultYear, int defaultMonth, int defaultDay, int defaultHour, int defaultMinute){


		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };
		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);
		//
		final List<String> textDMList = new ArrayList<String>();

		for(int i=1;i<13;i++){
			if(list_big.contains(String.valueOf(i))){
				for(int j=1;j<32;j++){
					textDMList.add(i+"月"+" "+j+"日");
					MDHMList.add(defaultYear+"-"+i+"-"+j);
				}
			}else{
				if(i==2){
					if(AbDateUtil.isLeapYear(defaultYear)){
						for(int j=1;j<28;j++){
							textDMList.add(i+"月"+" "+j+"日");
							MDHMList.add(defaultYear+"-"+i+"-"+j);
						}
					}else{
						for(int j=1;j<29;j++){
							textDMList.add(i+"月"+" "+j+"日");
							MDHMList.add(defaultYear+"-"+i+"-"+j);
						}
					}
				}else{
					for(int j=1;j<29;j++){
						textDMList.add(i+"月"+" "+j+"日");
						MDHMList.add(defaultYear+"-"+i+"-"+j);
					}
				}
			}
			
		}
		String currentDay = defaultMonth+"月"+" "+defaultDay+"日";
		int currentDayIndex = textDMList.indexOf(currentDay);
		
		// 月日
		pickerViewMD.setAdapter(new AbTextPickerAdapter(textDMList));
		pickerViewMD.setCyclic(true);
		pickerViewMD.setLabel(""); 
		pickerViewMD.setCurrentItem(currentDayIndex);
		pickerViewMD.setValueTextSize(35);
		pickerViewMD.setLabelTextSize(35);
		pickerViewMD.setLabelTextColor(0x80000000);

		// 时
		mWheelViewHH.setAdapter(new AbNumericPickerAdapter(0, 23));
		mWheelViewHH.setCyclic(true);
		mWheelViewHH.setLabel("点");
		mWheelViewHH.setCurrentItem(defaultHour);
		mWheelViewHH.setValueTextSize(35);
		mWheelViewHH.setLabelTextSize(35);
		mWheelViewHH.setLabelTextColor(0x80000000);
		// 分
		pickerViewMM.setAdapter(new AbNumericPickerAdapter(0, 59));
		pickerViewMM.setCyclic(true);
		pickerViewMM.setLabel("分");
		pickerViewMM.setCurrentItem(defaultMinute);
		pickerViewMM.setValueTextSize(35);
		pickerViewMM.setLabelTextSize(35);
		pickerViewMM.setLabelTextColor(0x80000000);

    }

	/**
	 * 默认的时分的时间选择器.
	 * @param mWheelViewHH the m wheel view hh
	 * @param pickerViewMM  选择分的轮子
	 */
	public static void initPickerValueHM(final AbPickerView mWheelViewHH, final AbPickerView pickerViewMM) {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		initWheelPickerHM(mWheelViewHH,pickerViewMM,hour,minute);
	}

	/**
	 * 默认的时分的时间选择器.
	 *
	 * @param mWheelViewHH the m wheel view hh
	 * @param pickerViewMM  选择分的轮子
	 * @param defaultHour the default hour
	 * @param defaultMinute the default minute
	 */
	public static void initWheelPickerHM(final AbPickerView mWheelViewHH, final AbPickerView pickerViewMM,
										 int defaultHour, int defaultMinute){
		
		// 时
		mWheelViewHH.setAdapter(new AbNumericPickerAdapter(0, 23));
		mWheelViewHH.setCyclic(true);
		mWheelViewHH.setLabel("点");
		mWheelViewHH.setCurrentItem(defaultHour);
		mWheelViewHH.setValueTextSize(35);
		mWheelViewHH.setLabelTextSize(35);
		mWheelViewHH.setLabelTextColor(0x80000000);

		// 分
		pickerViewMM.setAdapter(new AbNumericPickerAdapter(0, 59));
		pickerViewMM.setCyclic(true);
		pickerViewMM.setLabel("分");
		pickerViewMM.setCurrentItem(defaultMinute);
		pickerViewMM.setValueTextSize(35);
		pickerViewMM.setLabelTextSize(35);
		pickerViewMM.setLabelTextColor(0x80000000);

    }

	
}
