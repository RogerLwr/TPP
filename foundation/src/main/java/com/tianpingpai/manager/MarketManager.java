package com.tianpingpai.manager;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.db.MarketDao;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.parser.ListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.utils.SingletonFactory;

import java.util.ArrayList;

public class MarketManager extends ModelManager<ModelEvent, MarketModel> {
	
	public static MarketManager getInstance(){
		return SingletonFactory.getInstance(MarketManager.class);
	}

	private int areaId = -1;

    public void setAreaId(int areaId){
        this.areaId = areaId;
    }

	private ResultListener<ListResult<MarketModel>> listener = new ResultListener<ListResult<MarketModel>>() {
		@Override
		public void onResult(HttpRequest<ListResult<MarketModel>> request,
				ListResult<MarketModel> data) {
			if(!data.isSuccess()){
				return;
			}
			if(currentMarket != null){
				int id = currentMarket.getId();
				for(MarketModel mm:data.getModels()){
					if(mm.getId() == id){
						mm.setDefault(true);
						break;
					}
				}
			}
			markets.clear();
			markets.addAll(data.getModels());
			mMarketDao.save(markets);
			notifyEvent(ModelEvent.OnModelsGet, null);
		}
	};
	
	public void refreshMarkets(){
		String url = ContextProvider.getBaseURL() + "/api/market/list";
		HttpRequest<ListResult<MarketModel>> req = new HttpRequest<>(url,listener);
		req.addParam("pageSize", 1000 + "");
		ListParser<MarketModel> parser = new ListParser<>(MarketModel.class);
		req.setParser(parser);
        if(areaId != -1){
			req.addParam("area_id",areaId + "");
        }
		VolleyDispatcher.getInstance().dispatch(req);
	}

	public void refreshAllMarkets(){
		String url = ContextProvider.getBaseURL() + "/api/market/list";
		HttpRequest<ListResult<MarketModel>> req = new HttpRequest<>(url,listener);
		req.addParam("pageSize", 1000 + "");
		ListParser<MarketModel> parser = new ListParser<>(MarketModel.class);
		req.setParser(parser);
		req.addParam("area_id",areaId + "");
		VolleyDispatcher.getInstance().dispatch(req);
	}
	

	public ArrayList<MarketModel> getMarkets(){
		return markets;
	}
	
	private MarketModel currentMarket;

	public MarketModel getCurrentMarket() {
		return currentMarket;
	}

	public void setCurrentMarket(MarketModel aMarket) {
		this.currentMarket = aMarket;
		for(MarketModel m:markets){
			m.setDefault(false);
		}
		this.currentMarket.setDefault(true);
		mMarketDao.save(markets);
		mMarketDao.save(this.currentMarket);
		notifyEvent(ModelEvent.OnModelUpdate, null);
	}

	public void save(ArrayList<MarketModel> markets){
		mMarketDao.clear();
		mMarketDao.save(markets);
	}

    MarketDao mMarketDao = new MarketDao();
    private ArrayList<MarketModel> markets = new ArrayList<>();

    {
        ArrayList<MarketModel> cachedMarkets = mMarketDao.getAll();
        if(cachedMarkets != null){
            markets.addAll(cachedMarkets);
            for(MarketModel m:markets){
                if(m.isDefault()){
                    currentMarket = m;
                    break;
                }
            }
        }
    }
}
