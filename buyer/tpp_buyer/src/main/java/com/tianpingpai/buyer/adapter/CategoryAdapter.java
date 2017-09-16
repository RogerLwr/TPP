package com.tianpingpai.buyer.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.model.LayeredProduct;
import com.tianpingpai.buyer.model.LayeredProduct.Category;
import com.tianpingpai.buyer.model.LayeredProduct.FirstCategory;
import com.tianpingpai.buyer.model.LayeredProduct.SecondCategory;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.widget.BadgeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CategoryAdapter extends BaseAdapter implements OnItemClickListener {

    private LayeredProduct layeredProduct;
    private ProductAdapter productAdapter;
    private ListView listView;

    private Category selectedCategory;

    public LayeredProduct getLayeredProduct() {
        return layeredProduct;
    }

    public void setLayeredProduct(LayeredProduct layeredProduct) {
        this.layeredProduct = layeredProduct;

        if (!layeredProduct.getCategories().isEmpty()) {
            FirstCategory fc = layeredProduct.getCategories().get(0);
            ArrayList<ProductModel> allProducts = new ArrayList<>();
            if (fc.getId() < 0) {
                allProducts.addAll(fc.getProducts());
            } else {
                for (SecondCategory sc : fc.getSecondCategories()) {
                    allProducts.addAll(sc.getProducts());
                }
            }
            // TODO
            Collections.sort(allProducts, new Comparator<ProductModel>() {
                @Override
                public int compare(ProductModel lhs, ProductModel rhs) {
                    if (lhs.getProductNum() > rhs.getProductNum()) {
                        return -1;
                    } else if (lhs.getProductNum() < rhs.getProductNum()) {
                        return 1;
                    }
                    return 0;
                }
            });
            productAdapter.setModels(allProducts);
            selectedCategory = fc;
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

        int sum = layeredProduct.getCategories().size();
        for (FirstCategory cm : layeredProduct.getCategories()) {
            if (cm.isOpen()) {
                int subItemsCount = cm.getSecondCategories() == null ? 0 : cm
                        .getSecondCategories().size();
                sum += subItemsCount;
            }
        }
        return sum;
    }

    @Override
    public Category getItem(int position) {
        /*if (position == 0) {
            return null;
        }*/
        int count = 0;
        Category category = null;
        for (FirstCategory cm : layeredProduct.getCategories()) {
            if (count == position) {
                category = cm;
                break;
            } else {
                count++;
                if (cm.isOpen()) {
                    int subItemsCount = cm.getSecondCategories() == null ? 0
                            : cm.getSecondCategories().size();
                    if (count + subItemsCount > position) {
                        int index = position - count;
                        Log.e("xx", "pos:" + position + "index:" + index);
                        category = cm.getSecondCategories().get(index);
                        break;
                    } else {
                        count += subItemsCount;
                    }
                }
            }
        }
        return category;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        Category c = getItem(position);
        if (c instanceof FirstCategory) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            if (getItemViewType(position) == 0) {// TODO
                holder = new FirstCategoryViewHolder();
            } else {
                holder = new SecondCategoryViewHolder();
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category xx = getItem(position);
        holder.setModel(xx);
        return holder.view;
    }

    private class ViewHolder {
        View view;

        void setModel(Category cm) {

        }
    }

    private class FirstCategoryViewHolder extends ViewHolder {

        private TextView nameTextView;
        private ImageView chevronImageView;
        private View dotView;

        FirstCategoryViewHolder() {
            view = LayoutInflater.from(ContextProvider.getContext()).inflate(
                    R.layout.item_category, null);
            view.setTag(this);
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            chevronImageView = (ImageView) view
                    .findViewById(R.id.chevron_image_view);
            dotView = view.findViewById(R.id.dot_view);
        }

        void setModel(Category cm) {
            chevronImageView.setVisibility(View.VISIBLE);
            FirstCategory fc = (FirstCategory) cm;
            if (fc.isOpen()) {
                chevronImageView.setImageResource(R.drawable.chevron_up_gray_151113);

            } else {
                chevronImageView
                        .setImageResource(R.drawable.chevron_down_gray_151113);

            }

//            if (selectedCategory == fc
//                    || (fc.getSecondCategories() != null && fc
//                    .getSecondCategories().contains(selectedCategory))) {
////                selectionView.setVisibility(View.GONE);
//            } else {
////                selectionView.setVisibility(View.GONE);
//            }
            nameTextView.setText(formatName(cm.getName()));
            view.setBackgroundResource(R.color.gray_e9);

            boolean hasProduct = false;
            if (fc.getSecondCategories() != null) {
                for (SecondCategory sc : fc.getSecondCategories()) {
                    for (ProductModel p : sc.getProducts()) {
                        if (p.getProductNum() > 0) {
                            hasProduct = true;
                            break;
                        }
                    }
                }
            } else {
                if (fc.getProducts() != null) {
                    for (ProductModel p : fc.getProducts()) {
                        if (p.getProductNum() > 0) {
                            hasProduct = true;
                            break;
                        }
                    }
                }
            }

            if (hasProduct) {
                dotView.setVisibility(View.VISIBLE);
            } else {
                dotView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void sortItem(FirstCategory fc) {
        ArrayList<ProductModel> allProducts = new ArrayList<>();
        if (fc.getId() < 0) {
            allProducts.addAll(fc.getProducts());
        } else {
//            for (SecondCategory sc : fc.getSecondCategories()) {
//                allProducts.addAll(sc.getProducts());
//            }
//            selectedCategory.getProducts().
//            allProducts.addAll(fc.getSecondCategories().get(0).getProducts());
            allProducts.addAll(selectedCategory.getProducts());
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
        if(layeredProduct != null){
            for (FirstCategory fc : layeredProduct.getCategories()) {
                if (fc.isOpen()) {
                    sortItem(fc);
                    break;
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        Category item = getItem(position);
        selectedCategory = item;
        if (item instanceof FirstCategory) {
            for (FirstCategory fc : layeredProduct.getCategories()) {
                if (fc == item) {
                    fc.setOpen(!fc.isOpen());
                    if (fc.isOpen()) {
                        if(fc.getSecondCategories() != null && fc.getSecondCategories().size()>0){
                            selectedCategory = fc.getSecondCategories().get(0);
                        }
                        sortItem(fc);
                    }
                } else {
                    fc.setOpen(false);
                }
            }
            listView.setSelection(position);
        } else {
            if (productAdapter != null) {
                SecondCategory sc = (SecondCategory) item;
                productAdapter.setModels(sc.getProducts());
                selectedCategory = sc;
            }
        }
        notifyDataSetChanged();
    }

    private String space = ContextProvider.getContext().getString(R.string.sapce);

    private String formatName(String name) {
        if (name.length() == 2) {
            return name.charAt(0) + space + space + space + space + space
                    + space + space + space + name.charAt(1);
        }

        if (name.length() == 3) {
            return name.charAt(0) + space + space + name.charAt(1) + space
                    + space + name.charAt(2);
        }
        return name;
    }

    private class SecondCategoryViewHolder extends ViewHolder {
        private View selectionView;
        private TextView nameTextView;
        private BadgeView badgeView;

        SecondCategoryViewHolder() {
            view = LayoutInflater.from(ContextProvider.getContext()).inflate(
                    R.layout.item_second_category, null);
            view.setTag(this);
            selectionView = view.findViewById(R.id.selection_view);
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            badgeView = (BadgeView) view.findViewById(R.id.number_badge_view);
        }

        void setModel(Category cm) {
            LayeredProduct.SecondCategory sc = (SecondCategory) cm;

//            nameTextView.setText(formatName(sc.getName()) + "");
            nameTextView.setText(sc.getName());
            if (sc == selectedCategory) {
                view.setBackgroundColor(Color.parseColor("#ffffff"));
                nameTextView.setTextColor(Color.parseColor("#0cc486"));
            } else {
                view.setBackgroundColor(Color.parseColor("#f5f5f5"));
                nameTextView.setTextColor(Color.parseColor("#333333"));
            }
            if (selectedCategory == sc) {
                selectionView.setVisibility(View.VISIBLE);
            } else {
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

    public void setListView(ListView lv) {
        this.listView = lv;
    }
}
