package com.tianpingpai.seller.dialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;


/**
 * 提现弹窗
 */
@Layout(id = R.layout.ui_draw_cash_dialog)
public class DrawCashDialogViewController extends BaseViewController {

	public static final String key = "key.data";

	public void setSelectCardListener(SelectCardListener selectCardListener) {
		this.selectCardListener = selectCardListener;
	}

	SelectCardListener selectCardListener;

	public interface SelectCardListener {
		void selectCard(Model m);
	}

	private ArrayList<Model> mModels;

	public void setModels(ArrayList<Model> models){
		mModels = models;
	}

	private CardAdapter cardAdapter;

	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		hideActionBar();
		if(mModels != null){

			Log.e("xx", "33---------------id=" + mModels.size());
			Log.e("xx", "50---------------id=" + mModels.get(0).getString("name"));
			cardAdapter = new CardAdapter();
			cardAdapter.setModels(mModels);

		}

		initView(rootView);
	}

	private void initView(View view){

		ListView mCardListView = (ListView) view.findViewById(R.id.card_list_view);
		mCardListView.setAdapter(cardAdapter);
		mCardListView.setOnItemClickListener(onCardItemClickListener);
		View.OnClickListener onClickDimissLineChartListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ActionSheet as = (ActionSheet)getViewTransitionManager();
				as.dismiss();
			}
		};
		RelativeLayout topContainerClose = ((RelativeLayout) (view.findViewById(R.id.top_container_close)));
		topContainerClose.setOnClickListener(onClickDimissLineChartListener);
	}

	class CardAdapter extends ModelAdapter<Model> {


		@Override
		protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
			return new CardViewHolder();
		}

		private class CardViewHolder implements ViewHolder<Model> {

			CardViewHolder() {
				view = View.inflate(ContextProvider.getContext(), R.layout.item_card, null);
				nameTextView = (TextView) view.findViewById(R.id.name_text_view);
			}

			private View view;
			private TextView nameTextView;


			@Override
			public void setModel(Model model) {
				nameTextView.setText(model.getString("name"));
			}

			@Override
			public View getView() {
				return view;
			}


		}

	}

	AdapterView.OnItemClickListener onCardItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Model m = mModels.get(position);
			selectCardListener.selectCard(m);
//			DrawCashDialogViewController.this.dismiss();
			ActionSheet as = (ActionSheet)getViewTransitionManager();
			as.dismiss();

		}
	};

}



