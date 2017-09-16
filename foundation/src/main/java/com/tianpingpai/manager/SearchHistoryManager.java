package com.tianpingpai.manager;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.db.SearchHistoryDao;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.SearchHistoryModel;
import com.tianpingpai.parser.ListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.utils.SingletonFactory;

import java.util.ArrayList;

public class SearchHistoryManager extends ModelManager<ModelEvent, SearchHistoryModel> {
	
	public static SearchHistoryManager getInstance(){
		return SingletonFactory.getInstance(SearchHistoryManager.class);
	}

	private ResultListener<ListResult<SearchHistoryModel>> listener = new ResultListener<ListResult<SearchHistoryModel>>() {
		@Override
		public void onResult(HttpRequest<ListResult<SearchHistoryModel>> request,
				ListResult<SearchHistoryModel> data) {
			if(!data.isSuccess()){
				return;
			}
			if(currentMarket != null){
				
			}
			markets.clear();
			markets.addAll(data.getModels());
			mSearchHistoryDao.save(markets);
			notifyEvent(ModelEvent.OnModelsGet, null);
		}
	};
	

	public ArrayList<SearchHistoryModel> getMarkets(){
//		return markets;
		return mSearchHistoryDao.getAll();
	}
	
	private SearchHistoryModel currentMarket;

	public SearchHistoryModel getCurrentMarket() {
		return currentMarket;
	}

	public void setCurrentMarket(SearchHistoryModel aMarket) {
		this.currentMarket = aMarket;
		
		mSearchHistoryDao.save(markets);
		mSearchHistoryDao.save(this.currentMarket);
		notifyEvent(ModelEvent.OnModelUpdate, null);
	}

	public void save(ArrayList<SearchHistoryModel> searchHistoryModels){
		mSearchHistoryDao.clear();
		mSearchHistoryDao.save(searchHistoryModels);
	}

	public  void save(SearchHistoryModel searchHistoryModel){
		mSearchHistoryDao.save(searchHistoryModel);
	}

    public SearchHistoryDao mSearchHistoryDao = new SearchHistoryDao();
    private ArrayList<SearchHistoryModel> markets = new ArrayList<>();

    {
        ArrayList<SearchHistoryModel> cachedMarkets = mSearchHistoryDao.getAll();
        if(cachedMarkets != null){
            markets.addAll(cachedMarkets);

        }
    }
}
