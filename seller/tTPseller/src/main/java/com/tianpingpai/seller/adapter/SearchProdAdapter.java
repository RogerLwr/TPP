package com.tianpingpai.seller.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;


public class SearchProdAdapter extends ModelAdapter<ProductModel> {

//    private static final int CHANGED_COLOR = Color.RED;
//    private static final int DEFAULT_COLOR = Color.BLACK;

    private Activity a;

//    private HashMap<Long, Model> changedMap = new HashMap<>();

    /*
    public ArrayList<Model> getChangedModels(){
        ArrayList<Model> arrayList = new ArrayList<>();
        for(Long id:changedMap.keySet()){
            arrayList.add(changedMap.get(id));
        }
        return arrayList;
    }

    public void clearChanged(){
        changedMap.clear();
    }
    */

    @Override
    protected ViewHolder<ProductModel> onCreateViewHolder(
            LayoutInflater inflater) {
        return new ProductViewHolder(inflater);
    }

    public Activity getActivity() {
        return a;
    }

    public void setActivity(Activity a) {
        this.a = a;
    }

    /*
    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        notifyDataSetChanged();
    }

    public boolean editMode = false;
    */

    class ProductViewHolder implements ViewHolder<ProductModel> {
        private Binder binder = new Binder();

        private View view;

        @Binding(id = R.id.name_text_view)
        private TextView nameTextView;


        private ProductModel mModel;

        public ProductViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_search_prod, null);
            binder.bindView(this, view);
        }

        /*
        TextWatcher newPriceChangeListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Model m = changedMap.get(mModel.getId());
                if (m == null) {
                   m = new Model();
                }
                double price = 0;
                try{

                    changedMap.put(mModel.getId(),m);
                }catch(Exception e){
                    e.printStackTrace();
                    changedMap.remove(mModel.getId());
                }

            }
        };
        */

        @Override
        public void setModel(ProductModel model) {
            this.mModel = model;

            nameTextView.setText(mModel.getName());

        }

        @Override
        public View getView() {
            return view;
        }
    }
}
