package com.tianpingpai.seller.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.seller.model.LayeredProduct;
import com.tianpingpai.widget.BadgeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CategoryAdapter extends BaseAdapter implements OnItemClickListener {

    private LayeredProduct layeredProduct;
    private ProductAdapter productAdapter;

    private LayeredProduct.Category firstCategory;
    private LayeredProduct.SecondCategory secondCategory;

    public ArrayList<ProductModel> getAllProducts() {
        if(layeredProduct != null){
            return layeredProduct.getProducts();
        }
        return null;
    }

    public void setFirstCategory(LayeredProduct.FirstCategory firstCategory) {
        this.firstCategory = firstCategory;
        if (firstCategory.getSecondCategories() != null && firstCategory.getSecondCategories().size() > 0) {
            secondCategory = firstCategory.getSecondCategories().get(0);
            if (productAdapter != null) {
                productAdapter.setModels(secondCategory.getProducts());
            }
        } else {
            secondCategory = null;
            if (productAdapter != null) {
                productAdapter.setModels(firstCategory.getProducts());
            }
        }
        notifyDataSetChanged();
    }

    public void selectFirstCategory(int index) {
        if (layeredProduct != null && !layeredProduct.getCategories().isEmpty()) {
            LayeredProduct.FirstCategory fc = layeredProduct.getCategories().get(index);
            setFirstCategory(fc);
        }
    }

    public void setLayeredProduct(LayeredProduct layeredProduct) {
        this.layeredProduct = layeredProduct;
        if (layeredProduct == null) {
            return;
        }
        if (!layeredProduct.getCategories().isEmpty()) {
            LayeredProduct.FirstCategory fc = layeredProduct.getCategories().get(0);
            setFirstCategory(fc);
        }
        notifyDataSetChanged();
    }

    public void setProductAdapter(ProductAdapter pa) {
        this.productAdapter = pa;
    }

    @Override
    public int getCount() {
        if (layeredProduct == null) {
            return 0;
        }

        if (firstCategory == null) {
            return 0;
        }

        LayeredProduct.FirstCategory fc = (LayeredProduct.FirstCategory) firstCategory;
        return fc.getSecondCategories() == null ? 0 : fc.getSecondCategories().size();
    }

    @Override
    public LayeredProduct.Category getItem(int position) {
        LayeredProduct.FirstCategory fc = (LayeredProduct.FirstCategory) firstCategory;
        return fc.getSecondCategories().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new SecondCategoryViewHolder();
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LayeredProduct.Category xx = getItem(position);
        holder.setModel(xx);
        return holder.view;
    }

    private class ViewHolder {
        View view;

        void setModel(LayeredProduct.Category cm) {

        }
    }

    public void sortItem(LayeredProduct.FirstCategory fc) {
        ArrayList<ProductModel> allProducts = new ArrayList<>();
        if (fc.getId() < 0) {
            allProducts.addAll(fc.getProducts());
        } else {
            for (LayeredProduct.SecondCategory sc : fc.getSecondCategories()) {
                allProducts.addAll(sc.getProducts());
            }
        }
        Collections.sort(allProducts,
                new Comparator<ProductModel>() {
                    @Override
                    public int compare(ProductModel lhs,
                                       ProductModel rhs) {
                        if (lhs.getProductNum() > rhs
                                .getProductNum()) {
                            return -1;
                        } else if (lhs.getProductNum() < rhs
                                .getProductNum()) {
                            return 1;
                        }
                        return 0;
                    }
                });
        productAdapter.setModels(allProducts);
    }

    public void refresh() {
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        LayeredProduct.Category item = getItem(position);
        if (productAdapter != null) {
            LayeredProduct.SecondCategory sc = (LayeredProduct.SecondCategory) item;
            productAdapter.setModels(sc.getProducts());
        }
        secondCategory = (LayeredProduct.SecondCategory) item;
        notifyDataSetChanged();
    }

    private String formatName(String name) {
        return name;
    }

    private class SecondCategoryViewHolder extends ViewHolder {
        private TextView nameTextView;
        private BadgeView badgeView;
        private View selectionView;

        SecondCategoryViewHolder() {
            view = LayoutInflater.from(ContextProvider.getContext()).inflate(
                    R.layout.item_second_category, null);
            view.setTag(this);
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            badgeView = (BadgeView) view.findViewById(R.id.number_badge_view);
            selectionView = view.findViewById(R.id.selection_view);
        }

        void setModel(LayeredProduct.Category cm) {
            LayeredProduct.SecondCategory sc = (LayeredProduct.SecondCategory) cm;

            nameTextView.setText(formatName(sc.getName()));
            if (sc == secondCategory) {
                view.setBackgroundColor(Color.WHITE);
                selectionView.setVisibility(View.VISIBLE);
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
                selectionView.setVisibility(View.INVISIBLE);
            }
            int sum = 0;
            for (ProductModel p : sc.getProducts()) {
                if (p.getProductNum() > 0) {
                    sum++;
                }
            }
            if (sum > 0) {
                badgeView.setText("" + sum);
            } else {
                badgeView.setText("");
            }
        }
    }
}
